package fr.delweb.analyzer.Common.results;

/**
 * Cette classe sert à pouvoir retourner plusieurs informations après le parcours d'une boucle.
 * En l'occurence elle est utilisé ici pour vérifier l'existence dans une liste ainsi que la position de l'objet trouvée.
 *
 * @author Miguel Delhelle
 * @version 1.0
 */

public class SearchResult {

    private boolean exist = false;
    private int position = -1;

    public SearchResult(boolean exist, int position) {
        this.exist = exist;
        this.position = position;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
