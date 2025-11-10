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
        Map<String, PaireClass> nbrLienEntreClass = new HashMap<>();
        Map<String, Double> nbrLienDeToutesLesClasses = new HashMap<>();

        // Algo
        for (Map.Entry<String,Set<Edge> > graph : adjacencyList.entrySet()){
            String callerSignature = graph.getKey(); // Récupération de la clé qui est la signature entière de la classe

            if (basePackage != null && !basePackage.isEmpty() && !callerSignature.startsWith(basePackage)) {
                continue;
            }

            String callerClassName = MySignature.signatureToNameOfClass(callerSignature);

            Set<Edge> arretes = graph.getValue();
            double nbrDeLienSortantDeLaClasse = 0;

            for (Edge arrete: arretes){

                if (basePackage != null && !basePackage.isEmpty() && !arrete.getCalleeSignature().startsWith(basePackage)) {
                    continue;
                }

                if (!MySignature.signatureToNameOfClass(arrete.getCalleeSignature()).equals(callerClassName)){

                    PaireClass nouvellePaire = new PaireClass(
                            MySignature.signatureToNameOfClass(callerSignature),
                            MySignature.signatureToNameOfClass(arrete.getCalleeSignature()));


                    nbrLienEntreClass.putIfAbsent(nouvellePaire.twoSignature(), nouvellePaire);
                    if (nbrLienEntreClass.containsKey(nouvellePaire.twoSignature())){
                        nbrLienEntreClass.get(nouvellePaire.twoSignature()).addOneLink();
                        nbrDeLienSortantDeLaClasse++;
                    }
                }
            }
            nbrLienDeToutesLesClasses.put(callerClassName,nbrDeLienSortantDeLaClasse);
        }

        // Parcours de la Map pour appliquer le taux de couplage.
        for (PaireClass unePaireDeClass: nbrLienEntreClass.values()){
            System.out.println(nbrLienDeToutesLesClasses);
            unePaireDeClass.setCouplage(nbrLienDeToutesLesClasses);
        }
        return nbrLienEntreClass;
    }

    private class PaireClass{
        public String signClassA;
        public String signClassB;
        public double nbrOfLink = 0.0;
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
            this.nbrOfLink = this.nbrOfLink + 1.0;
        }

        @Override
        public String toString(){
            return couplage+"-"+twoSignature();
        }

        public void setCouplage(Map<String,Double> nbrLiensToutesLesClasses) {
            double nbrLienClassA = 0.0;
            double nbrLienClassB = 0.0;
            if (!nbrLiensToutesLesClasses.containsKey(signClassA)){
                log.warn("La class: "+signClassA+" n'existe pas dans la Map de lien interne!");
                nbrLienClassB = nbrLiensToutesLesClasses.get(signClassB);
            }
            else if (!nbrLiensToutesLesClasses.containsKey(signClassB)) {
                log.warn("La class: "+signClassB+" n'existe pas dans la Map de lien interne!");
                nbrLienClassA = nbrLiensToutesLesClasses.get(signClassA);
            }
            else {
                nbrLienClassA = nbrLiensToutesLesClasses.get(signClassA);
                nbrLienClassB = nbrLiensToutesLesClasses.get(signClassB);
            }
            double totalLienDesDeuxClasses = nbrLienClassA+nbrLienClassB;
            this.couplage = nbrOfLink/totalLienDesDeuxClasses;
        }
    }

}
