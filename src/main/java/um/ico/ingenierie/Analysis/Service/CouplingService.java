package um.ico.ingenierie.Analysis.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;
import um.ico.ingenierie.Analysis.Models.graph.Edge;
import um.ico.ingenierie.Common.utils.MySignature;

import java.util.*;

public class CouplingService {

    private static final Logger log = LoggerFactory.getLogger(CouplingService.class);

    public CouplingService(){

    }

    public Map<String,PaireClass> TreeCoupling(CallGraph callGraph) {

        //Variables d'entrée
        String basePackage = MySignature.determineBasePackage(callGraph);
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
            String classMale = MySignature.signatureToNameOfClass(callerSignature);
            Set<Edge> arretes = graph.getValue();
            int nbrDeLienSortantDeLaClasse = 0;

            for (Edge arrete: arretes){

                String classFemelle = MySignature.signatureToNameOfClass(arrete.getCalleeSignature());
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

    private class PaireClass{
        public String signClassA;
        public String signClassB;
        public int nbrOfLink = 0;
        public double couplage;

        public PaireClass(String signClassA, String signClassB){
            if (signClassA.compareTo(signClassB) <= 0) {
                this.signClassA = signClassA;
                this.signClassB = signClassB;
            } else {
                this.signClassA = signClassB;
                this.signClassB = signClassA;
            }
        }

        public boolean equals(PaireClass that) {
            return (signClassA.equals(that.signClassA) && signClassB.equals(that.signClassB)) ||
                    (signClassA.equals(that.signClassB) && signClassB.equals(that.signClassA));
        }

        public String twoSignature(){
            return "COUPLING_"+this.signClassA + "#"+ this.signClassB;
        }

        public void addOneLink(){
            this.nbrOfLink = this.nbrOfLink + 1;
        }

        @Override
        public String toString(){
            return couplage+"-"+twoSignature();
        }

        public void setCouplage(Map<String,Integer> nbrLiensToutesLesClasses) {
            int nbrLienClassA = nbrLiensToutesLesClasses.getOrDefault(signClassA,0);
            int nbrLienClassB = nbrLiensToutesLesClasses.getOrDefault(signClassB,0);
            log.info("NbrLink: "+nbrOfLink+" du couple: "+signClassA+"#"+signClassB);
            double totalLienDesDeuxClasses = nbrLienClassA+nbrLienClassB;
            log.info("NbrLiendeLaClass"+signClassA+" : "+nbrLienClassA);
            log.info("NbrLiendeLaClass"+signClassB+" : "+nbrLienClassB);
            log.info("Total des liens sortants des deux classes du couple"+totalLienDesDeuxClasses);
            this.couplage = (double) nbrOfLink/ totalLienDesDeuxClasses;
            log.info("Couplage du couple: "+this.couplage);
        }
    }

}
