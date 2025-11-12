package um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PaireClass{

    private static final Logger log = LoggerFactory.getLogger(PaireClass.class);

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

    public String getSignClassA() {
        return signClassA;
    }

    public String getSignClassB() {
        return signClassB;
    }

    public int getNbrOfLink() {
        return nbrOfLink;
    }

    public double getCouplage() {
        return couplage;
    }
}