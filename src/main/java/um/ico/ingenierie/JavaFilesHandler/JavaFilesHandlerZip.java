package um.ico.ingenierie.JavaFilesHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import um.ico.ingenierie.Common.Exceptions.NoJavaPathExceptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JavaFilesHandlerZip implements IJavaFilesHandler{

    Logger logger = LoggerFactory.getLogger(JavaFilesHandlerZip.class);
    List<Path> pathList = new LinkedList<>();
    Path rootPath;


    public JavaFilesHandlerZip(ZipInputStream zipInput) throws IOException {
        initPathList(zipInput);
    }

    @Override
    public List<Path> getAllPathJava() {
        return pathList;
    }

    @Override
    public Path getRootPath() {
        return rootPath;
    }

    private void initPathList(ZipInputStream zipFiles) throws IOException {
        byte[] buffer = new byte[1024];
        ZipEntry zipEntry = zipFiles.getNextEntry();

        Path tempDir = Files.createTempDirectory("ast-analysis-zip");
        while (zipEntry != null) {
            File newFile = new File(tempDir.toFile(), zipEntry.getName());
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    logger.warn("Echec de la création du répertoire temporaire", newFile);
                    throw new IOException("Échec de la création du répertoire " + newFile);
                }
            } else {
                // S'assure que le répertoire parent du fichier existe
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    logger.warn("Echec de la création du répertoire temporaire", parent.toString());
                    throw new IOException("Échec de la création du répertoire " + parent);
                }

                // Écrit le contenu du fichier
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zipFiles.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
            zipEntry = zipFiles.getNextEntry();
        }
        zipFiles.closeEntry();


        // --- ÉTAPE 2 : RECHERCHE DES FICHIERS .JAVA DANS LE DOSSIER DÉCOMPRESSÉ ---

        // On réutilise la logique de filtrage qu'on a définie précédemment
        String separator = FileSystems.getDefault().getSeparator();
        String srcMainPathFragment = "src" + separator + "main";
        List<Path> javaFiles;

        try (Stream<Path> walker = Files.walk(tempDir)) {
            javaFiles = walker
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String pathAsString = path.toString();
                        // On vérifie que le fichier est un .java ET qu'il est dans le bon sous-dossier
                        return pathAsString.endsWith(".java") &&
                                pathAsString.contains(srcMainPathFragment);
                    })
                    .collect(Collectors.toList());
        }

        if (javaFiles.isEmpty()) {
            throw new NoJavaPathExceptions();
        }

        this.pathList = javaFiles;
        this.rootPath = tempDir;

    }


}
