package um.ico.ingenierie.Api.DTO;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;

public record PaireClassDTO(String classA, String classB, int nbrOfLink, double coupling) {

    public static PaireClassDTO from(PaireClass paireClass){
        return new PaireClassDTO(paireClass.getSignClassA(), paireClass.getSignClassB(), paireClass.getNbrOfLink(), paireClass.getCouplage());
    }
}
