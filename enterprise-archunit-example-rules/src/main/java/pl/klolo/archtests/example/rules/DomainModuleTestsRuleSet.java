package pl.klolo.archtests.example.rules;

import lombok.Getter;
import pl.klolo.archtests.ruleset.api.EnterpriseArchRule;
import pl.klolo.archtests.ruleset.api.EnterpriseArchTestRuleSet;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Arch rules for writing domain business module.
 */
public class DomainModuleTestsRuleSet implements EnterpriseArchTestRuleSet {

    @Getter
    private final String name = "DomainArchTestRuleSet";

    @Override
    public List<EnterpriseArchRule> rules() {
        return Arrays.asList(
                EnterpriseArchRule.builder()
                        .name("domainClassesImplementsSerializable")
                        .rule(
                                classes()
                                        .should()
                                        .implement(Serializable.class)
                        )
                        .build()
        );
    }

}