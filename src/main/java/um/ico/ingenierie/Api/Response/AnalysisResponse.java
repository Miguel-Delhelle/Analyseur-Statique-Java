package um.ico.ingenierie.Api.Response;

import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;
import um.ico.ingenierie.Api.DTO.GraphDTO;
import um.ico.ingenierie.Api.DTO.MetricsDto;

public record AnalysisResponse(MetricsDto metricsDto, GraphDTO graphDotDTO) {
    public static AnalysisResponse from(MetricsData metricsData, CallGraph graph){

        MetricsDto metricsDto1 = MetricsDto.from(metricsData);
        GraphDTO callGraph = GraphDTO.from(graph);
        return new AnalysisResponse(metricsDto1,callGraph);
    }
}

