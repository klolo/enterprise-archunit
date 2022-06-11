package pl.klolo.archtests.mojo.configuration;

import lombok.Data;

import java.util.Set;

@Data
public class RuleSet {

    private String rulesetClass;

    private String applyToPackage;

    private Set<String> disableRule;

}