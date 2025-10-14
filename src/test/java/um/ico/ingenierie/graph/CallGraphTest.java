// Fichier : src/test/java/um/ico/ingenierie/graph/CallGraphTests.java

package um.ico.ingenierie.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests unitaires pour la classe CallGraph")
class CallGraphTests {

    private CallGraph callGraph;

    @BeforeEach
    void setUp() {
        callGraph = new CallGraph();
    }

    @Test
    @DisplayName("addEdge doit créer une arête simple entre deux méthodes")
    void addEdge_shouldCreateSimpleEdge() {
        // Arrange
        String caller = "com.monprojet.ServiceA#methode1()";
        String callee = "com.monprojet.ServiceB#methode2()";

        // Act
        callGraph.addEdge(caller, callee, EdgeType.CALL);

        // Assert
        // On vérifie que la représentation textuelle contient bien l'appel.
        // C'est un test simple mais efficace de l'état interne.
        assertThat(callGraph.toString()).contains(caller + " -->")
                .contains("- " + new Edge(callee, EdgeType.CALL).toString());
    }

    @Test
    @DisplayName("addEdge doit gérer plusieurs appels depuis la même méthode")
    void addEdge_shouldHandleMultipleCallsFromSameMethod() {
        // Arrange
        String caller = "com.monprojet.ServiceA#methode1()";
        String callee1 = "com.monprojet.ServiceB#methode2()";
        String callee2 = "com.monprojet.common.Utils#format()";

        // Act
        callGraph.addEdge(caller, callee1, EdgeType.CALL);
        callGraph.addEdge(caller, callee2, EdgeType.INSTANTIATION);

        // Assert
        assertThat(callGraph.toString()).contains(caller + " -->")
                .contains(callee1)
                .contains(callee2);
    }

    @Test
    @DisplayName("toDotString doit générer un graphe DOT valide avec des clusters")
    void toDotString_shouldGenerateValidDotWithClusters() {
        // Arrange
        String basePackage = "com.monprojet";
        String serviceA_methode1 = "com.monprojet.service.ServiceA#methode1()";
        String serviceA_methode2 = "com.monprojet.service.ServiceA#methode2()";
        String serviceB_methode3 = "com.monprojet.service.ServiceB#methode3()";
        String external_methode = "org.springframework.Helper#help()";

        callGraph.addEdge(serviceA_methode1, serviceA_methode2, EdgeType.CALL);
        callGraph.addEdge(serviceA_methode1, serviceB_methode3, EdgeType.INSTANTIATION);
        callGraph.addEdge(serviceB_methode3, serviceA_methode1, EdgeType.CALL); // Appel cyclique
        callGraph.addEdge(serviceA_methode1, external_methode, EdgeType.CALL); // Appel externe

        // Act
        String dotString = callGraph.toDotString(basePackage);
        System.out.println(dotString); // Très utile pour déboguer le DOT généré

        // Assert
        // On vérifie la présence des éléments clés de la structure DOT
        assertThat(dotString).startsWith("digraph CallGraph {")
                .endsWith("}\n");

        // Vérifie la création des clusters pour chaque classe
        assertThat(dotString).contains("subgraph cluster_0")
                .contains("label = \"ServiceA\"");
        assertThat(dotString).contains("subgraph cluster_1")
                .contains("label = \"ServiceB\"");

        // Vérifie la déclaration des noeuds (méthodes) à l'intérieur des clusters
        assertThat(dotString).containsPattern("label=\"methode1\\(...\\)\""); // Pattern car l'ID n... peut changer
        assertThat(dotString).containsPattern("label=\"methode2\\(...\\)\"");
        assertThat(dotString).containsPattern("label=\"methode3\\(...\\)\"");

        // Vérifie la création des arêtes avec les bonnes couleurs
        assertThat(dotString).containsPattern("n\\d+ -> n\\d+ \\[color=\"black\"\\];");      // Appel de methode1 vers methode2
        assertThat(dotString).containsPattern("n\\d+ -> n\\d+ \\[color=\"darkgreen\"\\];"); // Instanciation de ServiceB

        // Vérifie que l'appel externe n'est PAS inclus, car le filtre doit l'enlever
        assertThat(dotString).doesNotContain("org.springframework.Helper");
    }

    @Test
    @DisplayName("toDotString doit inclure une légende correcte")
    void toDotString_shouldIncludeLegend() {
        // Arrange (pas besoin de données pour tester la légende)

        // Act
        String dotString = callGraph.toDotString("com.monprojet");

        // Assert
        assertThat(dotString).contains("subgraph cluster_legend")
                .contains("label = \"Légende\"")
                .contains("Appel de méthode")
                .contains("Instanciation (new)")
                .contains("Lève une exception");
    }
}