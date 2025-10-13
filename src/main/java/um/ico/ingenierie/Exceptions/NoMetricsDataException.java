package um.ico.ingenierie.Exceptions;

public class NoMetricsDataException extends RuntimeException {

    public NoMetricsDataException(){
        super("Le metrics data n'existe pas");
    }

    public NoMetricsDataException(String message) {
        super(message);
    }
}
