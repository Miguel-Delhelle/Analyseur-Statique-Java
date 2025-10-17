package um.ico.ingenierie.Analysis.core;

import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Common.Exceptions.NoCompilationUnitExceptions;
import um.ico.ingenierie.Common.Exceptions.NoMetricsDataException;
import um.ico.ingenierie.Analysis.Models.SourceCode.AbstractSourceClass;
import um.ico.ingenierie.Analysis.Models.SourceCode.AbstractSourcePackage;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;

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

    private CompilationUnit cu;
    private MetricsData metricsData;
    private CallGraph callGraph = new CallGraph();

    private static final Logger log = LoggerFactory.getLogger(MetricsCollector.class);

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
        if (this.cu == null){throw new NoCompilationUnitExceptions();}
        if (this.metricsData == null){throw new NoMetricsDataException();} // Un peu faible et peu résilient, considère qu'il n'y a qu'une classe par .java

        MyVisitor visitor = new MyVisitor(this.metricsData, cu, callGraph);

        this.cu.accept(visitor);

        AbstractSourcePackage collectedPackage = visitor.getLePaquetSource();
        AbstractSourceClass collectedClass = visitor.getSourceClass();

        collectedClass.setPackageParent(collectedPackage);

        this.metricsData.addClass(collectedClass);

    }

    public CompilationUnit getCu() {
        return cu;
    }

    public void setCu(CompilationUnit cu) {
        this.cu = cu;
    }

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
