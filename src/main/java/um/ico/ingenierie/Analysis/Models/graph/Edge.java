package um.ico.ingenierie.Analysis.Models.graph;

/**
 *
 * Cette classe représente les liens entre les différents noeuds
 *
 * @author Miguel Delhelle
 * @version ALPHA_BEFORE_SPRING
 */


public class Edge {
    public final String calleeSignature;
    public final EdgeType type;

    public Edge(String calleeSignature, EdgeType type) {
        this.calleeSignature = calleeSignature;
        this.type = type;
    }

    // Nécessaire pour que le Set fonctionne correctement
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return calleeSignature.equals(edge.calleeSignature) && type == edge.type;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(calleeSignature, type);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "calleeSignature='" + calleeSignature + '\'' +
                ", type=" + type +
                '}';
    }
}