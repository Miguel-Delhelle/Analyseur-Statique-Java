package um.ico.ingenierie.traitementFile;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import um.ico.ingenierie.Metrics.MetricsCollector;
import um.ico.ingenierie.Metrics.MetricsData;
import um.ico.ingenierie.graph.CallGraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
/**
 * Le CodeAnalyzer a pour but de parcourir une arborescence de fichiers donnée par le JavaFilesHandler
 * et d'exécuter une Analyze, elle prend en entrée un path, si il n'est pas renseigné il execute sur lui même.
 *
 * Il contient un callGraph permettant d'avoir le graphe d'appel.
 * Ainsi qu'un metricsData correspondant
 *
 * @author Miguel Delhelle
 * @version 1.0
 * */

public class CodeAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(CodeAnalyzer.class);

    private JavaFilesHandler javaFilesHandler = new JavaFilesHandler();
    private List<Path> javaPathList = this.javaFilesHandler.getAllPathJava();
    private MetricsData metricsData = new MetricsData();
    private CallGraph callGraph = new CallGraph();

    public CodeAnalyzer() throws IOException {

    }

    public CodeAnalyzer(String path) throws IOException {
        this.javaFilesHandler = new JavaFilesHandler(path);
        this.javaPathList = this.javaFilesHandler.getAllPathJava();
        this.analyze();
    }

    public void analyze(){

        log.info("Démarrage de l'analyse pour le projet situé à : '{}'", this.javaFilesHandler.getChemin());
        log.debug("Nombre de fichiers .java trouvés : {}", this.javaPathList.size());

        String[] classPath = System.getProperty("java.class.path").split(File.pathSeparator);
        String[] sources = {this.getJavaFilesHandler().getChemin().toString()};

        System.out.println("Classpath utilisé : " + Arrays.toString(classPath));
        System.out.println("Sourcepath utilisé : " + Arrays.toString(sources));

        for (Path filePath : this.javaPathList){
            try{
                ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
                parser.setResolveBindings(true);
                parser.setKind(ASTParser.K_COMPILATION_UNIT);
                parser.setEnvironment(classPath,sources,null, true);

                parser.setUnitName(filePath.toString());

                parser.setSource(JavaFilesHandler.getJavaFile(filePath));

                CompilationUnit cu = (CompilationUnit) parser.createAST(null);

                if (cu == null) {
                    log.error("AVERTISSEMENT: Impossible de créer l'AST pour " + filePath);
                    continue; // On ne bloque pas tout, on passe au fichier suivant
                }

                if (cu.getProblems() != null && cu.getProblems().length > 0) {
                    log.warn("AVERTISSEMENT: Problèmes de compilation détectés dans " + filePath + ". Les bindings pourraient être incomplets.");
                }


                MetricsCollector metricsCollector = new MetricsCollector(cu, this.metricsData, this.callGraph);
                metricsCollector.collectMetrics();
                //System.out.println(this.getMetricsCollector().getCompteurClass().get());

                log.info("Analyse terminée.");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public MetricsData getMetricsData() {
        return metricsData;
    }

    public CallGraph getCallGraph(){
        return this.callGraph;
    }

    public JavaFilesHandler getJavaFilesHandler() {
        return javaFilesHandler;
    }
}

