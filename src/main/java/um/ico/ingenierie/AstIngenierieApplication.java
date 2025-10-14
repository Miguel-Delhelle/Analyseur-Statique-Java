package um.ico.ingenierie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import um.ico.ingenierie.traitementFile.CodeAnalyzer;

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

	public static void main(String[] args) throws IOException {
		SpringApplication.run(AstIngenierieApplication.class, args);
        CodeAnalyzer codeAnalyzer = new CodeAnalyzer(".");

        String dotRepresentation = codeAnalyzer.getCallGraph().toDotString(codeAnalyzer.getMetricsData().determineBasePackage());

        // 2. Écrire ce contenu dans un fichier "graph.dot"
        Files.writeString(Paths.get("graph.dot"), dotRepresentation);

        String toutleCode = codeAnalyzer.getJavaFilesHandler().javaToString();

        Files.writeString(Paths.get("ToutLeCode.txt"), toutleCode);

    }

}
