package um.ico.ingenierie.Exceptions;

public class NoCompilationUnitExceptions extends RuntimeException {

    public NoCompilationUnitExceptions(){super("Aucune compilation Unit n'a été renseigné");}

    public NoCompilationUnitExceptions(String message) {
        super(message);
    }
}
