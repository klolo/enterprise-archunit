package pl.klolo.archtests.mojo;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import pl.klolo.archtests.mojo.junit.JunitLauncher;
import pl.klolo.archtests.mojo.junit.JunitTestBridge;
import pl.klolo.archtests.mojo.junit.JunitTestFromArchRulesFactory;
import pl.klolo.archtests.mojo.reflection.ClassFinder;
import pl.klolo.archtests.mojo.reflection.TargetClassLoaderExecutor;
import pl.klolo.archtests.ruleset.api.EnterpriseArchTestRuleSet;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO: describe
 */
@Mojo(name = "archtest", defaultPhase = LifecyclePhase.TEST)
public class ArchTestRunnerMojo extends AbstractMojo {

    private String basePackage = "pl.klolo.arch.tests";

    /**
     * If true generation will be executed for yaml files not split by module.
     */
    @Setter
    @Parameter(required = true)
    private String[] ruleSet;

    @SneakyThrows
    public void execute() {
        getLog().info("start executing architecture tests");
        new TargetClassLoaderExecutor(this).execute(this::executeArchTest);
        getLog().info("architecture tests verification done");
    }

    @SneakyThrows
    private void executeArchTest() {
        var project = (MavenProject) getPluginContext().get("project");
        var build = project.getBuild();

        // TODO: add support to set list
        var ruleSetClass = ruleSet[0];
        var ruleSet = ((EnterpriseArchTestRuleSet) (
                Class.forName(ruleSetClass)
                        .getConstructor()
                        .newInstance()
        ));

        JunitTestBridge.setArchUnitTests(
                JunitTestFromArchRulesFactory.builder()
                        .basePackage(basePackage)
                        .ruleSet(ruleSet)
                        .build()
                        .createTests(
                                new ClassFileImporter()
                                        .importClasses(
                                                Stream.of(build.getOutputDirectory(), build.getTestOutputDirectory())
                                                        .map(ClassFinder::findIn)
                                                        .flatMap(Collection::stream)
                                                        .map(it -> loadClass(it, Thread.currentThread().getContextClassLoader()))
                                                        .collect(Collectors.toList())
                                        )
                        )
        );

        new JunitLauncher(this).launchClass(JunitTestBridge.class);
    }

    @SneakyThrows
    private Class<?> loadClass(String fullName, ClassLoader classLoader) {
        return classLoader.loadClass(fullName);
    }

}
