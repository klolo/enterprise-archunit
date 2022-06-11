package pl.klolo.archtests.example.rules;

import com.tngtech.archunit.lang.ArchRule;
import pl.klolo.archtests.ruleset.api.EnterpriseArchTestRuleSet;

import java.io.Serializable;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Arch rules for writing domain business module.
 */
public class DomainModuleTestsRuleSet extends EnterpriseArchTestRuleSet {

    public final ArchRule testMethodPackagePrivate = classes()
            .should()
            .implement(Serializable.class);

    public DomainModuleTestsRuleSet() {
        register("domain_classes_implements_Serializable", testMethodPackagePrivate);
    }

}