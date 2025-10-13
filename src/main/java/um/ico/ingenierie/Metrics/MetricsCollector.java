package um.ico.ingenierie.Metrics;

import org.eclipse.jdt.core.dom.*;
import um.ico.ingenierie.Exceptions.NoCompilationUnitExceptions;
import um.ico.ingenierie.Exceptions.NoMetricsDataException;
import um.ico.ingenierie.abstractSource.AbstractSourceAttributs;
import um.ico.ingenierie.abstractSource.AbstractSourceClass;
import um.ico.ingenierie.abstractSource.AbstractSourceMethods;
import um.ico.ingenierie.abstractSource.AbstractSourcePackage;
import um.ico.ingenierie.graph.CallGraph;
import um.ico.ingenierie.graph.EdgeType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MetricsCollector {

    // Attributs légitime
    private CompilationUnit cu;
    private MetricsData metricsData;
    private CallGraph callGraph = new CallGraph();

    //TODO Refactor pour voir si c'est tjrs nécessaire, et faut enlever la logique atomique
    //AtomicInteger numberOfPackage = new AtomicInteger(0);
    AtomicInteger compteurClass = new AtomicInteger(0) ;
    AtomicInteger numberOfLine = new AtomicInteger(0);
    AtomicInteger methodCounter = new AtomicInteger(0);

    // TODO Vérifier l'utilité de ce constructeur vide
    public MetricsCollector() {
    }

    public MetricsCollector(CompilationUnit cu,MetricsData metricsData){
        this.cu = cu;
        this.metricsData = metricsData;
    }
    public MetricsCollector(CompilationUnit cu,MetricsData metricsData, CallGraph callGraph){
        this.cu = cu;
        this.metricsData = metricsData;
        this.callGraph = callGraph;
    }

    public void collectMetrics(){
        // Verification que l'objet n'est pas invoqué bizarrement
        if (this.cu == null){throw new NoCompilationUnitExceptions();}
        if (this.metricsData == null){throw new NoMetricsDataException();}
        AbstractSourcePackage lePaquetSource = new AbstractSourcePackage();
        AbstractSourceClass sourceClass = new AbstractSourceClass(); // Un peu faible et peu résilient, considère qu'il n'y a qu'une classe par .java

        // Comptage des lignes encore ok
        int lastCharacterPosition = this.cu.getStartPosition()+this.cu.getLength() -1;
        int lastLine = this.cu.getLineNumber(lastCharacterPosition);
        sourceClass.setNumberOfLinesInClass(this.numberOfLine.addAndGet(lastLine));



        this.cu.accept(new ASTVisitor() {

            private String currentMethodSignature = null;

            public boolean visit(PackageDeclaration node){
                lePaquetSource.setName(node.getName().toString());
                //numberOfPackage.incrementAndGet();
                return true;
            }

            @Override
            public boolean visit(TypeDeclaration node) {
                sourceClass.setNameOfClass(node.getName().toString());
                compteurClass.incrementAndGet();
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
                methodCounter.incrementAndGet();
                //System.out.println(node.getName()+"\n"+node.parameters()+"\n"+node.getReturnType2());
                List<String> listParameters = new ArrayList<>();
                int numberOfLines = +cu.getLineNumber(node.getBody().getLength());
                sourceClass.addMethod(new AbstractSourceMethods(sourceClass,node.isConstructor(),node.getName().toString(),node.parameters(),node.getReturnType2(),numberOfLines));
                IMethodBinding binding = node.resolveBinding();
                if (binding != null){
                    this.currentMethodSignature = createMethodSignature(binding) ;
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
        });
        this.updateData(sourceClass, lePaquetSource);
    }

    public CompilationUnit getCu() {
        return cu;
    }

    public void setCu(CompilationUnit cu) {
        this.cu = cu;
    }

    public AtomicInteger getCompteurClass() {
        return compteurClass;
    }

    public AtomicInteger getNumberOfLine() {
        return numberOfLine;
    }


    public void updateData(AbstractSourceClass sourceClass, AbstractSourcePackage sourcePackage){
        //Seul chose ancienne tjrs logique
        this.metricsData.numberOfLines.addAndGet(this.numberOfLine.get());

        //TODO Enlever cette logique
        this.metricsData.numberOfClass.addAndGet(this.compteurClass.get());
        this.metricsData.numberOfMethods.addAndGet(this.methodCounter.get());
        sourceClass.setNumberOfMethods(this.methodCounter.get());

        //Approche nouvelle
        sourceClass.setPackageParent(sourcePackage);
        this.metricsData.addClass(sourceClass);

        /*this.lePaquetSource.addClassToPackage(this.cLaClasse);
        this.metricsData.addPackage(this.lePaquetSource); */
    }

    private String createMethodSignature(IMethodBinding binding) {
        if (binding == null) return "unknown.binding";

        StringBuilder signature = new StringBuilder();
        ITypeBinding declaringClass = binding.getDeclaringClass();

        // Protection contre les classes anonymes ou locales qui n'ont pas de nom qualifié
        if (declaringClass == null || declaringClass.getQualifiedName().isEmpty()) {
            return "local.class#" + binding.getName();
        }

        // On utilise l'effacement de type (erasure) pour la classe
        signature.append(declaringClass.getErasure().getQualifiedName());
        signature.append("#");
        signature.append(binding.getName());
        signature.append("(");

        ITypeBinding[] parameters = binding.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            // On utilise aussi l'effacement de type pour chaque paramètre
            signature.append(parameters[i].getErasure().getQualifiedName());
            if (i < parameters.length - 1) {
                signature.append(",");
            }
        }
        signature.append(")");
        return signature.toString();
    }
}
