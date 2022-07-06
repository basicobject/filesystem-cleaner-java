package main.java.fscleaner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;

public class FileSystemCleaner extends SimpleFileVisitor<Path> {
    private final Path rootPath;
    private final DotFileCleaner dotFileCleaner = new DotFileCleaner();
    FileSystemCleaner(String rootPath) {
        this.rootPath = Path.of(rootPath);
    }

    void clean() {
      try {
          Files.walkFileTree(rootPath, dotFileCleaner);
      } catch (Exception e) {
          e.printStackTrace();
      }
    }
}
