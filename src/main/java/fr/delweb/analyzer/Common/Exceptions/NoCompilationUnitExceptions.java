package fr.delweb.analyzer.Common.Exceptions;

/**
 * Exception levée si les objets de compilationUnit n'existe pas.
 * L'exception ne peut logiquement pas être levée par le comportement d'un utilisateur.
 *
 * @author Miguel Delhelle
 * @version 1.0
 */

public class NoCompilationUnitExceptions extends RuntimeException {

    public NoCompilationUnitExceptions(){super("Aucune compilation Unit n'a été renseigné");}

    public NoCompilationUnitExceptions(String message) {
        super(message);
    }
}
