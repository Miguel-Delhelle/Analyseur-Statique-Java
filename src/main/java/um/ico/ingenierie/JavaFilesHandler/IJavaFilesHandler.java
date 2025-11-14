package um.ico.ingenierie.JavaFilesHandler;

import um.ico.ingenierie.Common.Exceptions.NoJavaPathExceptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface IJavaFilesHandler {

    public List<Path> getAllPathJava();

    public Path getRootPath();
}
