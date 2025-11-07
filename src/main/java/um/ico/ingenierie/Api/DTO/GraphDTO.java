package um.ico.ingenierie.Api.DTO;

import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;
import um.ico.ingenierie.Analysis.Models.graph.Edge;

import java.util.Map;
import java.util.Set;

public record GraphDTO(Map<String, Set<Edge>> adjacencyList) {

    /**
     * Factory method pour créer le DTO à partir des résultats de l'analyse.
     * @param callGraph L'objet CallGraph contenant la structure du graphe.
     * @return Une nouvelle instance de GraphDotDto.
     */
    public static GraphDTO from(CallGraph callGraph) {
        return new GraphDTO(callGraph.getAdjacencyList());
    }
}
