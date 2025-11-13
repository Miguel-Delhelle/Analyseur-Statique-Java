package um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendo;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;

import java.util.Set;

public class DendoNode {

    private DendoNode leftChild;
    private DendoNode rightChild;

    private double hauteurCoupling;

    private String className;

    //Conteneur final
    // On utilise Set pour avoir des éléments unique et non ordonnées
    private Set<String> classContenu;

    // Constructeur vide protégé pour peut être des itérations plus tard avec des librarie comme JPA Hibernate
    // Qui en nécessite par défaut
    protected DendoNode(){};

    // Constructeur pour les Noeuds feuille
    public DendoNode(String className){
        this.leftChild = null;
        this.rightChild = null;
        this.hauteurCoupling = 0.0;
        this.className = className;
    }

    //Constructeur pour les noeuds représentant les fusions entre deux noeuds

    public DendoNode(DendoNode child1, DendoNode child2, double couplingValue){
        this.leftChild = child1;
        this.rightChild = child2;
        this.hauteurCoupling = couplingValue;
        //TODO METTRE TOUTES LES CLASSES dans un noeud pour l'algo DENDOGRAMME
    }

    public boolean isLeaf(){
        if (className == null){return true;}
        else {return false;}
    }


    public DendoNode getLeftChild() {
        return leftChild;
    }

    public DendoNode getRightChild() {
        return rightChild;
    }

    public double getHauteurCoupling() {
        return hauteurCoupling;
    }

    public String getClassName() {
        return className;
    }

    public Set<String> getClassContenu() {
        return classContenu;
    }
}
