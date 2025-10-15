package um.ico.ingenierie.Metrics;

import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import um.ico.ingenierie.Exceptions.NoCompilationUnitExceptions;
import um.ico.ingenierie.Exceptions.NoMetricsDataException;
import um.ico.ingenierie.abstractSource.AbstractSourceAttributs;
import um.ico.ingenierie.abstractSource.AbstractSourceClass;
import um.ico.ingenierie.abstractSource.AbstractSourceMethods;
import um.ico.ingenierie.abstractSource.AbstractSourcePackage;
import um.ico.ingenierie.graph.CallGraph;
import um.ico.ingenierie.graph.EdgeType;
import um.ico.ingenierie.traitementFile.CodeAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * La classe MetricsCollector est pensée pour être instancié dans le CodeAnalyzer
 * Et détruite à la fin d'un fichier analysé par le CodeAnalyzer.
 *
 * Elle a pour principale fonctionnalité d'appeler CompilationUnit et les visiteurs
 * Elle est le coeur de la collecte d'informations du code source rentrée.
 *
 * Elle prend en entrée une CompilationUnit (fourni par le code Analyzer) ainsi
 * Que le MetricsData, le MetricsData correspond au Data sur un code Source.
 *
 * @author Miguel Delhelle
 * @version 1.0
 * */

public class MetricsCollector {



    // Attributs légitime
    private CompilationUnit cu;
    private MetricsData metricsData;
    private CallGraph callGraph = new CallGraph();

    private static final Logger log = LoggerFactory.getLogger(MetricsCollector.class);

    //TODO Refactor pour voir si c'est tjrs nécessaire, et faut enlever la logique atomique
    //AtomicInteger numberOfPackage = new AtomicInteger(0);
//    AtomicInteger compteurClass = new AtomicInteger(0) ;
//    AtomicInteger numberOfLine = new AtomicInteger(0);
//    AtomicInteger methodCounter = new AtomicInteger(0);

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
        if (this.metricsData == null){throw new NoMetricsDataException();} // Un peu faible et peu résilient, considère qu'il n'y a qu'une classe par .java

        MyVisitor visitor = new MyVisitor(this.metricsData, cu, callGraph);
        // Comptage des lignes encore ok
//        int lastCharacterPosition = this.cu.getStartPosition() + this.cu.getLength() - 1;
//        int lastLine = this.cu.getLineNumber(lastCharacterPosition);
//        sourceClass.setNumberOfLinesInClass(this.numberOfLine.addAndGet(lastLine));
        this.cu.accept(visitor);

        AbstractSourcePackage collectedPackage = visitor.getLePaquetSource();
        AbstractSourceClass collectedClass = visitor.getSourceClass();

//        int lastCharacterPosition = this.cu.getStartPosition() + this.cu.getLength() - 1;
//        int lastLine = this.cu.getLineNumber(lastCharacterPosition);

        //collectedClass.setNumberOfLinesInClass(visitor.numberOfLine.addAndGet(lastLine));
        //collectedClass.setNumberOfMethods(visitor.methodCounter.get());
        collectedClass.setPackageParent(collectedPackage);

        this.metricsData.addClass(collectedClass);

    }

    public CompilationUnit getCu() {
        return cu;
    }

    public void setCu(CompilationUnit cu) {
        this.cu = cu;
    }

//    public AtomicInteger getCompteurClass() {
//        return compteurClass;
//    }
//
//    public AtomicInteger getNumberOfLine() {
//        return numberOfLine;
//    }


//    @Deprecated(since = "Priviligié la méthode dans le MyVisitor")
//    public void updateData(AbstractSourceClass sourceClass, AbstractSourcePackage sourcePackage){
//        //Seul chose ancienne tjrs logique
//        this.metricsData.numberOfLines.addAndGet(this.numberOfLine.get());
//
//        //TODO Enlever cette logique
//        //this.metricsData.numberOfClass.addAndGet(this.compteurClass.get());
//        this.metricsData.numberOfMethods.addAndGet(this.methodCounter.get());
//        sourceClass.setNumberOfMethods(this.methodCounter.get());
//
//        //Approche nouvelle
//        sourceClass.setPackageParent(sourcePackage);
//        this.metricsData.addClass(sourceClass);
//
//        /*this.lePaquetSource.addClassToPackage(this.cLaClasse);
//        this.metricsData.addPackage(this.lePaquetSource); */
//    }

    public static String createMethodSignature(IMethodBinding binding) {
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
