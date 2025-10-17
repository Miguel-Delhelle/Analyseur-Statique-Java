// Fichier : src/test/java/um/ico/ingenierie/traitementFile/CodeAnalyzerTests.java

package um.ico.ingenierie.JavaFilesHandler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import um.ico.ingenierie.Core.CodeAnalyzer;
import um.ico.ingenierie.Core.MetricsData;
import um.ico.ingenierie.graph.CallGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Tests d'intégration pour la classe CodeAnalyzer")
class CodeAnalyzerTests {

    // 1. ANNOTATION MAGIQUE
    // JUnit va créer un dossier temporaire et injecter son chemin dans cette variable avant chaque test.
    @TempDir
    Path tempDir;

    @Test
    @DisplayName("doit analyser correctement un projet simple avec deux classes")
    void analyze_shouldCorrectlyParseSimpleProject() throws IOException {
        // --- ARRANGE ---

        // 2. CRÉATION D'UNE FAUSSE ARBORESCENCE DE PROJET
        // On crée un sous-dossier pour simuler la structure 'src/main/java'
        Path srcDir = Files.createDirectories(tempDir.resolve("src/main/java/com/monprojet"));

        // On crée un premier faux fichier Java
        String classAContent = """
            package com.monprojet;
            public class ClassA {
                public void hello() {
                    new ClassB().world();
                }
            }
        """;
        Files.writeString(srcDir.resolve("ClassA.java"), classAContent);

        // On crée un second faux fichier Java
        String classBContent = """
            package com.monprojet;
            public class ClassB {
                public void world() {}
            }
        """;
        Files.writeString(srcDir.resolve("ClassB.java"), classBContent);

        // On crée un fichier non-Java qui doit être ignoré
        Files.writeString(srcDir.resolve("README.md"), "Ceci est un test.");

        // --- ACT ---

        // 3. ON LANCE L'ANALYSEUR sur la racine de notre projet temporaire
        CodeAnalyzer codeAnalyzer = new CodeAnalyzer(tempDir.toString());

        // --- ASSERT ---

        // 4. ON VÉRIFIE LES RÉSULTATS
        MetricsData metricsData = codeAnalyzer.getMetricsData();
        CallGraph callGraph = codeAnalyzer.getCallGraph();

        // Vérifications sur les métriques
        assertThat(metricsData.getNumberOfClass()).isEqualTo(2);
        assertThat(metricsData.getNumberOfPackage()).isEqualTo(1);
        assertThat(metricsData.getAbstractSourceClassList())
                .extracting("nameOfClass") // AssertJ est très puissant !
                .containsExactlyInAnyOrder("ClassA", "ClassB");

        // Vérifications sur le graphe d'appel
        String expectedCaller = "com.monprojet.ClassA#hello()";
        String expectedCallee = "com.monprojet.ClassB#world()";
        String expectedInstantiation = "com.monprojet.ClassB#ClassB()";

        assertThat(callGraph.toString())
                .contains(expectedCaller)
                .contains(expectedCallee)
                .contains(expectedInstantiation);
    }

    @Test
    @DisplayName("doit lever une exception si le répertoire ne contient aucun fichier .java")
    void constructor_shouldThrowException_whenNoJavaFilesFound() throws IOException {
        // --- ARRANGE ---
        // On crée juste un fichier non-Java dans le dossier temporaire
        Files.writeString(tempDir.resolve("config.xml"), "<config/>");

        // --- ACT & ASSERT ---
        // On vérifie qu'une exception est bien levée lors de la création du CodeAnalyzer
        // (car le constructeur de JavaFilesHandler va échouer)
        assertThatThrownBy(() -> {
            new CodeAnalyzer(tempDir.toString());
        })
                .isInstanceOf(RuntimeException.class) // L'exception est encapsulée dans une RuntimeException
                .hasCauseInstanceOf(IOException.class); // On vérifie la cause racine
    }
}