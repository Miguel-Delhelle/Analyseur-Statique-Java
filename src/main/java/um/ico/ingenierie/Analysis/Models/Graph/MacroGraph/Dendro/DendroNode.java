package um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendro;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class DendroNode {

    private final DendroNode leftChild;
    private final DendroNode rightChild;
    private final double hauteurCoupling;
    private final String className; // Non-null SEULEMENT pour les feuilles
    private final Set<String> classContenu;

    public DendroNode(String className) {
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("Le nom de la classe ne peut pas être nul ou vide pour une feuille.");
        }
        this.leftChild = null;
        this.rightChild = null;
        this.hauteurCoupling = 0.0;
        this.className = className;
        this.classContenu = Collections.singleton(className);
    }

    public DendroNode(DendroNode child1, DendroNode child2, double couplingValue) {
        if (child1 == null || child2 == null) {
            throw new IllegalArgumentException("Les enfants d'un nœud de fusion ne peuvent pas être nuls.");
        }
        this.leftChild = child1;
        this.rightChild = child2;
        this.hauteurCoupling = couplingValue;
        this.className = null; // UN NOEUD DE FUSION N'A PAS DE NOM DE CLASSE
        this.classContenu = new HashSet<>(child1.getClassContenu());
        this.classContenu.addAll(child2.getClassContenu());
    }

    // LA DÉFINITION CORRECTE DE "isLeaf"
    public boolean isLeaf() {
        return this.className != null;
    }

    // --- GETTERS ---
    public DendroNode getLeftChild() { return leftChild; }
    public DendroNode getRightChild() { return rightChild; }
    public double getHauteurCoupling() { return hauteurCoupling; }
    public String getClassName() { return className; }
    public Set<String> getClassContenu() { return classContenu; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DendroNode that = (DendroNode) o;
        return classContenu.equals(that.classContenu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classContenu);
    }
}