package um.ico.ingenierie.Api.DTO;

import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;

public record GraphDotDTO(String dotContent) {

    /**
     * Factory method pour créer le DTO à partir des résultats de l'analyse.
     * @param callGraph L'objet CallGraph contenant la structure du graphe.
     * @param metricsData L'objet MetricsData (nécessaire pour déterminer le package de base).
     * @return Une nouvelle instance de GraphDotDto.
     */
    public static GraphDotDTO from(CallGraph callGraph, MetricsData metricsData, boolean withExternal) {
        String basePackage = metricsData.determineBasePackage();
        String dotString;
        if (withExternal){
            dotString = callGraph.toDotString();
        }else{
            dotString = callGraph.toDotString(basePackage);
        }
        return new GraphDotDTO(dotString);
        //TODO PENSER A RENDRE LA VERSION FILTRE ET SANS FILTRE POSSIBLE (Voir que le programme lui même, et les dépendances, je java etc)
    }
}
