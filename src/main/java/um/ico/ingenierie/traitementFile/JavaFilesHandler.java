package um.ico.ingenierie.traitementFile;

import um.ico.ingenierie.Exceptions.NoJavaPathExceptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Cette classe à pour but de transformer un chemin d'un répertoire contenant du Java passé en chaine de caractère
 * Par un tableau de caractères ou un String contenant que du code Java.
 *
 * @author Miguel Delhelle
 * @version 1.0
 */

public class JavaFilesHandler {

    private Path chemin = Paths.get(".");
    private List<Path> allPathJava = new ArrayList<Path>();

    public JavaFilesHandler() throws IOException {
        this.allPathJava = this.allPathJava();
    }

    public JavaFilesHandler(String path) throws IOException {
        this.chemin = Paths.get(path);
        this.allPathJava = this.allPathJava();
    }

    public Path getChemin() {
        return chemin;
    }

    public void setChemin(Path chemin) {
        this.chemin = chemin;
    }

    private List<Path> allPathJava() throws IOException {

        List<Path> toutLesCheminsJava;
        String toutLeCodeJava = "";
        try (Stream<Path> walker = Files.walk(this.chemin)) {

            toutLesCheminsJava =
                    walker.filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".java"))
                            .collect(Collectors.toList());
        }
        if (toutLesCheminsJava.isEmpty()){throw new NoJavaPathExceptions(this.chemin);}
        return toutLesCheminsJava;
    }

    public String[] allPathToStringArray() throws IOException {

        List<Path> toutLesCheminsJava = this.getAllPathJava();
        List<String> allPathInString = new ArrayList<String>();
        if (toutLesCheminsJava.isEmpty()){throw new NoJavaPathExceptions(this.chemin);}
        for (Path unChemin : toutLesCheminsJava){
            allPathInString.add(unChemin.toString());
        }
        String[] arrayFinal = allPathInString.toArray(String[]::new);
        return arrayFinal;
    }

    @Deprecated(since = "Ancienne méthode du tout début, cependant je la garde déprécié psk c'est fort pratique d'extraire tout le code dans la console")
    public String javaToString() throws IOException {

        List<Path> toutLesCheminsJava = this.getAllPathJava();
        String toutLeCodeJava = "";
//        try (Stream<Path> walker = Files.walk(this.chemin)){
//
//            toutLesCheminsJava =
//                    walker.filter(Files::isRegularFile)
//                    .filter(path -> path.toString().endsWith(".java"))
//                    .collect(Collectors.toList());
//        }
        if (toutLesCheminsJava.isEmpty()){throw new NoJavaPathExceptions(this.chemin);
        }
        for (Path unChemin : toutLesCheminsJava){
            toutLeCodeJava = toutLeCodeJava + Files.readString(unChemin);
        }
        return toutLeCodeJava;
    }

    public List<Path> getAllPathJava() {
        return allPathJava;
    }

    public int getNumberOfJavaFiles(){
        return this.allPathJava.size();
    }

    public static char[] getJavaFile(Path path) throws IOException {
        return Files.readString(path).toCharArray();
    }

}
