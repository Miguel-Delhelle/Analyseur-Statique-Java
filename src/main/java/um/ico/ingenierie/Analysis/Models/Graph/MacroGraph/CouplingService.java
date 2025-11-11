package um.ico.ingenierie.Analysis.Models.Graph.MacroGraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.CallGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.Edge;
import um.ico.ingenierie.Common.utils.IcoUtils;

import java.util.*;

public class CouplingService {

    private static final Logger log = LoggerFactory.getLogger(CouplingService.class);

    private CouplingService(){

    }

    public static Map<String,PaireClass> TreeCoupling(CallGraph callGraph) {

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
                if (basePackage != null && !basePackage.isEmpty() && !arrete.getCalleeSignature().startsWith(basePackage)) {
                    continue;
                }

                if (!classFemelle.equals(classMale)){
                    nbrDeLienSortantDeLaClasse++;
                    PaireClass newCouple = new PaireClass(classMale,classFemelle);
                    String currentSignatureOfCouple = newCouple.twoSignature();

                    lienEntrePairs.putIfAbsent(currentSignatureOfCouple, newCouple);

                    lienEntrePairs.get(currentSignatureOfCouple).addOneLink();
                }
            }

            //JUSTE CETTE CONDITION CA SERVAIT A RIEN DE FAIRE UNE USINE A GAZ
            // ENfAITE MON CALLGRAPH N'A PAS FORCEMENT LES VALEURS DANS L'ORDRE DONC ON ECRASAIT DES PRECEDENTES CLASSE QUI EXISTAIT DEJA
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
            //System.out.println(nbrLienDeToutesLesClasses);
            unePaireDeClass.setCouplage(nbrLienEntreToutesLesClasses);
        }
        return lienEntrePairs;
    }

}
