package pl.klolo.archtests.mojo.reflection;

import lombok.RequiredArgsConstructor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Class creates new class loader with all classes from target and execute callback code with new classloader.
 */
@RequiredArgsConstructor
public class TargetClassLoaderExecutor {

    private final AbstractMojo parent;

    public void execute(Runnable callback) {
        var classLoader = getClassLoader();
        var originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            callback.run();
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    private ClassLoader getClassLoader() {
        try {
            var project = (MavenProject) parent.getPluginContext().get("project");

            List<String> classpathElements = project.getCompileClasspathElements();
            classpathElements.add(project.getBuild().getOutputDirectory());
            classpathElements.add(project.getBuild().getTestOutputDirectory());
            URL[] urls = new URL[classpathElements.size()];

            for (int i = 0; i < classpathElements.size(); ++i) {
                urls[i] = new File(classpathElements.get(i)).toURL();
            }

            return new URLClassLoader(urls, this.getClass().getClassLoader());

        } catch (Exception e) {
            parent.getLog().debug("Couldn't get the classloader.");
            return this.getClass().getClassLoader();
        }
    }

}