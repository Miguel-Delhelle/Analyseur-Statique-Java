package um.ico.ingenierie.Api.Response;

import um.ico.ingenierie.Api.DTO.GraphD3DTO;
import um.ico.ingenierie.Api.DTO.GraphDotDTO;
import um.ico.ingenierie.Api.DTO.MetricsDto;

public record AnalysisResponse(MetricsDto metricsDto, GraphDotDTO graphDotDTO) {
//    public static AnalysisResponse from(MetricsDto metricsDto, GraphDotDTO graphDotDTO){
//    }
}

