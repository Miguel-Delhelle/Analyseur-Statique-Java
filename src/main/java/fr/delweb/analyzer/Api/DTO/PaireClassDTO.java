package fr.delweb.analyzer.Api.DTO;

import fr.delweb.analyzer.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;

public record PaireClassDTO(String classA, String classB, int nbrOfLink, double coupling) {

    public static PaireClassDTO from(PaireClass paireClass){
        return new PaireClassDTO(paireClass.getSignClassA(), paireClass.getSignClassB(), paireClass.getNbrOfLink(), paireClass.getCouplage());
    }
}
