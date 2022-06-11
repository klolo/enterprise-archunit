package pl.klolo.archtests.mojo.junit;

import com.tngtech.archunit.core.domain.JavaClasses;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import pl.klolo.archtests.ruleset.api.EnterpriseArchRule;
import pl.klolo.archtests.ruleset.api.EnterpriseArchTestRuleSet;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Create in runtime junit tests as a DynamicTest stream,
 * based on selected class with Archunit rules.
 */
@SuppressWarnings("NewClassNamingConvention")
@Slf4j
@Builder
public class JunitTestFromArchRulesFactory {

    private String basePackage;

    private EnterpriseArchTestRuleSet ruleSet;

    @TestFactory
    public Stream<DynamicTest> createTests(JavaClasses classes, Set<String> disabledRules) {
        validateParams();
        return ruleSet.getRules()
                .stream()
                .filter(rule -> disabledRules == null || !disabledRules.contains(rule.getName()))
                .map(rule -> configureRuleWithRuleSetCommonSettings(rule, ruleSet))
                .map(ruleTuple -> DynamicTest.dynamicTest(
                                ruleTuple.getName(),
                                () -> ruleTuple.getRule().check(classes)
                        )
                );
    }

    private EnterpriseArchRule configureRuleWithRuleSetCommonSettings(final EnterpriseArchRule rule, final EnterpriseArchTestRuleSet ruleSet) {
        return new EnterpriseArchRule(
                rule.getName(),
                ruleSet.configureRule(rule.getRule()).allowEmptyShould(true)
        );
    }

    private void validateParams() {
        Assertions.assertNotNull(basePackage);
        Assertions.assertNotNull(ruleSet);
    }

}