package um.ico.ingenierie.Api.Response;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.PaireClass;
import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.CallGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.CouplingService;
import um.ico.ingenierie.Api.DTO.GraphDTO;
import um.ico.ingenierie.Api.DTO.MetricsDto;
import um.ico.ingenierie.Api.DTO.TreeCouplingDto;

import java.util.Map;

public record AnalysisResponse(MetricsDto metricsDto, GraphDTO graphDTO, String basePackage, TreeCouplingDto treeCoupling) {
    public static AnalysisResponse from(MetricsData metricsData, CallGraph graph) {

        MetricsDto metricsDto1 = MetricsDto.from(metricsData);
        GraphDTO callGraph = GraphDTO.from(graph);
        String basePackage = metricsData.determineBasePackage();
        //Map<String, PaireClass> treeCouplingMap = CouplingService.TreeCoupling(graph) ;
        TreeCouplingDto treeCoupling = TreeCouplingDto.from(graph);

        return new AnalysisResponse(metricsDto1,callGraph,basePackage,treeCoupling);
    }
}

