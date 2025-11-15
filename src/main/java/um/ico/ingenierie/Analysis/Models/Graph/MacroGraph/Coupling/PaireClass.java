package um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import um.ico.ingenierie.Common.utils.PairNormalisation;

import java.util.*;

public class PaireClass{

    private static final Logger log = LoggerFactory.getLogger(PaireClass.class);

    public String signClassA;
    public String signClassB;
    public int nbrOfLink = 0;
    public double couplage;

    public PaireClass(String signClassA, String signClassB) {
        String[] ordered = PairNormalisation.normalizePair(signClassA, signClassB);
        this.signClassA = ordered[0];
        this.signClassB = ordered[1];
    }

    public PaireClass(String signClassA, String signClassB,double couplage) {
        String[] ordered = PairNormalisation.normalizePair(signClassA, signClassB);
        this.signClassA = ordered[0];
        this.signClassB = ordered[1];
        this.couplage = couplage;
    }

    // --- FIXED VERSION ---
    public void merged(String classA, String classB) {
        String merged = "CLUSTER" + String.join("#", PairNormalisation.normalizePair(classA, classB));

        if (signClassA.equals(classA) || signClassA.equals(classB)) {
            signClassA = merged;
        }
        if (signClassB.equals(classA) || signClassB.equals(classB)) {
            signClassB = merged;
        }
        normalize();
    }

    // --- FIXED VERSION ---
    public void merged(PaireClass that) {
        merged(that.signClassA, that.signClassB);
    }

    // always maintain canonical ordering after modifications
    private void normalize() {
        String[] ordered = PairNormalisation.normalizePair(this.signClassA, this.signClassB);
        this.signClassA = ordered[0];
        this.signClassB = ordered[1];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PaireClass)) return false;

        PaireClass that = (PaireClass) obj;
        return (this.signClassA.equals(that.signClassA) && this.signClassB.equals(that.signClassB))
                || (this.signClassA.equals(that.signClassB) && this.signClassB.equals(that.signClassA));
    }
    @Override
    public int hashCode() {
        // ordre indépendant : on trie les deux noms
        String c1 = signClassA.compareTo(signClassB) <= 0 ? signClassA : signClassB;
        String c2 = signClassA.compareTo(signClassB) > 0 ? signClassA : signClassB;

        return Objects.hash(c1, c2);
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

    public boolean contains(String nameOfClass){
        if (this.getSignClassA().equals(nameOfClass)){
            return true;
        }
        else if (this.getSignClassB().equals(nameOfClass)){
            return true;
        }
        else {
            return false;
        }
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
    public String getOtherClass(String className){
        if (className.equals(signClassA)){
            return signClassB;
        }else if (className.equals(signClassB)){
            return signClassA;
        }else {
            return null;
        }
    }

    public static List<PaireClass> toutLesCouplesDe(String nameOfClass, Collection<PaireClass> inCollection){
        List<PaireClass> listDeCoupleOfThatClass = new ArrayList<PaireClass>();
        for (PaireClass paireClass : inCollection){
            if (paireClass.contains(nameOfClass)){
                listDeCoupleOfThatClass.add(paireClass);
            }
        }
        return listDeCoupleOfThatClass;
    }
}