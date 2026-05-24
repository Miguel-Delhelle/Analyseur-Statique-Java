package fr.delweb.analyzer.Analysis.Visitor;

import fr.delweb.analyzer.Analysis.Models.SourceCode.*;
import org.eclipse.jdt.core.dom.*;
import um.ico.ingenierie.Analysis.Models.SourceCode.*;
import fr.delweb.analyzer.Analysis.Models.Graph.MicroGraph.Edge;
import fr.delweb.analyzer.Analysis.Models.Graph.MicroGraph.EdgeType;
import fr.delweb.analyzer.Analysis.Result.SingleFileAnalysisResult;
import fr.delweb.analyzer.Common.utils.IcoUtils;

import java.util.*;

/**
 * Visitor implementation using Eclipse JDT.
 * * @deprecated Replaced by {@link SpoonVisitor} since first version release.
 * Spoon provides a better AST abstraction and native dependency resolution
 * (via MavenLauncher), which is critical for accurate analysis in isolated
 * environments like Docker containers.
 */
@Deprecated(since = "1.0", forRemoval= true)
public class JdtVisitor extends ASTVisitor implements IVisitor{

    private String currentMethodSignature = null;

    private AbstractSourcePackage localPackage = new AbstractSourcePackage();
    private AbstractSourceClass localClass = new AbstractSourceClass();
    private Map<String, Set<Edge>> localEdge = new HashMap<>();
    private CompilationUnit cu;

    public JdtVisitor(CompilationUnit cu) {
        this.cu = cu;
    }

    @Override
    public boolean visit(PackageDeclaration node){
        localPackage.setName(node.getName().toString());
        return true;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        localClass.setNameOfClass(node.getName().toString());
        localClass.setPackageParent(localPackage);
        int startPosition = node.getStartPosition();
        int endPosition = startPosition + node.getLength() - 1;
        int startLine = cu.getLineNumber(startPosition);
        int endLine = cu.getLineNumber(endPosition);
        int lineCount = endLine - startLine;
        localClass.setNumberOfLinesInClass(lineCount);

        ITypeBinding binding = node.resolveBinding();
        localClass.setTypeOfClass(determineNodeType(binding));

        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node){
        String nameOfAttributs = node.fragments().get(0).toString();
        localClass.addAttributs(new AbstractSourceAttributs(localClass,node.getType().toString(),nameOfAttributs));
        return true;
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        // On s'assure d'être dans le contexte d'une méthode de notre projet
        if (currentMethodSignature != null) {
            // On récupère le "binding" du constructeur qui est appelé
            IMethodBinding constructorBinding = node.resolveConstructorBinding();
            if (constructorBinding != null) {
                // On utilise notre méthode createMethodSignature pour obtenir la signature du constructeur
                String calleeSignature = IcoUtils.createMethodSignature(constructorBinding);

                // On ajoute une arête de la méthode courante vers le constructeur appelé
                //TODO REFAIRE CALLGRAPH
                // callGraph.addEdge(currentMethodSignature, calleeSignature, EdgeType.INSTANTIATION);
                localEdge.computeIfAbsent(currentMethodSignature, k -> new HashSet<>()).add(new Edge(calleeSignature,EdgeType.INSTANTIATION));
            }
        }
        return false;
    }

    @Override
    public boolean visit(MethodDeclaration node){
       //methodCounter.incrementAndGet();
        //System.out.println(node.getName()+"\n"+node.parameters()+"\n"+node.getReturnType2());
        try {
            List<String> listParameters = new ArrayList<>();
            int numberOfLines = 0;
            numberOfLines = numberOfLines + cu.getLineNumber(node.getBody().getLength());
            localClass.addMethod(new AbstractSourceMethods(localClass,node.isConstructor(),node.getName().toString(),node.parameters(),node.getReturnType2(),numberOfLines));
            IMethodBinding binding = node.resolveBinding();
            if (binding != null){
                this.currentMethodSignature = IcoUtils.createMethodSignature(binding) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    @Override
    public void endVisit(MethodDeclaration node) {
        this.currentMethodSignature = null;
    }
    @Override
    public boolean visit(MethodInvocation node) {
        if (currentMethodSignature != null){
            IMethodBinding calledMethodBinding = node.resolveMethodBinding();
            if (calledMethodBinding != null){
                String calleeSignature = IcoUtils.createMethodSignature(calledMethodBinding);
                localEdge.computeIfAbsent(currentMethodSignature, k -> new HashSet<>()).add(new Edge(calleeSignature,EdgeType.CALL));
            }
        }

        return false;
    }

    @Override
    public boolean visit(ThrowStatement node) {
        // On s'assure d'être dans le contexte d'une méthode de notre projet
        if (currentMethodSignature != null) {
            // L'expression dans un throw est souvent un "new Exception(...)"
            // On vérifie donc si c'est une création d'instance
            if (node.getExpression() instanceof ClassInstanceCreation newException) {
                IMethodBinding constructorBinding = newException.resolveConstructorBinding();
                if (constructorBinding != null) {
                    String calleeSignature = IcoUtils.createMethodSignature(constructorBinding);
                    // TODO
                    //  callGraph.addEdge(currentMethodSignature, calleeSignature, EdgeType.THROWS);
                    localEdge.computeIfAbsent(currentMethodSignature, k -> new HashSet<>()).add(new Edge(calleeSignature,EdgeType.THROWS));
                }
            }
        }
        return super.visit(node);
    }

    public AbstractSourcePackage getLocalPackage() {
        return localPackage;
    }

    public String getCurrentMethodSignature() {
        return currentMethodSignature;
    }

    public AbstractSourceClass getLocalClass() {
        return localClass;
    }

    public Map<String, Set<Edge>> getLocalEdge() {
        return localEdge;
    }

    public CompilationUnit getCu() {
        return cu;
    }

    public SingleFileAnalysisResult getResult(){
        return new SingleFileAnalysisResult(getLocalClass(),getLocalEdge());
    }

    private TypeOfClass determineNodeType(ITypeBinding binding) {
        if (binding.isInterface()) return TypeOfClass.INTERFACE;
        if (binding.isEnum()) return TypeOfClass.ENUM;
        if (binding.isRecord()) return TypeOfClass.RECORD;

        // Vérification pour les exceptions en remontant l'arbre d'héritage
        ITypeBinding superclass = binding.getSuperclass();
        while (superclass != null) {
            if ("java.lang.Throwable".equals(superclass.getQualifiedName())) {
                return TypeOfClass.EXCEPTION;
            }
            superclass = superclass.getSuperclass();
        }

        // Par défaut, c'est une classe standard.
        return TypeOfClass.CLASS;
    }
//
//    public CallGraph getCallGraph() {
//        return callGraph;
//    }
}
