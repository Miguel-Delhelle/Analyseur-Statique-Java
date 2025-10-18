package um.ico.ingenierie;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import um.ico.ingenierie.Analysis.core.CodeAnalyzer;
import um.ico.ingenierie.JavaFilesHandler.IJavaFilesHandler;
import um.ico.ingenierie.JavaFilesHandler.JavaFilesHandlerPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Le Main, le poitn d'entrée c'est tout.
 *
 * @author Miguel Delhelle
 * @version 1.0
 * */

@SpringBootApplication
public class AstIngenierieApplication {

	public static void main(String[] args) throws IOException, GitAPIException {
		SpringApplication.run(AstIngenierieApplication.class, args);
        IJavaFilesHandler javaFilesHandler = new JavaFilesHandlerPath("/home/miguel/Projet/X3RSI");
        CodeAnalyzer codeAnalyzer = new CodeAnalyzer(javaFilesHandler);

        String dotRepresentation = codeAnalyzer.getCallGraph().toDotString(codeAnalyzer.getMetricsData().determineBasePackage());

        // 2. Écrire ce contenu dans un fichier "graph.dot"
        Files.writeString(Paths.get("graph.dot"), dotRepresentation);

        String toutleCode = javaFilesHandler.javaToString();

        Files.writeString(Paths.get("ToutLeCode.txt"), toutleCode);

    }

}
