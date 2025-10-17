package um.ico.ingenierie.JavaFilesHandler;

import um.ico.ingenierie.Common.Exceptions.NoJavaPathExceptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface IJavaFilesHandler {

    public List<Path> getAllPathJava();

    public Path getRootPath();

    @Deprecated(since = "Ancienne méthode du tout début, cependant je la garde déprécié psk c'est fort pratique d'extraire tout le code dans la console")
    default public String javaToString() throws IOException {

        List<Path> toutLesCheminsJava = this.getAllPathJava();
        String toutLeCodeJava = "";
        if (toutLesCheminsJava.isEmpty()){throw new NoJavaPathExceptions(this.getRootPath());
        }
        for (Path unChemin : toutLesCheminsJava){
            toutLeCodeJava = toutLeCodeJava + Files.readString(unChemin);
        }
        return toutLeCodeJava;
    }
}
