package um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendo;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;

import java.util.ArrayList;
import java.util.List;

public class Dendogramme {

    private List<PaireClass> listCouplin = new ArrayList<PaireClass>();

    public Dendogramme(List<PaireClass> paireClassList){
        this.listCouplin = new ArrayList<>(paireClassList); // On fait bien une copie de la liste, on la référence pas
    }
//
//    public static constructDendo(List<PaireClass> listCoupling){
//
//    }
}
