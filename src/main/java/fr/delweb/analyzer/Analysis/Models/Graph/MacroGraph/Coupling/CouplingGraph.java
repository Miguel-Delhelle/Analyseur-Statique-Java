package fr.delweb.analyzer.Analysis.Models.Graph.MacroGraph.Coupling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.delweb.analyzer.Analysis.Models.Graph.MicroGraph.CallGraph;
import fr.delweb.analyzer.Analysis.Models.Graph.MicroGraph.Edge;
import fr.delweb.analyzer.Common.utils.IcoUtils;

import java.util.*;

public class CouplingGraph {

    private static final Logger log = LoggerFactory.getLogger(CouplingGraph.class);

    private CouplingGraph(){

    }

    public static List<PaireClass> CouplingGraph(CallGraph callGraph) {

        //Variables d'entrée
        String basePackage = IcoUtils.determineBasePackage(callGraph);
        //int totalEdges = callGraph.getNumberOfEdges();
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

                if (basePackage != null && !basePackage.isEmpty() && !arrete.getCalleeSignature().startsWith(basePackage)) {
                    continue;
                }

                if (!classFemelle.equals(classMale)){
                    nbrDeLienSortantDeLaClasse++;
                    PaireClass newCouple = new PaireClass(classMale,classFemelle);
                    String currentSignatureOfCouple = newCouple.twoSignature();

                    lienEntrePairs.putIfAbsent(currentSignatureOfCouple, newCouple);
                    if (nbrLienEntreToutesLesClasses.containsKey(classMale)){
                int valeurActuelle = nbrLienEntreToutesLesClasses.get(classMale);
                nbrLienEntreToutesLesClasses.put(classMale,valeurActuelle+nbrDeLienSortantDeLaClasse);
            }
            else {
                nbrLienEntreToutesLesClasses.put(classMale,nbrDeLienSortantDeLaClasse);
            }

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
