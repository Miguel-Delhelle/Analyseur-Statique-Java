package um.ico.ingenierie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import um.ico.ingenierie.traitementFile.CodeAnalyzer;

import java.io.IOException;

@SpringBootApplication
public class AstIngenierieApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(AstIngenierieApplication.class, args);
        CodeAnalyzer codeAnalyzer = new CodeAnalyzer(".");
        System.out.println(codeAnalyzer.getMetricsData().numberOfLines);
	}

}
