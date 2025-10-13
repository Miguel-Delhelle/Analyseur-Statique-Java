package um.ico.ingenierie.Exceptions;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Exception levée dans le cas ou le Path indiqué
 * ne correspond pas à un répertoire Java.
 *
 * @author Miguel Delhelle
 * @version 1.0
 */

public class NoJavaPathExceptions extends IOException {

    Path leChemin;

    public NoJavaPathExceptions(){
        super("Le chemin indiqué ne correspond pas à du code Java");
    }
    public NoJavaPathExceptions(Path leChemin) {
        super ("Erreur, le chemin: "+leChemin.toString()+"ne correspond pas à du code Java, relancez le programme");
        this.leChemin = leChemin;
    }

    public NoJavaPathExceptions(String message, Path leChemin) {
        super(message);
        this.leChemin = leChemin;
    }

    public NoJavaPathExceptions(String message, Throwable cause, Path leChemin) {
        super(message, cause);
        this.leChemin = leChemin;
    }

    public NoJavaPathExceptions(Throwable cause, Path leChemin) {
        super(cause);
        this.leChemin = leChemin;
    }
}
