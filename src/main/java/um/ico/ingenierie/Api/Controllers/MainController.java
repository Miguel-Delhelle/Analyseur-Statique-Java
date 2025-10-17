package um.ico.ingenierie.Api.Controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.ico.ingenierie.Analysis.Service.AnalysisService;

/**
 * Cette classe, est le principale controlleur qui dessert le frontend.
 * Son but est de permettre à l'application web d'interragir avec l'application Java.
 *
 * @author Miguel Delhelle
 * @version 1.0
 */

@RestController
@RequestMapping("/api/analyze")
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private final AnalysisService analysisService;

    public MainController(AnalysisService analysisService){
        this.analysisService = analysisService;
    }

//    @PostMapping("/git")
//    public ResponseEntity<ResponseAnalysis> analyzeGitProject(@RequestParam String uriGit) throws GitAPIException, IOException {
//        CodeAnalyzer result = analysisService.analyzeProject(new JavaFilesHandlerGit(uriGit));
//        //new GraphD3DTO();
//        GraphDotDTO resultatGraphDot = new GraphDotDTO(result.getCallGraph().toDotString(result.getMetricsData().determineBasePackage()));
//        //MetricsDto metricsDto = new MetricsDto(result.getMetricsData());
//
//        //return ResponseEntity.ok(new ResponseAnalysis(result.getMetricsData(),result.getCallGraph().toDotString()));
//    }



}
