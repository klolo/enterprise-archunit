package pl.klolo.archtests.ruleset.api;

import com.tngtech.archunit.lang.ArchRule;

import java.util.List;

public interface EnterpriseArchTestRuleSet {

    List<EnterpriseArchRule> rules();

    /**
     * This method allows you to configure globally all rules in set.
     */
    default ArchRule configureRule(final ArchRule rule) {
        return rule;
    }

}