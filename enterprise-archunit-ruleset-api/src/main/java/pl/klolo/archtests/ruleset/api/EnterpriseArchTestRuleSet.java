package pl.klolo.archtests.ruleset.api;

import com.tngtech.archunit.lang.ArchRule;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public abstract class EnterpriseArchTestRuleSet {

    @Getter
    private List<EnterpriseArchRule> rules = new LinkedList<>();

    /**
     * This method allows you to configure globally all rules in set.
     */
    public ArchRule configureRule(final ArchRule rule) {
        return rule;
    }

    protected void register(String name, ArchRule rule) {
        rules.add(new EnterpriseArchRule(name, rule));
    }

}