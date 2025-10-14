package um.ico.ingenierie.graph;

import java.util.*;

/**
 * La classe CallGraph permet de créer un .dot représentant le graphe d'appel
 *
 * @author Miguel Delhelle
 * @version ALPHA_BEFORE_SPRING
 */

public class CallGraph {

    // Caller Method Signature -> Set of Callee Method Signatures
    private final Map<String, Set<Edge>> adjacencyList = new HashMap<>();

    public void addEdge(String callerSignature, String calleeSignature, EdgeType type) {
        adjacencyList.computeIfAbsent(callerSignature, k -> new HashSet<>());
        adjacencyList.computeIfAbsent(calleeSignature, k -> new HashSet<>());
        adjacencyList.get(callerSignature).add(new Edge(calleeSignature, type));
    }




    /**
     * Génère une représentation du graphe au format DOT, en filtrant
     * pour ne garder que les nœuds appartenant au package de base fourni.
     * @param basePackageFilter Le package de base du projet à visualiser.
     * @return Une chaîne de caractères prête à être écrite dans un fichier .dot.
     */
    public String toDotString(String basePackageFilter) {
        StringBuilder dot = new StringBuilder("digraph CallGraph {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  splines=ortho;\n");
        dot.append("  dpi=150; // Augmente la résolution (défaut=96). 150 est un bon début.\n");
        dot.append("  size=\"20,20\"; // Taille maximale en pouces (facultatif, mais aide à contrôler la taille)\n");
        dot.append("  ratio=auto; // Ajuste les proportions\n");
        dot.append("  node [shape=box, style=rounded, fontname=\"Helvetica\", fontsize=10];\n");
        dot.append("  edge [fontname=\"Helvetica\", fontsize=9];\n");
        dot.append("  graph [label=\"" + basePackageFilter + "\", fontsize=12, fontcolor=\"blue\"];\n\n");

        // ÉTAPE 1: Regrouper les méthodes par nom de classe
        Map<String, List<String>> classToMethodsMap = new HashMap<>();
        for (String signature : adjacencyList.keySet()) {
            if (signature.startsWith(basePackageFilter)) {
                String className = signature.split("#")[0];
                classToMethodsMap.computeIfAbsent(className, k -> new ArrayList<>()).add(signature);
            }
        }

        // ÉTAPE 2: Créer une map de signatures vers des IDs de nœuds uniques (n0, n1...)
        Map<String, String> signatureToNodeId = new HashMap<>();
        int nodeCounter = 0;

        // ÉTAPE 3: Écrire les clusters (un par classe)
        int clusterCounter = 0;
        for (Map.Entry<String, List<String>> entry : classToMethodsMap.entrySet()) {
            String className = entry.getKey();
            List<String> methods = entry.getValue();
            String simpleClassName = className.substring(className.lastIndexOf('.') + 1);

            dot.append(String.format("  subgraph cluster_%d {\n", clusterCounter++));
            dot.append(String.format("    label = \"%s\";\n", simpleClassName));
            dot.append("    style = \"filled\";\n");
            dot.append("    color = \"lightgrey\";\n\n");

            // Déclarer tous les nœuds (méthodes) de cette classe
            for (String methodSignature : methods) {
                String nodeId = "n" + nodeCounter++;
                signatureToNodeId.put(methodSignature, nodeId);
                // On ne garde que le nom de la méthode pour le label, la classe est déjà dans le titre du cluster
                String methodLabel = simplifySignature(methodSignature).split("#")[1];
                dot.append(String.format("    %s [label=\"%s\"];\n", nodeId, methodLabel));
            }
            dot.append("  }\n\n");
        }

        // ÉTAPE 4: Écrire toutes les arêtes (les flèches)
        for (Map.Entry<String, Set<Edge>> edgeEntry : adjacencyList.entrySet()) {
            String callerSignature = edgeEntry.getKey();

            if (callerSignature.startsWith(basePackageFilter)) {
                String callerId = signatureToNodeId.get(callerSignature);
                if (callerId == null) continue;

                for (Edge edge : edgeEntry.getValue()) {
                    String calleeSignature = edge.calleeSignature;
                    if (calleeSignature.startsWith(basePackageFilter)) {
                        String calleeId = signatureToNodeId.get(calleeSignature);
                        if (calleeId != null) {
                            // LA NOUVELLE LOGIQUE DE COULEUR
                            String color = "black"; // Par défaut
                            switch (edge.type) {
                                case INSTANTIATION: color = "darkgreen"; break;
                                case THROWS: color = "red"; break;
                            }
                            dot.append(String.format("  %s -> %s [color=\"%s\"];\n", callerId, calleeId, color));
                        }
                    }
                }
            }
        }
        // Légende totalement généré par Gemini Pro, mais voilà le truc
        dot.append("\n  subgraph cluster_legend {\n");
        dot.append("    rankdir = TB; // Orientation de haut en bas pour la légende\n");
        dot.append("    label = \"Légende\";\n");
        dot.append("    style = \"dotted\";\n");
        dot.append("    bgcolor = \"white\";\n");
        dot.append("    // Utilisation d'un label HTML pour un alignement parfait\n");
        dot.append("    key [shape=none, margin=0, label=<\n");
        dot.append("      <table border=\"0\" cellpadding=\"2\" cellspacing=\"0\" cellborder=\"0\">\n");
        dot.append("        <tr><td align=\"right\" port=\"c1\">Appel de méthode</td><td>&nbsp;</td><td align=\"left\" port=\"c2\">&nbsp;</td></tr>\n");
        dot.append("        <tr><td align=\"right\" port=\"i1\">Instanciation (new)</td><td>&nbsp;</td><td align=\"left\" port=\"i2\">&nbsp;</td></tr>\n");
        dot.append("        <tr><td align=\"right\" port=\"t1\">Lève une exception</td><td>&nbsp;</td><td align=\"left\" port=\"t2\">&nbsp;</td></tr>\n");
        dot.append("      </table>\n");
        dot.append("    >];\n");
        dot.append("    // On dessine les arêtes colorées en se connectant aux ports du tableau HTML\n");
        dot.append("    key:c1 -> key:c2 [color=\"black\"];\n");
        dot.append("    key:i1 -> key:i2 [color=\"darkgreen\"];\n");
        dot.append("    key:t1 -> key:t2 [color=\"red\"];\n");
        dot.append("  }\n");

        dot.append("}\n");
        return dot.toString();
    }

    /**
     * Simplifie une signature complète pour une meilleure lisibilité.
     * "com.exemple.package#main(java.lang.String[])" devient "Main#main(...)"
     */
    private String simplifySignature(String signature) {
        String[] parts = signature.split("#");
        String classAndPackage = parts[0];
        String methodNameAndParams = parts.length > 1 ? parts[1] : "";

        String className = classAndPackage.substring(classAndPackage.lastIndexOf('.') + 1);

        // On remplace les longs noms de paramètres par "..." pour la clarté
        return className + "#" + methodNameAndParams.replaceAll("\\(.*\\)", "(...)");
    }

    // On pourrait ajouter une méthode pour afficher le graphe (format DOT, etc.)
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graphe d'Appel :\n");
        for (Map.Entry<String, Set<Edge>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey()).append(" -->\n");
            for (Edge callee : entry.getValue()) {
                sb.append("\t- ").append(callee).append("\n");
            }
        }
        return sb.toString();
    }
}