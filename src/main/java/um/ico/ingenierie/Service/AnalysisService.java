package um.ico.ingenierie.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import um.ico.ingenierie.Analysis.CodeAnalyzer;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.CouplingGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendro.DendroNode;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendro.Dendrogramme;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.CallGraph;
import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Analysis.Result.CodeAnalyzerResult;
import um.ico.ingenierie.Api.DTO.CallGraphDTO;
import um.ico.ingenierie.Api.DTO.CouplingGraphDTO;
import um.ico.ingenierie.Api.DTO.MetricsDto;
import um.ico.ingenierie.Api.Response.AnalysisResponse;
import um.ico.ingenierie.Common.utils.IcoUtils;
import um.ico.ingenierie.JavaFilesHandler.IJavaFilesHandler;

import java.io.IOException;
import java.util.List;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);


   public AnalysisResponse analyzeProject(IJavaFilesHandler javaFilesHandler) throws IOException{
        log.info("Le service d'analyse est appelé pour", javaFilesHandler);
        //System.out.println(javaFilesHandler.javaToString());
        CodeAnalyzer ca = new CodeAnalyzer(javaFilesHandler);
       CodeAnalyzerResult caResult = ca.analyze();

       MetricsData metricsData = caResult.metricsData();
       CallGraph callGraph = caResult.callGraph();

       List<PaireClass> arbreCouplageNoDTO = CouplingGraph.CouplingGraph(callGraph);


       DendroNode rootDendro = Dendrogramme.construct(arbreCouplageNoDTO);
       String basePackage = IcoUtils.determineBasePackage(arbreCouplageNoDTO);

       //init DTO
       MetricsDto metricsDto = MetricsDto.from(metricsData);
       CallGraphDTO callGraphDTO = CallGraphDTO.from(callGraph);
       CouplingGraphDTO couplingGraphDTO = CouplingGraphDTO.from(arbreCouplageNoDTO);

       System.out.println(IcoUtils.getAllClass_From(arbreCouplageNoDTO,true));
       return new AnalysisResponse(metricsDto,callGraphDTO,basePackage,couplingGraphDTO,rootDendro);
    }

}
