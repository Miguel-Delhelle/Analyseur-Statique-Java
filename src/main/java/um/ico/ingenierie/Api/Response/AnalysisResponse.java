package um.ico.ingenierie.Api.Response;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendro.DendroNode;
import um.ico.ingenierie.Api.DTO.CouplingGraphDTO;
import um.ico.ingenierie.Api.DTO.CallGraphDTO;
import um.ico.ingenierie.Api.DTO.MetricsDto;

public record AnalysisResponse(MetricsDto metricsDto, CallGraphDTO graphDTO, String basePackage, CouplingGraphDTO treeCoupling, DendroNode rootDendro) {
//    public static AnalysisResponse from(MetricsData metricsData, CallGraph graphAppel) {
//
//        MetricsDto metricsDto1 = MetricsDto.from(metricsData);
//        GraphDTO callGraph = GraphDTO.from(graphAppel);
//        String basePackage = metricsData.determineBasePackage();
//        //Map<String, PaireClass> treeCouplingMap = CouplingService.TreeCoupling(graphAppel) ;
//
//        GraphCouplingDTO treeCoupling = GraphCouplingDTO.from(graphAppel);
//
//
//        return new AnalysisResponse(metricsDto1,callGraph,basePackage,treeCoupling);
//    }
}

