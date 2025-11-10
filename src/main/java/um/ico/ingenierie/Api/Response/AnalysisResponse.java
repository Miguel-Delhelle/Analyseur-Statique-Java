package um.ico.ingenierie.Api.Response;

import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;
import um.ico.ingenierie.Analysis.Service.CouplingService;
import um.ico.ingenierie.Api.DTO.GraphDTO;
import um.ico.ingenierie.Api.DTO.MetricsDto;

public record AnalysisResponse(MetricsDto metricsDto, GraphDTO graphDTO, String basePackage) {
    public static AnalysisResponse from(MetricsData metricsData, CallGraph graph) {

        MetricsDto metricsDto1 = MetricsDto.from(metricsData);
        GraphDTO callGraph = GraphDTO.from(graph);
        String basePackage = metricsData.determineBasePackage();
        System.out.println(callGraph);
        System.out.println(new CouplingService().TreeCoupling(graph));
        return new AnalysisResponse(metricsDto1,callGraph,basePackage);
    }
}

