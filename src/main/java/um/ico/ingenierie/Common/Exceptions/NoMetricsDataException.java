package um.ico.ingenierie.Common.Exceptions;

/**
 * Exception levée si un objet MetricsData n'est pas renseignée.
 * L'exception ne peut pas être levée par le comportemetn de l'utilisateur.
 *
 * @author Miguel Delhelle
 * @version 1.0
 */

public class NoMetricsDataException extends RuntimeException {

    public NoMetricsDataException(){
        super("Le metrics data n'existe pas");
    }

    public NoMetricsDataException(String message) {
        super(message);
    }
}
