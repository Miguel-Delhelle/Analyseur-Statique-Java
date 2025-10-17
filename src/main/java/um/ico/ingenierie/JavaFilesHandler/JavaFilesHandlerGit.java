package um.ico.ingenierie.JavaFilesHandler;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;
import um.ico.ingenierie.Common.Exceptions.NoJavaPathExceptions;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implémentation de IJavaFilesHandler pour une source de type dépôt Git.
 * Cette classe clone un dépôt distant dans un répertoire temporaire
 * et est responsable de son nettoyage.
 * Elle doit être utilisée dans un bloc try-with-resources.
 */
public class JavaFilesHandlerGit implements IJavaFilesHandler, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(JavaFilesHandlerGit.class);

    private Path tempDir;
    private List<Path> pathList;

    /**
     * Constructeur qui prend l'URL d'un dépôt Git, le clone dans un répertoire temporaire,
     * et prépare l'analyse des fichiers .java.
     *
     * @param gitUrl L'URL du dépôt Git à cloner (ex: https://github.com/user/repo.git).
     * @throws IOException Si une erreur de création de dossier survient.
     * @throws GitAPIException Si une erreur survient durant le clonage (URL invalide, dépôt privé...).
     */
    public JavaFilesHandlerGit(String gitUrl) throws IOException, GitAPIException {
        // 1. Crée le dossier temporaire
        this.tempDir = Files.createTempDirectory("ast-analysis-git-");

        log.info("Clonage du dépôt '{}' dans le dossier temporaire : {}", gitUrl, tempDir);

        // 2. Clone le dépôt Git en utilisant JGit
        try {
            Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(tempDir.toFile())
                    .setDepth(1) // Optimisation : ne clone que la dernière version des fichiers (pas tout l'historique)
                    .call();
            log.info("Clonage terminé avec succès.");
        } catch (GitAPIException e) {
            close();
            throw e;
        }

        this.pathList = allPathJava();
    }

    private List<Path> allPathJava() throws IOException {
        List<Path> toutLesCheminsJava = new ArrayList<Path>();
        String separator = FileSystems.getDefault().getSeparator();
        String srcMainPathFragment = "src" + separator + "main";
        try (Stream<Path> walker = Files.walk(this.getRootPath())) {
            toutLesCheminsJava = walker
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String pathAsString = path.toString();
                        return pathAsString.endsWith(".java") &&
                                pathAsString.contains(srcMainPathFragment);
                    })
                    .collect(Collectors.toList());
        }
        if (toutLesCheminsJava.isEmpty()) {
            throw new NoJavaPathExceptions(this.getRootPath());
        }
        return toutLesCheminsJava;
    }

    @Override
    public void close() throws IOException {
        // 4. Nettoie le dossier temporaire à la fin
        log.info("Nettoyage du dossier temporaire Git : {}", tempDir);
        FileSystemUtils.deleteRecursively(tempDir);
    }

    @Override
    public List<Path> getAllPathJava() {
        return this.pathList;
    }

    @Override
    public Path getRootPath() {
        return tempDir;
    }
}