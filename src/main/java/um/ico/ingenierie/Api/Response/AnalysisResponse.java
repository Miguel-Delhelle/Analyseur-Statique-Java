package um.ico.ingenierie.Api.Response;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendro.Dendrogramme;
import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.CallGraph;
import um.ico.ingenierie.Api.DTO.GraphCouplingDTO;
import um.ico.ingenierie.Api.DTO.GraphDTO;
import um.ico.ingenierie.Api.DTO.MetricsDto;

public record AnalysisResponse(MetricsDto metricsDto, GraphDTO graphDTO, String basePackage, GraphCouplingDTO treeCoupling) {
    public static AnalysisResponse from(MetricsData metricsData, CallGraph graphAppel) {

        MetricsDto metricsDto1 = MetricsDto.from(metricsData);
        GraphDTO callGraph = GraphDTO.from(graphAppel);
        String basePackage = metricsData.determineBasePackage();
        //Map<String, PaireClass> treeCouplingMap = CouplingService.TreeCoupling(graphAppel) ;

        GraphCouplingDTO treeCoupling = GraphCouplingDTO.from(graphAppel);


        return new AnalysisResponse(metricsDto1,callGraph,basePackage,treeCoupling);
    }
}

