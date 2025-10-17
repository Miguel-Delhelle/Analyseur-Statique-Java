package um.ico.ingenierie.Core;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import um.ico.ingenierie.graph.CallGraph;
import um.ico.ingenierie.graph.Edge;
import um.ico.ingenierie.graph.EdgeType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests unitaires pour la logique de MyVisitor")
class MyVisitorTests {

    // Note : On n'utilise pas @BeforeEach ici car chaque test a une configuration unique.

    @Test
    @DisplayName("doit collecter les informations d'une classe (nom, package, lignes, attributs, méthodes)")
    void visitor_shouldCollectBasicClassInfo() {
        // Arrange
        String javaCode = """
            package com.monprojet;
            
            public class MaClasse { // Ligne 3
                private String nom;
                private int age;
                
                public MaClasse() {} // Constructeur
                
                public void methode1() {
                    // corps de la méthode
                }
            } // Ligne 11
        """;

        MetricsData metricsData = new MetricsData();
        CallGraph callGraph = new CallGraph();
        CompilationUnit cu = parseJavaCode(javaCode);

        // Act
        MyVisitor visitor = new MyVisitor(metricsData, cu, callGraph);
        cu.accept(visitor);

        // On simule ce que le MetricsCollector ferait après la visite
        metricsData.addClass(visitor.getSourceClass());

        // Assert
        assertThat(metricsData.getNumberOfClass()).isEqualTo(1);
        var collectedClass = metricsData.getAbstractSourceClassList().getFirst();

        assertThat(collectedClass.getNameOfClass()).isEqualTo("MaClasse");
        assertThat(collectedClass.getPackageParent().getName()).isEqualTo("com.monprojet");
        assertThat(collectedClass.getNumberOfAttributs()).isEqualTo(2);
        assertThat(collectedClass.getNumberOfLinesInClass()).isEqualTo(9); // Ligne 11 - 3 + 1
        assertThat(collectedClass.getNumberOfMethods()).isEqualTo(1); // La méthode getNumberOfMethods ignore les constructeurs
        assertThat(collectedClass.getAbstractSourceMethodsList()).hasSize(2); // ...mais la liste contient bien les deux (méthode + constructeur)
    }

    @Test
    @DisplayName("doit identifier les appels (CALL), instanciations (INSTANTIATION) et exceptions (THROWS)")
    void visitor_shouldBuildCallGraphCorrectly() {
        // Arrange
        String javaCode = """
            package com.monprojet;
            import java.io.IOException;

            public class ServiceA {
                public void doWork() {
                    ServiceB serviceB = new ServiceB(); // INSTANTIATION
                    serviceB.process(); // CALL
                    if (true) {
                        throw new IOException("error"); // THROWS
                    }
                }
            }
            class ServiceB { public void process() {} }
        """;

        MetricsData metricsData = new MetricsData();
        CallGraph callGraph = new CallGraph();
        CompilationUnit cu = parseJavaCode(javaCode);

        // Act
        MyVisitor visitor = new MyVisitor(metricsData, cu, callGraph);
        cu.accept(visitor);

        // Assert
        String caller = "com.monprojet.ServiceA#doWork()";
        Edge instantiationEdge = new Edge("com.monprojet.ServiceB#ServiceB()", EdgeType.INSTANTIATION);
        Edge callEdge = new Edge("com.monprojet.ServiceB#process()", EdgeType.CALL);
        Edge throwsEdge = new Edge("java.io.IOException#IOException(java.lang.String)", EdgeType.THROWS);

        // On inspecte directement l'état du CallGraph qui a été rempli par le visiteur
        assertThat(callGraph.toString())
                .contains(instantiationEdge.toString())
                .contains(callEdge.toString())
                .contains(throwsEdge.toString());
    }

    /**
     * Méthode utilitaire qui parse une chaîne de code Java et retourne l'AST (CompilationUnit).
     * C'est le cœur de notre stratégie de test pour le visiteur.
     */
    private CompilationUnit parseJavaCode(String javaCode) {
        ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
        parser.setSource(javaCode.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setEnvironment(new String[]{}, new String[]{}, null, true);
        parser.setUnitName("Test.java");

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        assertThat(cu).isNotNull();
        return cu;
    }
}