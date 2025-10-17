package um.ico.ingenierie.Api.Controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import um.ico.ingenierie.Api.DTO.GraphD3DTO;
import um.ico.ingenierie.Api.DTO.MetricsDto;
import um.ico.ingenierie.Core.CodeAnalyzer;
import um.ico.ingenierie.Service.AnalysisService;

/**
 * Cette classe, est le principale controlleur qui dessert le frontend.
 * Son but est de permettre à l'application web d'interragir avec l'application Java.
 *
 * @author Miguel Delhelle
 * @version 1.0
 */
//@RestController
//@RequestMapping("/api/analyze")
//public class MainController {
//
//    private static final Logger log = LoggerFactory.getLogger(MainController.class);
//
//    private final AnalysisService analysisService = new AnalysisService();
//
//    public MainController(AnalysisService analysisService){
//
//    }
//
//    @PostMapping("/metrics/zip")
//    public ResponseEntity<MetricsDto> getMetricsFromZip(@RequestParam("file") MultipartFile file) {
//        log.info("Requête métriques pour le fichier ZIP : {}", file.getOriginalFilename());
//        //if (!isValidZip(file)) return ResponseEntity.badRequest().build();
//
//        try {
//            // 1. Lancer l'analyse via le service
//            CodeAnalyzer results = analysisService.analyzeProject();
//
//            // 2. Transformer le résultat métier en DTO web
//            MetricsDto metricsDto = MetricsDto.from(results.getMetricsData());
//
//            // 3. Renvoyer le DTO
//            return ResponseEntity.ok(metricsDto);
//        } catch (Exception e) {
//            log.error("Échec de l'analyse pour les métriques", e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }


//}
