package um.ico.ingenierie.Common.Exceptions;

public class NoValidGitRemoteUriException extends RuntimeException {

    public NoValidGitRemoteUriException() {
        super("Pas de répertoire git valide");
    }

    public NoValidGitRemoteUriException(String message) {
        super(message);
    }
}
