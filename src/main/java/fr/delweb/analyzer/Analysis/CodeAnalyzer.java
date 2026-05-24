package fr.delweb.analyzer.Analysis;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;
import fr.delweb.analyzer.Analysis.Models.MetricsData;
import fr.delweb.analyzer.Analysis.Result.CodeAnalyzerResult;
import fr.delweb.analyzer.Analysis.Result.SingleFileAnalysisResult;
import fr.delweb.analyzer.Analysis.Visitor.IVisitor;
import fr.delweb.analyzer.Analysis.Visitor.SpoonVisitor;
import fr.delweb.analyzer.Common.Exceptions.NoCompilationUnitExceptions;
import fr.delweb.analyzer.JavaFilesHandler.IJavaFilesHandler;
import fr.delweb.analyzer.JavaFilesHandler.JavaFilesHandlerPath;
import fr.delweb.analyzer.Analysis.Models.Graph.MicroGraph.CallGraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

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
    private IJavaFilesHandler filesHandler;
    public CodeAnalyzer() {

    }

    public CodeAnalyzer(String path) throws IOException {
        this.filesHandler = new JavaFilesHandlerPath(path);
    }

    public CodeAnalyzer(IJavaFilesHandler javaFilesHandler) {
        this.filesHandler = javaFilesHandler;
    }

    public CodeAnalyzerResult analyze() {

        log.info("Démarrage de l'analyse pour le projet situé à : '{}'", this.filesHandler.getRootPath());
        log.debug("Nombre de fichiers .java trouvés : {}", this.filesHandler.getAllPathJava().size());

        String[] classPath = System.getProperty("java.class.path").split(File.pathSeparator);
        String[] sources = {this.getFilesHandler().getRootPath().toString()};

        log.info("Classpath utilisé : " + Arrays.toString(classPath));
        log.info("Sourcepath utilisé : " + Arrays.toString(sources));
        MetricsData metricsData = new MetricsData();
        CallGraph callGraph = new CallGraph();

        for (Path filePath : this.filesHandler.getAllPathJava()){
                IVisitor visitor = null;
                    try {
                        Launcher launcher = new Launcher();
                        log.info("Launcher Spoon démarée ");
                        launcher.addInputResource(filePath.toString());
                        log.info("add Input Spoon démarée ");
                        launcher.getEnvironment().setNoClasspath(true);
                        log.info("Class Path démarée ");
                        CtModel model = launcher.buildModel();
                        log.info("Build Model Spoon démarée ");
                        CtType<?> type = model.getAllTypes().stream().findFirst().orElse(null);
                        log.info("Contexte Spoon démarée ");
                        visitor = new SpoonVisitor();
                        ((SpoonVisitor) visitor).scan(type);
                    }catch(Exception e){
                            e.printStackTrace();
                        }
                SingleFileAnalysisResult singleFileAnalysisResult = visitor.getResult();
                log.info("Analyse du fichier "+filePath.toString()+"terminée.");
                metricsData.addClass(singleFileAnalysisResult.getFoundClass());
                callGraph.addEdges(singleFileAnalysisResult.getFoundEdges());
        }
        return new CodeAnalyzerResult(metricsData,callGraph);
        //return AnalysisResponse.from(metricsData,callGraph);
    }

    private CompilationUnit initCu(String[] classPath,String[] sources,Path filePath) throws NoCompilationUnitExceptions, IOException {
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

    protected IJavaFilesHandler getFilesHandler() {
        return filesHandler;
    }
}

