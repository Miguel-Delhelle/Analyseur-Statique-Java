package um.ico.ingenierie.Analysis.core;

import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.*;
import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Analysis.Models.SourceCode.*;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;
import um.ico.ingenierie.Analysis.Models.graph.Edge;
import um.ico.ingenierie.Analysis.Models.graph.EdgeType;
import um.ico.ingenierie.Common.utils.MySignature;

import java.util.*;

public class MyVisitor extends ASTVisitor {

    private String currentMethodSignature = null;

    private AbstractSourcePackage localPackage = new AbstractSourcePackage();
    private AbstractSourceClass localClass = new AbstractSourceClass();
    private Map<String, Set<Edge>> localEdge = new HashMap<>();
    private CompilationUnit cu;
    //private MetricsData metricsData;
    //private CallGraph callGraph;

    public MyVisitor(CompilationUnit cu) {
        //this.metricsData = metricsData;
        this.cu = cu;
        //this.callGraph = callGraph;
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
                String calleeSignature = MySignature.createMethodSignature(constructorBinding);

                // On ajoute une arête de la méthode courante vers le constructeur appelé
                //TODO REFAIRE CALLGRAPH
                // callGraph.addEdge(currentMethodSignature, calleeSignature, EdgeType.INSTANTIATION);
                localEdge.computeIfAbsent(calleeSignature, k -> new HashSet<>()).add(new Edge(calleeSignature,EdgeType.INSTANTIATION));
            }
        }
        return super.visit(node);
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
                this.currentMethodSignature = MySignature.createMethodSignature(binding) ;
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
                String calleeSignature = MySignature.createMethodSignature(calledMethodBinding);
                localEdge.computeIfAbsent(currentMethodSignature, k -> new HashSet<>()).add(new Edge(calleeSignature,EdgeType.CALL));
            }
        }

        return true;
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
                    String calleeSignature = MySignature.createMethodSignature(constructorBinding);
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
