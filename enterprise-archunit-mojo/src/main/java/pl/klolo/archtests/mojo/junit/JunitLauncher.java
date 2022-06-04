package pl.klolo.archtests.mojo.junit;

import lombok.RequiredArgsConstructor;
import org.apache.maven.plugin.AbstractMojo;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.stream.Collectors;

/**
 * Executes select junit 5 test.
 */
@RequiredArgsConstructor
public class JunitLauncher {

    private final AbstractMojo parent;

    public void launchClass(final Class<?> clazz) {
        var launcher = LauncherFactory.create();
        var listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(
                LauncherDiscoveryRequestBuilder
                        .request()
                        .selectors(DiscoverySelectors.selectClass(clazz))
                        .build()
        );

        var summary = listener.getSummary();
        printSummary(summary);
        if (summary.getTotalFailureCount() > 0) {
            var errors = summary.getFailures().stream()
                    .map(it -> it.getException().getMessage())
                    .collect(Collectors.joining("\n"));
            throw new IllegalStateException("!!! Architecture is broken !!!\n" + errors);
        }
    }

    private void printSummary(final TestExecutionSummary summary) {
        parent.getLog().info(String.format("Passed tests: %d/%d", summary.getTestsSucceededCount(), summary.getTestsFoundCount()));
        summary.getFailures().forEach(it -> parent.getLog().error(it.getException()));
    }

}
