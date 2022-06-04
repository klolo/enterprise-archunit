package pl.klolo.archtests.mojo.junit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.yaml.snakeyaml.Yaml;
import pl.klolo.archtests.ruleset.api.EnterpriseArchRule;
import pl.klolo.archtests.ruleset.api.EnterpriseArchTestRuleSet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Create in runtime junit tests as a DynamicTest stream,
 * based on selected class with Archunit rules.
 */
@Slf4j
@Builder
public class JunitTestFromArchRulesFactory {

    private static final String CONFIG_FILENAME = "archtest.yaml";

    private String basePackage;

    private EnterpriseArchTestRuleSet ruleSet;

    @TestFactory
    public Stream<DynamicTest> createTests(JavaClasses classes) {
        validateParams();
        var configuration = loadConfiguration();
        return ruleSet.rules()
                .stream()
                .filter(rule -> !isRuleDisabled(configuration, ruleSet.getClass().getSimpleName(), rule.getName()))
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

    private boolean isRuleDisabled(Map<String, Map<String, Boolean>> configuration, String ruleSetClass, String ruleName) {
        if (configuration.containsKey(ruleSetClass) && configuration.get(ruleSetClass) != null) {
            var rulesConfiguration = configuration.get(ruleSetClass);
            var ruleEnable = rulesConfiguration.getOrDefault(ruleName, true);
            if (!ruleEnable) {
//                LOGGER.warn("Rule {}::{} is disabled", fieldDeclarationClass, field.getName());
                return true;
            }
        }

        return false;
    }

    private void validateParams() {
        Assertions.assertNotNull(basePackage);
        Assertions.assertNotNull(ruleSet);
    }

    private Map<String, Map<String, Boolean>> loadConfiguration() {
        var inputStream = JunitTestFromArchRulesFactory.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILENAME);

        if (inputStream != null) {
            Yaml yaml = new Yaml();
            return yaml.load(
                    new BufferedReader(
                            new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                            .lines()
                            .collect(Collectors.joining("\n"))
            );
        }

        return new HashMap<>();
    }

}