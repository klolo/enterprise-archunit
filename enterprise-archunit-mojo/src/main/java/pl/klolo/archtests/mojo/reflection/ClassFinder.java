package pl.klolo.archtests.mojo.reflection;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassFinder {

    @SneakyThrows
    public static List<String> findIn(String baseDir) {
        if (!new File(baseDir).exists()) {
            return new ArrayList<>();
        }

        return Files.walk(Paths.get(baseDir))
                .filter(Files::isRegularFile)
                .filter(it -> it.getName(it.getNameCount() - 1).toString().contains(".class"))
                .map(Path::toString)
                .map(it -> it.replaceAll(baseDir + "/", ""))
                .map(it -> it.replaceAll(".class", ""))
                .map(it -> it.replaceAll("\\/", "."))
                .collect(Collectors.toList());
    }

}
