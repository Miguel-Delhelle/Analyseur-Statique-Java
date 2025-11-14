package um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendro;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.CouplingGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;
import um.ico.ingenierie.Common.utils.IcoUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Dendrogramme {


    public Dendrogramme(){
    }


    public static Set<DendroNode> constructDendo(List<PaireClass> listCoupling){
        List<PaireClass> classList = CouplingGraph.triSurCouplingScore(listCoupling);
        Set<String> classSet = IcoUtils.getAllClass_From(listCoupling, true);
        Set<DendroNode> leafs = initLeafs(classSet);
        Set<DendroNode> dendro = new HashSet<>();

        for (DendroNode leaf : leafs){
            String leafStr = leaf.getClassName();

            List<PaireClass> coupleDeCetteLeaf = PaireClass.toutLesCouplesDe(leafStr,classList);
            PaireClass futurNode = clusterProche(coupleDeCetteLeaf,leafStr);

            String leafLaPlusProcheStr = futurNode.getOtherClass(leafStr);
            DendroNode leafLePLusProche = Dendrogramme.getByClassName(leafs,leafLaPlusProcheStr);

            dendro.add(new DendroNode(leaf,leafLePLusProche,futurNode.getCouplage()));
        }

        return dendro;

    }

    public static Set<DendroNode> initLeafs(Set<String> classSet){
        Set<DendroNode> dendro = new HashSet<>();
        for (String strClass: classSet){
            dendro.add(new DendroNode(strClass));
        }
        return dendro;
    }

    /**
     * Cette méthode renvoie la paire de classe avec le plus haut couplage pour une classe donnée
     *
     * @author Miguel Delhelle
     */
    public static PaireClass clusterProche(List<PaireClass> coupleList, String nameOfClass){
        List<PaireClass> toutLesCouplesDe_LaClass = PaireClass.toutLesCouplesDe(nameOfClass,coupleList);
        toutLesCouplesDe_LaClass = CouplingGraph.triSurCouplingScore(toutLesCouplesDe_LaClass);
        return toutLesCouplesDe_LaClass.get(0);
    }

    public static PaireClass clusterProche(List<PaireClass> coupleList, DendroNode leaf){
        String nameOfClass = leaf.getClassName();
        List<PaireClass> toutLesCouplesDe_LaClass = PaireClass.toutLesCouplesDe(nameOfClass,coupleList);
        toutLesCouplesDe_LaClass = CouplingGraph.triSurCouplingScore(toutLesCouplesDe_LaClass);
        return toutLesCouplesDe_LaClass.get(0);
    }

    public static DendroNode getByClassName(Set<DendroNode> leafSet, String nameOfClass){
        for (DendroNode noeudCherche: leafSet){
            if ((noeudCherche.isLeaf()) && (noeudCherche.getClassName().equals(nameOfClass))){
                return noeudCherche;
            }
        }
        return null;
    }

//    public static PaireClass clusterProche(Set<DendroNode> leafSet, DendroNode leaf){
//        String nameOfClass = leaf.getClassName();
//        List<PaireClass> toutLesCouplesDe_LaClass = PaireClass.toutLesCouplesDe(nameOfClass,leafSet);
//        toutLesCouplesDe_LaClass = CouplingGraph.triSurCouplingScore(toutLesCouplesDe_LaClass);
//        return toutLesCouplesDe_LaClass.getFirst();
//    }
    //TODO Peu résiliente, considére que le couple AB n'a pas de plus haut couplage ailleurs type AC, ou BC
}
