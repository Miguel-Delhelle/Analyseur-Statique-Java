package fr.delweb.analyzer.Analysis.Models.Graph.MacroGraph.Dendro;

import fr.delweb.analyzer.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;
import fr.delweb.analyzer.Common.utils.IcoUtils;

import java.util.*;

public final class Dendrogramme {

    private Dendrogramme() {} // Classe non instanciable

    /**
     * Construit le dendrogramme complet à partir d'une liste de couplages.
     *
     * @param allCouplings La liste de toutes les {@link PaireClass} calculées pour l'application.
     * @return Le {@link DendroNode} racine de l'arbre hiérarchique complet.
     */
    public static DendroNode construct(List<PaireClass> allCouplings) {
        // 1. PRÉPARATION : Trier les couplages du plus FORT au plus FAIBLE.
        List<PaireClass> sortedCouplings = new ArrayList<>(allCouplings);
        sortedCouplings.sort(Comparator.comparingDouble(PaireClass::getCouplage).reversed());

        // 2. INITIALISATION : Créer un cluster-feuille pour chaque classe.
        Set<String> allClassNames = IcoUtils.getAllClass_From(allCouplings, true);
        Set<DendroNode> activeClusters = new HashSet<>();
        for (String className : allClassNames) {
            activeClusters.add(new DendroNode(className));
        }

        // 3. BOUCLE DE FUSION : Continuer tant qu'il y a plus d'un cluster.
        while (activeClusters.size() > 1) {
            PaireClass bestPairToMerge = null;
            DendroNode cluster1 = null;
            DendroNode cluster2 = null;

            // 4. TROUVER LA MEILLEURE FUSION VALIDE
            for (PaireClass currentPair : sortedCouplings) {
                cluster1 = findClusterContaining(activeClusters, currentPair.getSignClassA());
                cluster2 = findClusterContaining(activeClusters, currentPair.getSignClassB());

                if (cluster1 != null && cluster2 != null && !cluster1.equals(cluster2)) {
                    bestPairToMerge = currentPair;
                    break;
                }
            }

            if (bestPairToMerge == null) {
                break;
            }

            // 5. EFFECTUER LA FUSION
            activeClusters.remove(cluster1);
            activeClusters.remove(cluster2);

            // ======================================================================
            // === LA CORRECTION FINALE ET CRUCIALE EST ICI ===
            // ======================================================================
            // La hauteur n'est pas le couplage, mais la "distance".
            // On la calcule comme l'inverse de la similarité (couplage).
            double hauteur = 1.0 - bestPairToMerge.getCouplage();

            DendroNode mergedCluster = new DendroNode(cluster1, cluster2, hauteur);
            // ======================================================================

            activeClusters.add(mergedCluster);

            // On peut retirer la paire utilisée pour optimiser légèrement les tours suivants.
            sortedCouplings.remove(bestPairToMerge);
        }

        if (activeClusters.isEmpty()) {
            return null;
        }
        return activeClusters.iterator().next();
    }

    /**
     * Méthode utilitaire pour trouver le cluster actif qui contient une classe donnée.
     */
    private static DendroNode findClusterContaining(Set<DendroNode> clusters, String className) {
        for (DendroNode cluster : clusters) {
            if (cluster.getClassContenu().contains(className)) {
                return cluster;
            }
        }
        return null;
    }
}