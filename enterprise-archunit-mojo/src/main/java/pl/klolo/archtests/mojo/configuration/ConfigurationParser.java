package pl.klolo.archtests.mojo.configuration;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@RequiredArgsConstructor
public class ConfigurationParser {

    private final AbstractMojo parent;

    @SneakyThrows
    public Optional<RuleSetsConfiguration> parseConfiguration(String filePath) {
        var configFilePath = ((MavenProject) parent.getPluginContext().get("project"))
                .getBasedir() + File.separator + filePath;

        var plainConfiguration = String.join("\n", Files.readAllLines(Paths.get(configFilePath)));

        Yaml yaml = new Yaml();
        var result = yaml.loadAs(plainConfiguration, RuleSetsConfiguration.class);
        return Optional.of(result);
    }

}