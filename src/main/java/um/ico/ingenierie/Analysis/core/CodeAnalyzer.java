package um.ico.ingenierie.Analysis.core;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Analysis.Result.SingleFileAnalysisResult;
import um.ico.ingenierie.Api.Response.AnalysisResponse;
import um.ico.ingenierie.Common.Exceptions.NoCompilationUnitExceptions;
import um.ico.ingenierie.JavaFilesHandler.IJavaFilesHandler;
import um.ico.ingenierie.JavaFilesHandler.JavaFilesHandlerPath;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;

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

    private IJavaFilesHandler javaFilesHandlerPath;

    public CodeAnalyzer() {

    }

    public CodeAnalyzer(String path) throws IOException {
        this.javaFilesHandlerPath = new JavaFilesHandlerPath(path);
    }

    public CodeAnalyzer(IJavaFilesHandler javaFilesHandler) throws IOException {
        this.javaFilesHandlerPath = javaFilesHandler;
    }

    public AnalysisResponse analyze(){

        log.info("Démarrage de l'analyse pour le projet situé à : '{}'", this.javaFilesHandlerPath.getRootPath());
        log.debug("Nombre de fichiers .java trouvés : {}", this.javaFilesHandlerPath.getAllPathJava().size());

        String[] classPath = System.getProperty("java.class.path").split(File.pathSeparator);
        String[] sources = {this.getJavaFilesHandler().getRootPath().toString()};

        log.info("Classpath utilisé : " + Arrays.toString(classPath));
        log.info("Sourcepath utilisé : " + Arrays.toString(sources));
        MetricsData metricsData = new MetricsData();
        CallGraph callGraph = new CallGraph();

        for (Path filePath : this.javaFilesHandlerPath.getAllPathJava()){
            try{
                CompilationUnit cu = initCu(classPath,sources,filePath);
                JdtVisitor visitor = new JdtVisitor(cu);
                cu.accept(visitor);

                SingleFileAnalysisResult singleFileAnalysisResult = visitor.getResult();

                log.info("Analyse terminée.");

                metricsData.addClass(singleFileAnalysisResult.getFoundClass());
                callGraph.addEdges(singleFileAnalysisResult.getFoundEdges());


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return AnalysisResponse.from(metricsData,callGraph);
    }

    private CompilationUnit initCu(String[] classPath,String[] sources,Path filePath) throws IOException {
        ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setEnvironment(classPath,sources,null, true);
        parser.setUnitName(filePath.toString());
        parser.setSource(JavaFilesHandlerPath.getJavaFile(filePath));
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        if (cu == null) {
            log.error("AVERTISSEMENT: Impossible de créer l'AST pour " + filePath);
            throw new NoCompilationUnitExceptions();
        }
        if (cu.getProblems() != null && cu.getProblems().length > 0) {
            log.warn("AVERTISSEMENT: Problèmes de compilation détectés dans " + filePath + ". Les bindings pourraient être incomplets.");
        }
        return cu;
    }
//
//    public MetricsData getMetricsData() {
//        return metricsData;
//    }
//
//    public CallGraph getCallGraph(){
//        return this.callGraph;
//    }

    protected IJavaFilesHandler getJavaFilesHandler() {
        return javaFilesHandlerPath;
    }
}

