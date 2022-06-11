package pl.klolo.archtests.example.rules;

import org.junit.jupiter.api.Test;
import pl.klolo.archtests.ruleset.api.EnterpriseArchRule;
import pl.klolo.archtests.ruleset.api.EnterpriseArchTestRuleSet;

import java.util.Arrays;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/**
 * Arch rules for writing integration tests.
 */
public class IntegrationTestsRuleSet implements EnterpriseArchTestRuleSet {

    @Override
    public List<EnterpriseArchRule> rules() {
        return Arrays.asList(
                EnterpriseArchRule.builder()
                        .name("test_method_are_package_private")
                        .rule(
                                methods()
                                        .that()
                                        .areAnnotatedWith(Test.class)
                                        .should()
                                        .bePackagePrivate()
                                        .allowEmptyShould(true) // FIXME: should be global
                        )
                        .build(),
                EnterpriseArchRule.builder()
                        .name("test_method_name_start_with_should")
                        .rule(
                                methods()
                                        .that()
                                        .areDeclaredInClassesThat()
                                        .haveSimpleNameEndingWith("Test")
                                        .and()
                                        .areAnnotatedWith(Test.class)
                                        .should()
                                        .haveNameStartingWith("should")
                                        .allowEmptyShould(true)
                        )
                        .build()
        );
    }
}