package pl.klolo.archtests.mojo.configuration;

import lombok.Data;

import java.util.Set;

@Data
public class RuleSet {

    private String clazz;

    private String applyTo;

    private Set<String> disabled;

}