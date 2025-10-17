package um.ico.ingenierie.Analysis.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import um.ico.ingenierie.Analysis.core.CodeAnalyzer;
import um.ico.ingenierie.JavaFilesHandler.IJavaFilesHandler;

import java.io.IOException;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);


   public CodeAnalyzer analyzeProject(IJavaFilesHandler javaFilesHandler) throws IOException{
        log.info("Le service d'analyse est appelé pour", javaFilesHandler);
        CodeAnalyzer result = new CodeAnalyzer(javaFilesHandler);
        return result;
    }

}
