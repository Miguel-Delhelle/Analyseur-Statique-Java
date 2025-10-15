package um.ico.ingenierie.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import um.ico.ingenierie.traitementFile.CodeAnalyzer;

import java.io.IOException;

public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);


    public CodeAnalyzer analyzeProjectFromPath(String projectPath) throws IOException{
        log.info("Le service d'analyse est appelé pour", projectPath);
        CodeAnalyzer result = new CodeAnalyzer(projectPath);
        return result;
    }

}
