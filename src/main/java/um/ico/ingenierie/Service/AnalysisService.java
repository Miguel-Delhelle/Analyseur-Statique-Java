package um.ico.ingenierie.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import um.ico.ingenierie.Core.CodeAnalyzer;
import um.ico.ingenierie.JavaFilesHandler.IJavaFilesHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipInputStream;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);


   public CodeAnalyzer analyzeProject(IJavaFilesHandler javaFilesHandler) throws IOException{
        log.info("Le service d'analyse est appelé pour", javaFilesHandler);
        CodeAnalyzer result = new CodeAnalyzer(javaFilesHandler);
        return result;
    }

}
