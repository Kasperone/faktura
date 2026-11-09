package io.github.kasperone.faktura.architecture;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Package boundary rules for the current feature layout.
 *
 * <p>Limitations (phase 0): main sources only; root {@code io.github.kasperone.faktura} not
 * modeled; no rules for Spring or JDK package depth. Cross-feature rules use {@code
 * allowEmptyShould(true)} while feature packages only contain {@code package-info}; they become
 * strict once real types exist.
 */
class ArchitectureRulesTest {

  private static final String FEATURE_ROOT = "io.github.kasperone.faktura";

  private static final String[] FEATURE_PACKAGE_PREFIXES = {
    FEATURE_ROOT + ".customer",
    FEATURE_ROOT + ".invoice",
    FEATURE_ROOT + ".organization",
    FEATURE_ROOT + ".ksef"
  };

  private static JavaClasses productionClasses;

  @BeforeAll
  static void importProductionClasses() {
    Path mainClasses = Path.of(System.getProperty("user.dir"), "target", "classes");
    productionClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPath(mainClasses);
  }

  @Test
  void configMustNotDependOnFeaturePackages() {
    ArchRule rule =
        classes()
            .that(resideUnder(FEATURE_ROOT + ".config"))
            .should()
            .onlyDependOnClassesThat()
            .resideOutsideOfPackages(FEATURE_PACKAGE_PREFIXES)
            .allowEmptyShould(false);
    rule.check(productionClasses);
  }

  @Test
  void sharedMustNotDependOnFeaturePackages() {
    ArchRule rule =
        classes()
            .that(resideUnder(FEATURE_ROOT + ".shared"))
            .should()
            .onlyDependOnClassesThat()
            .resideOutsideOfPackages(FEATURE_PACKAGE_PREFIXES)
            .allowEmptyShould(false);
    rule.check(productionClasses);
  }

  @Test
  void customerMustNotDependOnOtherFeatures() {
    assertFeatureDoesNotDependOnOtherFeatures(FEATURE_ROOT + ".customer");
  }

  @Test
  void invoiceMustNotDependOnOtherFeatures() {
    assertFeatureDoesNotDependOnOtherFeatures(FEATURE_ROOT + ".invoice");
  }

  @Test
  void organizationMustNotDependOnOtherFeatures() {
    assertFeatureDoesNotDependOnOtherFeatures(FEATURE_ROOT + ".organization");
  }

  @Test
  void ksefMustNotDependOnOtherFeatures() {
    assertFeatureDoesNotDependOnOtherFeatures(FEATURE_ROOT + ".ksef");
  }

  private void assertFeatureDoesNotDependOnOtherFeatures(String homePrefix) {
    String[] otherFeatures =
        java.util.Arrays.stream(FEATURE_PACKAGE_PREFIXES)
            .filter(prefix -> !prefix.equals(homePrefix))
            .toArray(String[]::new);

    ArchRule rule =
        classes()
            .that(resideUnder(homePrefix))
            .should()
            .onlyDependOnClassesThat()
            .resideOutsideOfPackages(otherFeatures)
            .allowEmptyShould(true);
    rule.check(productionClasses);
  }

  private static DescribedPredicate<JavaClass> resideUnder(String packagePrefix) {
    return describe(
        "reside under " + packagePrefix,
        javaClass -> {
          String pkg = javaClass.getPackageName();
          return pkg.equals(packagePrefix) || pkg.startsWith(packagePrefix + ".");
        });
  }
}
