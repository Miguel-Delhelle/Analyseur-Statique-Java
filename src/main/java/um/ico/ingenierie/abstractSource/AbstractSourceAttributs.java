package um.ico.ingenierie.abstractSource;

import java.io.Serializable;

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
