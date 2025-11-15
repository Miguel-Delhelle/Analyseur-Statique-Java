package um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.CouplingGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;
import um.ico.ingenierie.Common.utils.IcoUtils;
import um.ico.ingenierie.Common.utils.PairNormalisation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DendroGramme {

    private static final Logger log = LoggerFactory.getLogger(DendroGramme.class);

    private DendroGramme(){}

    public static DendroNode buildDendroGramme(List<PaireClass> couplingGraph){
        Set<String> classes = IcoUtils.getAllClass_From(couplingGraph, true);
        List<PaireClass> mutableCouplings = new ArrayList<>(couplingGraph);

        Set<DendroNode> cluster = new HashSet<>();
        for (String classe : classes){
            cluster.add(new DendroNode(classe));
        }

        while (cluster.size() > 1){
            log.info("Taille du cluster"+cluster.size());
            DendroNode actif = cluster.iterator().next();
            log.info("Node actif"+actif.getClassName());
            ResultClusterProche clusterProche = clusterProche(actif,mutableCouplings);
            log.info("Résultat clusterProche:" + clusterProche);
            DendroNode clusterProcheNode = getByClassName(cluster,clusterProche.otherName());
            DendroNode newNode = new DendroNode(actif,clusterProcheNode, clusterProche.couplage());

            cluster.remove(actif);
            cluster.remove(clusterProcheNode);
            cluster.add(newNode);

            updateCouplingList(mutableCouplings,actif.getClassName(),clusterProcheNode.getClassName(),newNode.getClassName());
        }

        return cluster.iterator().next();

    }


    // Miguel
//    public static ResultClusterProche clusterProche(DendroNode cluster,List<PaireClass> couplingGraph){
//        String clusterStr = cluster.getClassName();
//        List<PaireClass> filAttente = CouplingGraph.triSurCouplingScore(couplingGraph);
//        ResultClusterProche resultClusterProche = null;
//        PaireClass toRemove = null;
//        for (PaireClass paireClass : filAttente){
//            if (paireClass.contains(clusterStr)){
//                resultClusterProche = new ResultClusterProche(clusterStr, paireClass.getOtherClass(clusterStr),paireClass.getCouplage());
//                toRemove = paireClass;
//                break;
//            }
//        }
//        filAttente.remove(toRemove);
//        return resultClusterProche;
//    }

    //ChatGpt
    public static ResultClusterProche clusterProche(
            DendroNode cluster,
            List<PaireClass> couplingGraph
    ) {
        String clusterName = cluster.getClassName();
        List<PaireClass> sorted = CouplingGraph.triSurCouplingScore(couplingGraph);

        for (PaireClass pair : sorted) {
            if (pair.contains(clusterName)) {
                String other = pair.getOtherClass(clusterName);
                return new ResultClusterProche(clusterName, other, pair.getCouplage());
            }
        }

        return null; // plus propre que retourner un truc incohérent
    }

    private record ResultClusterProche(String firstName, String otherName, double couplage){}


    public static void updateCouplingList(
            List<PaireClass> couplings,
            String c1,
            String c2,
            String mergedName
    ) {
        List<PaireClass> toAdd = new ArrayList<>();

        for (PaireClass p : couplings) {
            if (p.contains(c1) || p.contains(c2)) {
                String other = p.getOtherClass(c1);
                if (other == null) other = p.getOtherClass(c2);

                double newCoupling = p.getCouplage(); // single-link

                String[] ordered = PairNormalisation.normalizePair(mergedName, other);
                toAdd.add(new PaireClass(ordered[0], ordered[1], newCoupling));
            }
        }

        // On retire toutes les anciennes paires
        couplings.removeIf(p -> p.contains(c1) || p.contains(c2));

        // On ajoute les nouvelles
        couplings.addAll(toAdd);
    }




//    public static void updateCouplingList(List<PaireClass> couplingGraph, String clusterAMerged, String clusterBMerged){
//        List<PaireClass> toutLesCouplesAvecA = PaireClass.toutLesCouplesDe(clusterAMerged, couplingGraph);
//        List<PaireClass> toutLesCouplesAvecB = PaireClass.toutLesCouplesDe(clusterBMerged, couplingGraph);
//        Set<PaireClass> toutLesCouplesDeAetB = new HashSet<>();
//        PaireClass ab = null;
//        for (PaireClass paireClass: couplingGraph){
//            if (paireClass.contains(clusterAMerged) && paireClass.contains(clusterBMerged)){
//                ab = paireClass;
//            }
//        }
//
//        for (PaireClass couple: couplingGraph){
//            if (toutLesCouplesAvecA.contains(couple)){
//                couple.merged(ab);
//                //toutLesCouplesDeAetB.add(couple);
//            }
//            if (toutLesCouplesAvecB.contains(couple)){
//                couple.merged(ab);
//                //toutLesCouplesDeAetB.add(couple);
//            }
//        }
////        for (PaireClass paireClass : toutLesCouplesDeAetB){
////            paireClass.merged(ab);
////        }
//        return ;
//
//
//    }

    public static DendroNode getByClassName(Set<DendroNode> clusters, String nameOfClass){
        Set<DendroNode> nodesWithThatClass = new HashSet<>();
        for (DendroNode noeudCherche: clusters){
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

}
