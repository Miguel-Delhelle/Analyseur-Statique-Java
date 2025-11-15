package um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendro;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.CouplingGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;
import um.ico.ingenierie.Common.utils.IcoUtils;

import java.util.*;

public class Dendrogramme {

    List<PaireClass> coupleList;

    public Dendrogramme(){
    }

    public Dendrogramme(List<PaireClass> coupleList){
        CouplingGraph.triSurCouplingScore(coupleList);
    }


    public static DendroNode constructDendo(List<PaireClass> coupleListFromGraph){

        List<PaireClass> listCoupling = new ArrayList<>(coupleListFromGraph);

        Set<String> strClass = IcoUtils.getAllClass_From(listCoupling, true);
        //HashMap<String,DendroNode> leafs = new HashMap<>();
        Set<DendroNode> clusters = new HashSet<>();
        for (String classStr: strClass){
            DendroNode leaf = new DendroNode(classStr);
            //leafs.put(classStr,leaf);
            clusters.add(leaf);
        }

        while (clusters.size() > 1){

            PaireClass tmpForNode = listCoupling.get(0);
            DendroNode childA = Dendrogramme.getByClassName(clusters,tmpForNode.getSignClassA());
            DendroNode childB = Dendrogramme.getByClassName(clusters, tmpForNode.getSignClassB());
            DendroNode newNode = new DendroNode(childA,childB,tmpForNode.getCouplage());
            listCoupling.remove(0);


            clusters.remove(childA);
            clusters.remove(childB);
            clusters.add(newNode);



        }

        return clusters.iterator().next();

    }

    public static Set<DendroNode> initLeafs(List<PaireClass> classList){
        Set<String> classSet = IcoUtils.getAllClass_From(classList, true);
        Set<DendroNode> nodeSet = new HashSet<>();
        for (String classStr : classSet){
            nodeSet.add(new DendroNode(classStr));
        }
        return nodeSet;
    }

    /**
     * Cette méthode renvoie la paire de classe avec le plus haut couplage pour une classe donnée
     *
     * @author Miguel Delhelle
     */
    public PaireClass clusterProche(List<PaireClass> coupleList, String nameOfClass){
        List<PaireClass> toutLesCouplesDe_LaClass = PaireClass.toutLesCouplesDe(nameOfClass,coupleList);
        if (toutLesCouplesDe_LaClass.isEmpty()){return null;}
        toutLesCouplesDe_LaClass = CouplingGraph.triSurCouplingScore(toutLesCouplesDe_LaClass);
        return toutLesCouplesDe_LaClass.get(0);
    }

//    public static PaireClass clusterProche(Set<DendroNode> coupleList, DendroNode node){
//
//    }

    public PaireClass clusterProche(Set<DendroNode> node){
        PaireClass nodeACreer = CouplingGraph.triSurCouplingScore(coupleList).get(0);
        coupleList.remove(0);
        return nodeACreer;

    }

    public static DendroNode getByClassName(Set<DendroNode> leafSet, String nameOfClass){
        Set<DendroNode> nodesWithThatClass = new HashSet<>();
        for (DendroNode noeudCherche: leafSet){
            if (noeudCherche.containsClass(nameOfClass)){
                nodesWithThatClass.add(noeudCherche);
            }
        }
        DendroNode minCouplingNode = null;
        double minCoupling = Double.MAX_VALUE;

        for (DendroNode node : nodesWithThatClass) {
            if (node.getHauteurCoupling() < minCoupling) {
                minCoupling = node.getHauteurCoupling();
                minCouplingNode = node;
            }
        }


        return minCouplingNode;
    }

    // SI LA METHODE RENVOIE NULL APPELLEE
//
//    public static DendroNode getByClassNameR(Set<DendroNode> activeCluster, String nameOfClass) {
//
//        if (activeCluster.contains(
//
//    }


//    public static PaireClass clusterProche(Set<DendroNode> leafSet, DendroNode leaf){
//        String nameOfClass = leaf.getClassName();
//        List<PaireClass> toutLesCouplesDe_LaClass = PaireClass.toutLesCouplesDe(nameOfClass,leafSet);
//        toutLesCouplesDe_LaClass = CouplingGraph.triSurCouplingScore(toutLesCouplesDe_LaClass);
//        return toutLesCouplesDe_LaClass.getFirst();
//    }
    //TODO Peu résiliente, considére que le couple AB n'a pas de plus haut couplage ailleurs type AC, ou BC
}
