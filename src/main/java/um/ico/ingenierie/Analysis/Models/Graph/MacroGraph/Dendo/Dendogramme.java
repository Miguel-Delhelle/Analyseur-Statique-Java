package um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendo;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.CouplingGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Dendogramme {


    public Dendogramme(){
    }


    public static Set<DendoNode> constructDendo(List<PaireClass> listCoupling){
        List<PaireClass> classListTrie = CouplingGraph.triSurCouplingScore(listCoupling);
        DendoNode

    }
}
