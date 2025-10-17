package um.ico.ingenierie.JavaFilesHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import um.ico.ingenierie.Common.Exceptions.NoJavaPathExceptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Implémentation de IJavaFilesHandler pour une source de type fichier ZIP.
 * Gère la création et le nettoyage automatique d'un répertoire temporaire.
 * DOIT être utilisée dans un bloc try-with-resources pour garantir le nettoyage.
 */
public class JavaFilesHandlerZip implements IJavaFilesHandler, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(JavaFilesHandlerZip.class);

    private final Path tempDir;
    private final List<Path> pathList;

    /**
     * Constructeur qui prend le fichier ZIP uploadé.
     * Il crée un répertoire temporaire, y décompresse le contenu et trouve les fichiers .java.
     * @param zipFile Le fichier MultipartFile envoyé par le client.
     * @throws IOException Si une erreur d'I/O survient.
     */
    public JavaFilesHandlerZip(MultipartFile zipFile) throws IOException {
        // 1. Crée le dossier temporaire. C'est la seule chose que le constructeur fait.
        this.tempDir = Files.createTempDirectory("ast-analysis-zip-");

        // 2. On appelle une méthode privée pour faire le vrai travail.
        this.pathList = processZipFile(zipFile, this.tempDir);
    }

    private List<Path> processZipFile(MultipartFile zipFile, Path destinationDir) throws IOException {
        log.info("Décompression du fichier '{}' dans : {}", zipFile.getOriginalFilename(), destinationDir);

        // On ouvre le flux ici, dans un try-with-resources pour qu'il soit bien fermé.
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            byte[] buffer = new byte[1024];
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(destinationDir.toFile(), zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Échec de la création du répertoire " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Échec de la création du répertoire " + parent);
                    }
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
        log.info("Décompression terminée.");

        // La logique de recherche est maintenant appelée APRES la décompression.
        return findJavaFilesInPath(destinationDir);
    }

    private List<Path> findJavaFilesInPath(Path basePath) throws NoJavaPathExceptions {
        log.debug("Recherche des fichiers .java dans {}", basePath);
        String separator = FileSystems.getDefault().getSeparator();
        String srcMainPathFragment = "src" + separator + "main";

        try (Stream<Path> walker = Files.walk(basePath)) {
            List<Path> files = walker
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().contains(srcMainPathFragment) && path.toString().endsWith(".java"))
                    .collect(Collectors.toList());

            if (files.isEmpty()) {
                throw new NoJavaPathExceptions("Le fichier ZIP ne contient aucun fichier .java dans un sous-dossier 'src/main'.", basePath);
            }
            log.info("{} fichiers .java trouvés.", files.size());
            return files;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du parcours des fichiers temporaires.", e);
        }
    }

    @Override
    public List<Path> getAllPathJava() {
        return this.pathList;
    }

    @Override
    public Path getRootPath() {
        return this.tempDir;
    }

    /**
     * Cette méthode est appelée automatiquement par le try-with-resources.
     * Elle garantit le nettoyage du répertoire temporaire.
     */
    @Override
    public void close() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            log.info("Nettoyage du répertoire temporaire : {}", tempDir);
            FileSystemUtils.deleteRecursively(tempDir);
        }
    }
}