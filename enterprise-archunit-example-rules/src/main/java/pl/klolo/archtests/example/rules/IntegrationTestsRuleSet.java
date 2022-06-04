package pl.klolo.archtests.example.rules;

import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/**
 * Arch rules for writing integration tests.
 */
public class IntegrationTestsRuleSet {

    public static final ArchRule methodNameStartWithShould =  methods()
            .that()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith("Test")
            .and()
            .areAnnotatedWith(Test.class)
            .should()
            .haveNameStartingWith("should")
            .allowEmptyShould(true); // FIXME: should be global

    public static final ArchRule testMethodArePackagePrivate = methods()
            .that()
            .areAnnotatedWith(Test.class)
            .should()
            .bePackagePrivate()
            .allowEmptyShould(true); // FIXME: should be global

}