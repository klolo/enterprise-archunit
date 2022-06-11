package pl.klolo.archtests.example.rules;

import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import pl.klolo.archtests.ruleset.api.EnterpriseArchTestRuleSet;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/**
 * Arch rules for writing integration tests.
 */
public class IntegrationTestsRuleSet extends EnterpriseArchTestRuleSet {

    public final ArchRule testMethodPackagePrivate = methods()
            .that()
            .areAnnotatedWith(Test.class)
            .should()
            .bePackagePrivate();

    public final ArchRule testMethodNameStartWithShould = methods()
            .that()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith("Test")
            .and()
            .areAnnotatedWith(Test.class)
            .should()
            .haveNameStartingWith("should");

    public IntegrationTestsRuleSet() {
        register("test_method_are_package_private", testMethodPackagePrivate);
        register("test_method_name_start_with_should", testMethodNameStartWithShould);
    }

}