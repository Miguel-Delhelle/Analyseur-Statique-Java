// Fichier : src/test/java/um/ico/ingenierie/Metrics/MetricsCollectorTests.java

package um.ico.ingenierie.Core;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.ico.ingenierie.Models.AbstractSourceClass;
import um.ico.ingenierie.Models.AbstractSourcePackage;
import um.ico.ingenierie.graph.CallGraph;

import static org.mockito.Mockito.*;

// 1. ANNOTATION INDISPENSABLE pour activer Mockito dans nos tests JUnit 5
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires (avec mocks) pour la classe MetricsCollector")
class MetricsCollectorTests {

    // 2. DÉCLARATION DES MOCKS
    // Mockito va créer des "doublures" pour ces classes.
    // Elles n'auront aucune logique interne, nous les contrôlerons entièrement.
    @Mock
    private CompilationUnit mockCompilationUnit;

    @Mock
    private MetricsData mockMetricsData;

    @Mock
    private CallGraph mockCallGraph;

    // 3. INJECTION DES MOCKS
    // On demande à Mockito de créer une VRAIE instance de MetricsCollector,
    // mais d'y injecter nos MOCKS à la place des vrais objets.
    // NOTE : Cela ne fonctionne que si MetricsCollector a un constructeur qui accepte ces dépendances,
    // ou si on les injecte manuellement. On va le faire manuellement pour être plus clair.

    @Test
    @DisplayName("collectMetrics doit orchestrer correctement la visite et la mise à jour des données")
    void collectMetrics_shouldOrchestrateVisitAndUpdate() {
        // --- ARRANGE ---

        // On crée une instance réelle de l'objet à tester.
        // On lui passe nos mocks en paramètres.
        MetricsCollector metricsCollector = new MetricsCollector(mockCompilationUnit, mockMetricsData, mockCallGraph);

        // On prépare des objets factices qui seront retournés par notre futur visiteur
        AbstractSourceClass fakeClass = new AbstractSourceClass();
        fakeClass.setNameOfClass("FakeClass");
        AbstractSourcePackage fakePackage = new AbstractSourcePackage("fake.package");

        // Problème : `collectMetrics` crée `new MyVisitor(...)` à l'intérieur.
        // On ne peut pas "mocker" un `new`. C'est une limite du mocking simple.
        // Au lieu de mocker le visiteur, on va donc tester l'INTEGRATION entre MetricsCollector et un VRAI MyVisitor.
        // On va donc "stubber" (définir le comportement) des mocks dont le visiteur dépend.

        // On pourrait aller plus loin et mocker MyVisitor lui-même avec des techniques avancées (PowerMock),
        // mais pour ce projet, tester l'intégration `MetricsCollector -> MyVisitor` est plus simple et pertinent.
        // Le test suivant est donc un test d'intégration plus qu'un test unitaire pur.

        // --- ACT ---

        // On appelle la méthode à tester.
        // Cette méthode va appeler `mockCompilationUnit.accept(...)`
        metricsCollector.collectMetrics();

        // --- ASSERT ---

        // 4. VÉRIFICATION DES INTERACTIONS (C'est la partie la plus importante)

        // a) Vérifions que la méthode `accept` a bien été appelée sur notre mock de CompilationUnit.
        // `verify(mock)` permet de vérifier les appels.
        // `any(MyVisitor.class)` est un "matcher" qui dit "je me fiche de l'instance exacte du visiteur,
        // je veux juste vérifier que `accept` a été appelé avec N'IMPORTE QUEL objet de type MyVisitor".
        verify(mockCompilationUnit, times(1)).accept(any(MyVisitor.class));

        // b) Vérifions que la méthode `addClass` a bien été appelée sur notre mock de MetricsData.
        // Cela prouve que le collecteur a bien fait son travail de mise à jour à la fin.
        // `any(AbstractSourceClass.class)` vérifie qu'une classe a bien été passée.
        verify(mockMetricsData, times(1)).addClass(any(AbstractSourceClass.class));

        // c) On peut aussi vérifier qu'aucune autre méthode n'a été appelée sur nos mocks, si nécessaire.
        verifyNoMoreInteractions(mockMetricsData);
    }
}