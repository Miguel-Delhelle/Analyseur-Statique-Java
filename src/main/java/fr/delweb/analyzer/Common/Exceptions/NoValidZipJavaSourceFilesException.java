package fr.delweb.analyzer.Common.Exceptions;

public class NoValidZipJavaSourceFilesException extends RuntimeException {

    public NoValidZipJavaSourceFilesException(){
        super("L'archive Zip ne contient pas de fichier Java, ou est corrompue");
    }

    public NoValidZipJavaSourceFilesException(String message) {
        super(message);
    }
}
