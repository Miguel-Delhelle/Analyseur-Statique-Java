package um.ico.ingenierie.Common.utils;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;

import java.util.List;
import java.util.stream.Collectors;

public final class MySignature {

    public static String createMethodSignature(IMethodBinding binding) {
        if (binding == null) return "unknown.binding";

        StringBuilder signature = new StringBuilder();
        ITypeBinding declaringClass = binding.getDeclaringClass();
        if (declaringClass == null || declaringClass.getQualifiedName().isEmpty()) {
            return "local.class#" + binding.getName();
        }
        signature.append(declaringClass.getErasure().getQualifiedName());
        signature.append("#");
        signature.append(binding.getName());
        signature.append("(");

        ITypeBinding[] parameters = binding.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            signature.append(parameters[i].getErasure().getQualifiedName());
            if (i < parameters.length - 1) {
                signature.append(",");
            }
        }
        signature.append(")");
        return signature.toString();
    }

    public static String simplifySignature(String signature) {
        String[] parts = signature.split("#");
        String classAndPackage = parts[0];
        String methodNameAndParams = parts.length > 1 ? parts[1] : "";

        String className = classAndPackage.substring(classAndPackage.lastIndexOf('.') + 1);

        // On remplace les longs noms de paramètres par "..." pour la clarté
        return className + "#" + methodNameAndParams.replaceAll("\\(.*\\)", "(...)");
    }

    public static String signatureToNameOfClass(String signature) {
        if (signature.isEmpty()){return "";}
        String result = "";
        String afterHash = signature.split("#")[0];
        String[] tabResult = afterHash.split("\\.");
        result = tabResult[tabResult.length-1];
        return result;
    }

    /**
     * Détermine le package de base du projet analysé à partir d'un CallGraph
     * en trouvant le plus long préfixe commun à tous les noms de classes.
     * @param callGraph L'objet contenant le graphe d'appel.
     * @return Le nom du package de base (ex: "com.example.superAppli") ou une chaîne vide.
     */
    public static String determineBasePackage(CallGraph callGraph) {
        // 1. Extraire tous les noms de classes uniques des clés du graphe d'appel.
        //    On utilise un Set pour garantir l'unicité et la performance.
        List<String> classNames = callGraph.getAdjacencyList().keySet().stream()
                .map(signature -> signature.split("#")[0]) // Extrait le nom de la classe de la signature
                .distinct() // Garde uniquement les noms de classes uniques
                .collect(Collectors.toList());

        if (classNames.isEmpty()) {
            return "";
        }

        // 2. L'algorithme du plus long préfixe commun reste identique.
        String longestCommonPrefix = classNames.get(0);
        for (int i = 1; i < classNames.size(); i++) {
            String currentClassName = classNames.get(i);
            while (!currentClassName.startsWith(longestCommonPrefix)) {
                // On retire le dernier segment (après le dernier point)
                int lastDot = longestCommonPrefix.lastIndexOf('.');
                if (lastDot == -1) {
                    return ""; // Pas de préfixe commun
                }
                longestCommonPrefix = longestCommonPrefix.substring(0, lastDot);
            }
        }

        // On a trouvé le préfixe commun le plus long, qui est notre package de base.
        return longestCommonPrefix;
    }

}
