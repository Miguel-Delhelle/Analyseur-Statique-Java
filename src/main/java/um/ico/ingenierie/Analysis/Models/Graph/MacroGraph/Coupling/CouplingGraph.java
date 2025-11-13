package um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.CallGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.Edge;
import um.ico.ingenierie.Common.utils.IcoUtils;

import java.util.*;

public class CouplingGraph {

    private static final Logger log = LoggerFactory.getLogger(CouplingGraph.class);

    private CouplingGraph(){

    }

    public static List<PaireClass> TreeCoupling(CallGraph callGraph) {

        //Variables d'entrée
        String basePackage = IcoUtils.determineBasePackage(callGraph);
        int totalEdges = callGraph.getNumberOfEdges();
        Map<String,Set<Edge>> adjacencyList = callGraph.getAdjacencyList();

        //Numérateur
        Map<String, PaireClass> lienEntrePairs = new HashMap<>();

        //Dénominateur
        Map<String, Integer> nbrLienEntreToutesLesClasses = new HashMap<>();

        // Algo
        for (Map.Entry<String,Set<Edge> > graph : adjacencyList.entrySet()){
            String callerSignature = graph.getKey(); // Récupération de la clé qui est la signature entière de la classe
            if (basePackage != null && !basePackage.isEmpty() && !callerSignature.startsWith(basePackage)) {
                continue;
            }
            String classMale = IcoUtils.signatureToQualifiedClassName(callerSignature);
            Set<Edge> arretes = graph.getValue();
            int nbrDeLienSortantDeLaClasse = 0;

            for (Edge arrete: arretes){

                String classFemelle = IcoUtils.signatureToQualifiedClassName(arrete.getCalleeSignature());

                //Condition commentée, l'idée initiale était de compter que les liens interne aux programmes,
                // Pas les méthode vers les Spring, Java.utils, etc etc
                // J'ai pris le partie de finalement les incorporer dans le comptage, puisque je me retrouvais avec des situations étrange
                // Ou les petites classes faisant appel une fois à une méthode interne, et 100 fois à des méthode "externes"
                // Se retrouvait avec des couplages à 100%
                // Ce qui est sémantiquement faux.

//                if (basePackage != null && !basePackage.isEmpty() && !arrete.getCalleeSignature().startsWith(basePackage)) {
//                    continue;
//                }

                if (!classFemelle.equals(classMale)){
                    nbrDeLienSortantDeLaClasse++;
                    PaireClass newCouple = new PaireClass(classMale,classFemelle);
                    String currentSignatureOfCouple = newCouple.twoSignature();

                    lienEntrePairs.putIfAbsent(currentSignatureOfCouple, newCouple);

                    lienEntrePairs.get(currentSignatureOfCouple).addOneLink();
                }
            }
            if (nbrLienEntreToutesLesClasses.containsKey(classMale)){
                int valeurActuelle = nbrLienEntreToutesLesClasses.get(classMale);
                nbrLienEntreToutesLesClasses.put(classMale,valeurActuelle+nbrDeLienSortantDeLaClasse);
            }
            else {
                nbrLienEntreToutesLesClasses.put(classMale,nbrDeLienSortantDeLaClasse);
            }
        }

        // Parcours de la Map pour appliquer le taux de couplage.
        for (PaireClass unePaireDeClass: lienEntrePairs.values()){
            unePaireDeClass.setCouplage(nbrLienEntreToutesLesClasses);
        }

        return lienEntrePairs.values().stream().toList();
    }

    public static List<PaireClass> triSurCouplingScore(List<PaireClass> paireClassNonTrie){
        if (paireClassNonTrie == null || paireClassNonTrie.isEmpty()) {
            log.warn("Aucune paire à trier.");
            return Collections.emptyList();
        }

        List<PaireClass> listeTriee = new ArrayList<>(paireClassNonTrie);

        listeTriee.sort(Comparator.comparingDouble(PaireClass::getCouplage).reversed());

        log.info("Tri effectué sur le score de couplage (du plus fort au plus faible).");

        return listeTriee;
    }


}
