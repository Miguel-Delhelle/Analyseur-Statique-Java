package um.ico.ingenierie.Analysis.core;

import org.eclipse.jdt.core.dom.*;
import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Analysis.Models.SourceCode.AbstractSourceAttributs;
import um.ico.ingenierie.Analysis.Models.SourceCode.AbstractSourceClass;
import um.ico.ingenierie.Analysis.Models.SourceCode.AbstractSourceMethods;
import um.ico.ingenierie.Analysis.Models.SourceCode.AbstractSourcePackage;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;
import um.ico.ingenierie.Analysis.Models.graph.EdgeType;

import java.util.ArrayList;
import java.util.List;

import static um.ico.ingenierie.Analysis.core.MetricsCollector.createMethodSignature;

public class MyVisitor extends ASTVisitor {

    private String currentMethodSignature = null;

    private AbstractSourcePackage lePaquetSource = new AbstractSourcePackage();
    private AbstractSourceClass sourceClass = new AbstractSourceClass();
    private CompilationUnit cu;
    private MetricsData metricsData;
    private CallGraph callGraph;

    public MyVisitor(MetricsData metricsData, CompilationUnit cu, CallGraph callGraph) {
        this.metricsData = metricsData;
        this.cu = cu;
        this.callGraph = callGraph;
    }

    @Override
    public boolean visit(PackageDeclaration node){
        lePaquetSource.setName(node.getName().toString());
        //numberOfPackage.incrementAndGet();
        return true;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        sourceClass.setNameOfClass(node.getName().toString());
        sourceClass.setPackageParent(lePaquetSource);

        int startPosition = node.getStartPosition();

        int endPosition = startPosition + node.getLength() - 1;

        int startLine = cu.getLineNumber(startPosition);
        int endLine = cu.getLineNumber(endPosition);
        int lineCount = endLine - startLine;

        sourceClass.setNumberOfLinesInClass(lineCount);

        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node){
        String nameOfAttributs = node.fragments().getFirst().toString();
        sourceClass.addAttributs(new AbstractSourceAttributs(sourceClass,node.getType().toString(),nameOfAttributs));
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
                String calleeSignature = createMethodSignature(constructorBinding);

                // On ajoute une arête de la méthode courante vers le constructeur appelé
                callGraph.addEdge(currentMethodSignature, calleeSignature, EdgeType.INSTANTIATION);
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
            sourceClass.addMethod(new AbstractSourceMethods(sourceClass,node.isConstructor(),node.getName().toString(),node.parameters(),node.getReturnType2(),numberOfLines));
            IMethodBinding binding = node.resolveBinding();
            if (binding != null){
                this.currentMethodSignature = createMethodSignature(binding) ;
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
                String calleeSignature = createMethodSignature(calledMethodBinding);
                callGraph.addEdge(currentMethodSignature,calleeSignature, EdgeType.CALL);
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
            if (node.getExpression() instanceof ClassInstanceCreation) {
                ClassInstanceCreation newException = (ClassInstanceCreation) node.getExpression();
                IMethodBinding constructorBinding = newException.resolveConstructorBinding();
                if (constructorBinding != null) {
                    String calleeSignature = createMethodSignature(constructorBinding);
                    callGraph.addEdge(currentMethodSignature, calleeSignature, EdgeType.THROWS);
                }
            }
        }
        return super.visit(node);
    }

//    @Override
//    public void postVisit(ASTNode node) {
//        //this.updateData(this.sourceClass,this.lePaquetSource);
//        super.postVisit(node);
//    }

    @Deprecated(since = "N'as plus aucun sens ici")
    public void updateData(AbstractSourceClass sourceClass, AbstractSourcePackage sourcePackage){
        //Seul chose ancienne tjrs logique

        int lastCharacterPosition = this.cu.getStartPosition()+this.cu.getLength() -1;
        int lastLine = this.cu.getLineNumber(lastCharacterPosition);
        //sourceClass.setNumberOfLinesInClass(this.numberOfLine.addAndGet(lastLine));

        //this.metricsData.numberOfLines.addAndGet(this.numberOfLine.get());

        //TODO Enlever cette logique
        //this.metricsData.getNumberOfClass().addAndGet(this.compteurClass.get());
        //this.metricsData.numberOfMethods.addAndGet(this.methodCounter.get());
        //sourceClass.setNumberOfMethods(this.methodCounter.get());

        //Approche nouvelle
        sourceClass.setPackageParent(sourcePackage);
        this.metricsData.addClass(sourceClass);

        /*this.lePaquetSource.addClassToPackage(this.cLaClasse);
        this.metricsData.addPackage(this.lePaquetSource); */
    }

    public AbstractSourcePackage getLePaquetSource() {
        return lePaquetSource;
    }

    public AbstractSourceClass getSourceClass() {
        return sourceClass;
    }

    public CallGraph getCallGraph() {
        return callGraph;
    }

    //    public int getNumberOfLines(){
//
//    }
}
