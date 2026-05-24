package fr.delweb.analyzer.Api.Response;

import fr.delweb.analyzer.Analysis.Models.Graph.MacroGraph.Dendro.DendroNode;
import fr.delweb.analyzer.Api.DTO.CouplingGraphDTO;
import fr.delweb.analyzer.Api.DTO.CallGraphDTO;
import fr.delweb.analyzer.Api.DTO.MetricsDto;

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

