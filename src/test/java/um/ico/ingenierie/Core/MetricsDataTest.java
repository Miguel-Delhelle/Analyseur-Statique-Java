package um.ico.ingenierie.Core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import um.ico.ingenierie.Models.AbstractSourceAttributs;
import um.ico.ingenierie.Models.AbstractSourceClass;
import um.ico.ingenierie.Models.AbstractSourceMethods;
import um.ico.ingenierie.Models.AbstractSourcePackage;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

@DisplayName("Tests unitaires pour la classe MetricsData")
class MetricsDataTests {

    private MetricsData metricsData;

    @BeforeEach
    void setUp() {
        metricsData = new MetricsData();
    }

    // === Test de la logique de base (ajout et duplication) ===

    @Test
    @DisplayName("addClass doit ajouter une nouvelle classe et son package")
    void addClass_shouldAddNewClassAndPackage() {
        // Arrange
        AbstractSourceClass classA = createTestClass("com.test", "ClassA");

        // Act
        metricsData.addClass(classA);

        // Assert
        assertThat(metricsData.getNumberOfClass()).isEqualTo(1);
        assertThat(metricsData.getNumberOfPackage()).isEqualTo(1);
        assertThat(metricsData.getAbstractSourceClassList().get(0).getNameOfClass()).isEqualTo("ClassA");
        assertThat(metricsData.getAbstractPackageSourceList().get(0).getName()).isEqualTo("com.test");
    }

    @Test
    @DisplayName("addClass doit empêcher l'ajout de classes en double")
    void addClass_shouldPreventDuplicates() {
        // Arrange
        AbstractSourceClass classA_v1 = createTestClass("com.test", "ClassA");
        classA_v1.addAttributs(new AbstractSourceAttributs()); // Version 1 a 1 attribut

        AbstractSourceClass classA_v2 = createTestClass("com.test", "ClassA");
        classA_v2.addAttributs(new AbstractSourceAttributs());
        classA_v2.addAttributs(new AbstractSourceAttributs()); // Version 2 a 2 attributs

        // Act
        metricsData.addClass(classA_v1);
        metricsData.addClass(classA_v2); // Tente d'ajouter la même classe (nom + package)

        // Assert
        assertThat(metricsData.getNumberOfClass()).isEqualTo(1);
        // Doit contenir la PREMIÈRE version ajoutée
        assertThat(metricsData.getAbstractSourceClassList().get(0).getNumberOfAttributs()).isEqualTo(1);
    }

    // === Tests pour les getters dynamiques (totaux) ===

    @Test
    @DisplayName("getTotalNumberOfMethods doit sommer correctement les méthodes de toutes les classes")
    void getTotalNumberOfMethods_shouldSumAllMethods() {
        // Arrange
        AbstractSourceClass classA = createTestClassWithMethods("com.test", "ClassA", 3);
        AbstractSourceClass classB = createTestClassWithMethods("com.test", "ClassB", 5);
        metricsData.addClass(classA);
        metricsData.addClass(classB);

        // Act
        int totalMethods = metricsData.getTotalNumberOfMethods();

        // Assert
        assertThat(totalMethods).isEqualTo(8);
    }

    @Test
    @DisplayName("getTotalNumberOfLines doit sommer correctement les lignes de toutes les méthodes")
    void getTotalNumberOfLines_shouldSumAllLines() {
        // Arrange
        AbstractSourceClass classA = createTestClass("com.test", "ClassA");
        classA.addMethod(new AbstractSourceMethods(null, false, "m1", null, null, 15));
        classA.addMethod(new AbstractSourceMethods(null, false, "m2", null, null, 20)); // Total 35

        AbstractSourceClass classB = createTestClass("com.test", "ClassB");
        classB.addMethod(new AbstractSourceMethods(null, false, "m3", null, null, 50)); // Total 50

        metricsData.addClass(classA);
        metricsData.addClass(classB);

        // Act
        int totalLines = metricsData.getTotalNumberOfLines();

        // Assert
        assertThat(totalLines).isEqualTo(85);
    }

    // === Tests pour les calculs de moyenne (qui utilisent les getters dynamiques) ===

    @Test
    @DisplayName("getAverageNumberMethodsByClass doit calculer la moyenne correcte")
    void getAverageNumberMethodsByClass_shouldCalculateCorrectAverage() {
        // Arrange
        metricsData.addClass(createTestClassWithMethods("com.test", "ClassA", 2));
        metricsData.addClass(createTestClassWithMethods("com.test", "ClassB", 4));

        // Act
        double average = metricsData.getAverageNumberMethodsByClass();

        // Assert : (2 + 4) / 2 = 3.0
        assertThat(average).isEqualTo(3.0);
    }

    @Test
    @DisplayName("getAverageNumberMethodsByClass doit retourner 0 si aucune classe")
    void getAverageNumberMethodsByClass_shouldReturnZeroWhenNoClasses() {
        // Act & Assert
        assertThat(metricsData.getAverageNumberMethodsByClass()).isZero();
    }


    // === Méthodes utilitaires pour créer des données de test propres ===

    private AbstractSourceClass createTestClass(String packageName, String className) {
        AbstractSourcePackage pkg = new AbstractSourcePackage(packageName);
        AbstractSourceClass cls = new AbstractSourceClass();
        cls.setNameOfClass(className);
        cls.setPackageParent(pkg);
        return cls;
    }

    private AbstractSourceClass createTestClassWithMethods(String packageName, String className, int methodCount) {
        AbstractSourceClass testClass = createTestClass(packageName, className);
        for (int i = 0; i < methodCount; i++) {
            testClass.addMethod(new AbstractSourceMethods(testClass, false, "method" + i, Collections.emptyList(), null, 10));
        }
        return testClass;
    }
}