package um.ico.ingenierie.Api.Controllers;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import um.ico.ingenierie.Analysis.Service.AnalysisService;
import um.ico.ingenierie.Api.Request.GitAnalysisRequest;
import um.ico.ingenierie.Api.Response.AnalysisResponse;
import um.ico.ingenierie.JavaFilesHandler.IJavaFilesHandler;
import um.ico.ingenierie.JavaFilesHandler.JavaFilesHandlerGit;
import um.ico.ingenierie.JavaFilesHandler.JavaFilesHandlerZip;

import java.io.IOException;

@RestController
@RequestMapping("/api/analyses")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalysisController {

    private static final Logger log = LoggerFactory.getLogger(AnalysisController.class);
    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/zip")
    public ResponseEntity<AnalysisResponse> analyzeZip(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Requête reçue sur /zip pour le fichier : {}", file.getOriginalFilename());
        IJavaFilesHandler gestionFiles = new JavaFilesHandlerZip(file);
        AnalysisResponse results = analysisService.analyzeProject(gestionFiles);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/git")
    public ResponseEntity<AnalysisResponse> analyzeGit(@RequestBody GitAnalysisRequest request) throws GitAPIException, IOException {
        log.info("Requête reçue sur /git pour l'URL : {}", request.uriGit());
        IJavaFilesHandler gestionFiles = new JavaFilesHandlerGit(request.uriGit());
        AnalysisResponse results = analysisService.analyzeProject(gestionFiles);
        return ResponseEntity.ok(results);
    }


//    @Deprecated(since = "plus utile")
//    private AnalysisResponse createAnalysisResponse(CodeAnalyzer results) {
//        MetricsDto metricsDto = MetricsDto.from(results.getMetricsData());
//        results.getMetricsData().determineBasePackage();
//        GraphDTO graphDto = GraphDTO.from(results.getCallGraph(), results.getMetricsData(),true);
//        return new AnalysisResponse(metricsDto, graphDto);
//    }
}