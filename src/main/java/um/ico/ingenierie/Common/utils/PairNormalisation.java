package um.ico.ingenierie.Common.utils;

import java.util.Objects;

public final class PairNormalisation {

    private PairNormalisation() {} // empêche l'instanciation

    /**
     * Retourne un tableau où les deux chaînes sont
     * triées de façon lexicographique et null-safe.
     * Toujours déterministe :
     *  - normalizePair("B","A") → ["A","B"]
     *  - normalizePair("A","A") → ["A","A"]
     */
    public static String[] normalizePair(String a, String b) {
        Objects.requireNonNull(a, "first value cannot be null");
        Objects.requireNonNull(b, "second value cannot be null");

        if (a.compareTo(b) <= 0) {
            return new String[] { a, b };
        } else {
            return new String[] { b, a };
        }
    }
}
