package um.ico.ingenierie.Models;

import java.io.Serializable;

/**
 * Cette classe, est un modèle de répresentation abstraite
 * de la représentation d'un attributs trouvé dans le code source analysé.
 *
 * @author Miguel Delhelle
 * @version 1.0
 */

public class AbstractSourceAttributs implements Serializable {

    private AbstractSourceClass classParent;
    private String typeOfAttributs;
    private String nameOfAttributs;

    public AbstractSourceAttributs() {
    }

    public AbstractSourceAttributs(AbstractSourceClass classParent, String typeOfAttributs, String nameOfAttributs) {
        this.classParent = classParent;
        this.typeOfAttributs = typeOfAttributs;
        this.nameOfAttributs = nameOfAttributs;
    }
}
