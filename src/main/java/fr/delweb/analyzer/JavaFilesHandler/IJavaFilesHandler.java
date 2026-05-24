package fr.delweb.analyzer.JavaFilesHandler;

import java.nio.file.Path;
import java.util.List;

public interface IJavaFilesHandler {

    public List<Path> getAllPathJava();

    public Path getRootPath();
}
