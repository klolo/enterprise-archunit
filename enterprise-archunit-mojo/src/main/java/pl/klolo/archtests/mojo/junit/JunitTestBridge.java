package pl.klolo.archtests.mojo.junit;

import lombok.Setter;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Allows to executed dynamic created tests as regular junit tests method.
 * Probably you cannot do it with only <i>org.junit.platform.launcher.Launcher</i>.
 */
public class JunitTestBridge {

    @Setter
    private static Stream<DynamicTest> archUnitTests;

    @TestFactory
    public Stream<DynamicTest> createTests() {
        return archUnitTests;
    }

}