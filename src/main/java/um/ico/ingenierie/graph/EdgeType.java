package um.ico.ingenierie.graph;

/**
 * Cette enumeration représente le type de lien qu'on affiche.
 *
 * @author Miguel Delhelle
 * @version ALPHA_BEFORE_SPRING
 */


public enum EdgeType {
    CALL,          // Appel de méthode classique
    INSTANTIATION, // new ...
    THROWS         // throw new ...
}