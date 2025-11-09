    package um.ico.ingenierie;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import um.ico.ingenierie.Analysis.core.CodeAnalyzer;
import um.ico.ingenierie.JavaFilesHandler.IJavaFilesHandler;
import um.ico.ingenierie.JavaFilesHandler.JavaFilesHandlerPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Le Main, le point d'entrée c'est tout.
 *
 * @author Miguel Delhelle
 * @version 1.0
 * */

@SpringBootApplication
public class AstIngenierieApplication {

    public static void main(String[] args) {
        SpringApplication.run(AstIngenierieApplication.class, args);
    }
}
