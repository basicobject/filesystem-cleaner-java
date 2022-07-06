package main.java.fscleaner;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;

public class DotFileCleaner extends SimpleFileVisitor<Path> {
    final List<String> prefixes = List.of(".DS_Store", "._.DS_Store");
    @Override
    public FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs)  {
            if (attrs.isRegularFile()) {
                if (isDotFile(file)) {
//                     System.out.println("Dotfile found " + file.toString() + " Size " + attrs.size() / 1000 + "KB");
                    if (safeToRemove(file)) {
                        System.out.println("Safe to remove file " + file);
                    }
                }
            }
            return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if (exc instanceof AccessDeniedException)   {
            System.out.println("Access denied Skipping file " + file.getFileName().toString());
            return FileVisitResult.CONTINUE;
        }

        return super.visitFileFailed(file, exc);
    }

    private boolean isDotFile(Path file) {
      return file.getFileName().toString().startsWith(".");
    }

    private boolean safeToRemove(Path path) {
        var stringName = path.getFileName().toString();
        return prefixes.stream().anyMatch(stringName::endsWith);
    }
}
