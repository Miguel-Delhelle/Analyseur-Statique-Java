package um.ico.ingenierie.Analysis.core; // Adapte le package à ta nouvelle structure

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import um.ico.ingenierie.Common.Exceptions.NoJavaPathExceptions;
import um.ico.ingenierie.Analysis.Models.MetricsData;
import um.ico.ingenierie.Analysis.Models.graph.CallGraph;
import um.ico.ingenierie.JavaFilesHandler.IJavaFilesHandler;
import um.ico.ingenierie.JavaFilesHandler.JavaFilesHandlerPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Tests d'intégration pour la classe CodeAnalyzer")
class CodeAnalyzerTests {

    // JUnit va créer un dossier temporaire unique pour chaque test
    // et injecter son chemin dans cette variable.
//    @TempDir
//    Path tempDir;
//
//    @Test
////    @DisplayName("doit analyser correctement un projet simple et produire les métriques et le graphe")
//    void analyze_shouldCorrectlyParseSimpleProject() throws IOException {
//        // --- ARRANGE ---
//
//        // On crée une arborescence de projet réaliste dans le dossier temporaire.
//        // C'est crucial car notre JavaFilesHandler filtre sur "src/main".
//        Path srcDir = Files.createDirectories(tempDir.resolve("src/main/java/com/testprojet"));
//
//        // On crée deux faux fichiers Java avec une interaction
//        String classAContent = """
//            package com.testprojet;
//            public class ServiceA {
//                public void executer() {
//                    new Utilitaire().formater();
//                }
//            }
//        """;
//        Files.writeString(srcDir.resolve("ServiceA.java"), classAContent);
//
//        String classBContent = """
//            package com.testprojet;
//            public class Utilitaire {
//                public void formater() {}
//            }
//        """;
//        Files.writeString(srcDir.resolve("Utilitaire.java"), classBContent);
//
//        // On crée un fichier non-Java qui doit être ignoré
//        Files.writeString(srcDir.resolve("README.md"), "Test");
//
//        // On instancie la stratégie de handler concrète qu'on veut tester
//        IJavaFilesHandler handler = new JavaFilesHandlerPath(tempDir.toString());
//
//        // --- ACT ---
//
//        // On lance l'analyseur avec notre handler. L'analyse se fait dans le constructeur.
//        CodeAnalyzer codeAnalyzer = new CodeAnalyzer(handler);
//
//        // --- ASSERT ---
//
//        MetricsData metricsData = codeAnalyzer.getMetricsData();
//        CallGraph callGraph = codeAnalyzer.getCallGraph();
//
//        // 1. Vérifier les métriques
//        assertThat(metricsData.getNumberOfClass()).isEqualTo(2);
//        assertThat(metricsData.getNumberOfPackage()).isEqualTo(1);
//        assertThat(metricsData.getAbstractSourceClassList())
//                .extracting("nameOfClass") // Utilise la puissance d'AssertJ
//                .containsExactlyInAnyOrder("ServiceA", "Utilitaire");
//
//        // 2. Vérifier le graphe d'appel
//        String caller = "com.testprojet.ServiceA#executer()";
//        String callee = "com.testprojet.Utilitaire#formater()";
//        String instantiation = "com.testprojet.Utilitaire#Utilitaire()"; // Le constructeur
//
//        assertThat(callGraph.toString())
//                .contains(caller)
//                .contains(callee)
//                .contains(instantiation);
//    }

//    @Test
//    @DisplayName("doit échouer si le handler ne trouve aucun fichier .java valide")
//    void constructor_shouldFail_whenHandlerFindsNoJavaFiles() throws IOException {
//        // --- ARRANGE ---
//        // On crée un dossier avec une structure mais sans fichiers .java dans src/main
//        Files.createDirectories(tempDir.resolve("src/test/java"));
//        Files.writeString(tempDir.resolve("pom.xml"), "<project/>");
//
//        // --- ACT & ASSERT ---
//        // On vérifie que la création du HANDLER lève bien une exception,
//        // car c'est lui qui est responsable de valider la source.
//        assertThatThrownBy(() -> {
//            new JavaFilesHandlerPath(tempDir.toString());
//        })
//                .isInstanceOf(NoJavaPathExceptions.class)
//                .hasMessageContaining("Aucun fichier .java trouvé dans src/main");
//    }
}