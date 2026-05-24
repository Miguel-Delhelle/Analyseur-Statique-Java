package fr.delweb.analyzer.Analysis.Models.Graph.MicroGraph;

import java.util.*;

/**
 * La classe CallGraph permet de créer un .dot représentant le graphe d'appel
 *
 * @author Miguel Delhelle
 * @version ALPHA_BEFORE_SPRING
 */

public class CallGraph {

    private final Map<String, Set<Edge>> adjacencyList = new HashMap<>();

    public void addEdge(String callerSignature, String calleeSignature, EdgeType type) {
        adjacencyList.computeIfAbsent(callerSignature, k -> new HashSet<>());
        adjacencyList.computeIfAbsent(calleeSignature, k -> new HashSet<>());
        adjacencyList.get(callerSignature).add(new Edge(calleeSignature, type));
    }

    public void addEdges(Map<String, Set<Edge>> newEdges) {
        newEdges.forEach((caller, calleeSet) -> {
            this.adjacencyList.computeIfAbsent(caller, k -> new HashSet<>())
                    .addAll(calleeSet);
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graphe d'Appel :\n");
        for (Map.Entry<String, Set<Edge>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey()).append(" -->\n");
            for (Edge callee : entry.getValue()) {
                sb.append("\t- ").append(callee).append("\n");
            }
        }
        return sb.toString();
    }

    public Map<String, Set<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    public int getNumberOfEdges(){
        int totalEdges = 0;
        for (Set<Edge> edges : this.getAdjacencyList().values()){
            totalEdges = totalEdges + edges.size();
        };
        return totalEdges;
    }

}