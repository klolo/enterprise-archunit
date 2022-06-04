package pl.klolo.archtests.ruleset.api;

import com.tngtech.archunit.lang.ArchRule;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class EnterpriseArchRule {
    private final String name;
    private final ArchRule rule;
}