package um.ico.ingenierie.graph;

public enum EdgeType {
    CALL,          // Appel de méthode classique
    INSTANTIATION, // new ...
    THROWS         // throw new ...
}