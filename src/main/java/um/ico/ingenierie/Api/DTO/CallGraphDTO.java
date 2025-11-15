package um.ico.ingenierie.Api.DTO;

import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.CallGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.Edge;

import java.util.Map;
import java.util.Set;

public record CallGraphDTO(Map<String, Set<Edge>> adjacencyList) {

    /**
     * Factory method pour créer le DTO à partir des résultats de l'analyse.
     * @param callGraph L'objet CallGraph contenant la structure du graphe.
     * @return Une nouvelle instance de GraphDotDto.
     */
    public static CallGraphDTO from(CallGraph callGraph) {
        return new CallGraphDTO(callGraph.getAdjacencyList());
    }
}
