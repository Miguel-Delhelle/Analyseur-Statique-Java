package fr.delweb.analyzer.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import fr.delweb.analyzer.Analysis.CodeAnalyzer;
import fr.delweb.analyzer.Analysis.Models.Graph.MacroGraph.Coupling.CouplingGraph;
import fr.delweb.analyzer.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;
import fr.delweb.analyzer.Analysis.Models.Graph.MacroGraph.Dendro.DendroNode;
import fr.delweb.analyzer.Analysis.Models.Graph.MacroGraph.Dendro.Dendrogramme;
import fr.delweb.analyzer.Analysis.Models.Graph.MicroGraph.CallGraph;
import fr.delweb.analyzer.Analysis.Models.MetricsData;
import fr.delweb.analyzer.Analysis.Result.CodeAnalyzerResult;
import fr.delweb.analyzer.Api.DTO.CallGraphDTO;
import fr.delweb.analyzer.Api.DTO.CouplingGraphDTO;
import fr.delweb.analyzer.Api.DTO.MetricsDto;
import fr.delweb.analyzer.Api.Response.AnalysisResponse;
import fr.delweb.analyzer.Common.utils.IcoUtils;
import fr.delweb.analyzer.JavaFilesHandler.IJavaFilesHandler;

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
