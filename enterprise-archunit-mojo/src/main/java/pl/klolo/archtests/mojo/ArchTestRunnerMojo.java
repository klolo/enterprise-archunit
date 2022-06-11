package pl.klolo.archtests.mojo;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.DynamicTest;
import pl.klolo.archtests.mojo.configuration.ConfigurationParser;
import pl.klolo.archtests.mojo.junit.JunitLauncher;
import pl.klolo.archtests.mojo.junit.JunitTestBridge;
import pl.klolo.archtests.mojo.junit.JunitTestFromArchRulesFactory;
import pl.klolo.archtests.mojo.reflection.ClassFinder;
import pl.klolo.archtests.mojo.reflection.TargetClassLoaderExecutor;
import pl.klolo.archtests.ruleset.api.EnterpriseArchTestRuleSet;
import pl.klolo.archtests.ruleset.api.SkipArchitectureValidation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO: describe
 */
@Mojo(name = "archtest", defaultPhase = LifecyclePhase.TEST)
public class ArchTestRunnerMojo extends AbstractMojo {

    @Setter
    @Parameter(required = false, defaultValue = "archtest-config.yaml")
    private String configurationFile;

    @SneakyThrows
    public void execute() {
        getLog().info("start executing architecture tests");

        var classLoaderWithTargetClasses = new TargetClassLoaderExecutor(this);
        classLoaderWithTargetClasses.execute(this::executeArchTest);

        getLog().info("architecture tests verification done");
    }

    @SneakyThrows
    private void executeArchTest() {
        var project = (MavenProject) getPluginContext().get("project");
        var build = project.getBuild();
        var configuration = new ConfigurationParser(this)
                .parseConfiguration(configurationFile)
                .orElseThrow();

        Collection<Class<?>> targetClasses = Stream.of(build.getOutputDirectory(), build.getTestOutputDirectory())
                .map(ClassFinder::findIn)
                .flatMap(Collection::stream)
                .map(it -> loadClass(it, Thread.currentThread().getContextClassLoader()))
                .collect(Collectors.toList());

        var testStreams = new LinkedList<Stream<DynamicTest>>();

        for (var ruleSet : configuration.getRulesets()) {
            var ruleSetInstance = createRuleSetClassInstance(ruleSet.getClazz());

            testStreams.add(JunitTestFromArchRulesFactory.builder()
                    .basePackage(ruleSet.getApplyTo())
                    .ruleSet(ruleSetInstance)
                    .build()
                    .createTests(
                            new ClassFileImporter().importClasses(
                                    targetClasses.stream()
                                            .filter(it -> it.getPackage().getName().startsWith(ruleSet.getApplyTo()))
                                            .filter(it -> it.getDeclaredAnnotation(SkipArchitectureValidation.class) == null)
                                            .collect(Collectors.toList())
                            ),
                            ruleSet.getDisabled()
                    )
            );
        }

        JunitTestBridge.setArchUnitTests(
                testStreams.stream().flatMap(it -> it)
        );

        new JunitLauncher(this).launchClass(JunitTestBridge.class);
    }

    private EnterpriseArchTestRuleSet createRuleSetClassInstance(final String clazz) {
        try {
            return ((EnterpriseArchTestRuleSet) (
                    Class.forName(clazz)
                            .getConstructor()
                            .newInstance()
            ));
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot create instance of: " + clazz, e);
        }
    }

    @SneakyThrows
    private Class<?> loadClass(String fullName, ClassLoader classLoader) {
        return classLoader.loadClass(fullName);
    }

}
