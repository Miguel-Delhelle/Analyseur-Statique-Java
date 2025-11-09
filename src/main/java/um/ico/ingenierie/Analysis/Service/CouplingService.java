package um.ico.ingenierie.Analysis.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;
import um.ico.ingenierie.Analysis.Models.graph.Edge;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class CouplingService {

    private static final Logger log = LoggerFactory.getLogger(CouplingService.class);

    public double TauxDeCouplage(CallGraph callGraph, String classA, String classB){

        //Variables d'entrée
        int totalEdges = callGraph.getNumberOfEdges();
        Map<String,Set<Edge>> adjacencyList = callGraph.getAdjacencyList();

        // Algo
        
        Set<Edge> appelClassA = adjacencyList.get(classA);
        Set<Edge> appelClassB = adjacencyList.get(classB);

        return 0;
        //TODO A FAIRE !!
        // Implémenter un Algorithme capable de parcourir tout la liste d'adjence de CallGraph
        // Il faut qu'on récupère chaque appel de A à B et B à A et qu'on les divise par NumberOfEdges
    }














    private String getClassNameFromSignature(String signature) {
        if (signature == null || !signature.contains("#")) {
            return ""; // Retourne une chaîne vide si le format est invalide.
        }
        return signature.split("#")[0];
    }
}
