package main.java.fscleaner;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemCleaner {
    private final Path rootPath;
    private final DotFileCleaner dotFileCleaner = new DotFileCleaner();
    FileSystemCleaner(String rootPath) {
        this.rootPath = Path.of(rootPath);
    }

    void clean() {
      try {
          Files.walkFileTree(rootPath, dotFileCleaner);
          System.out.println("Total number of files deleted " + dotFileCleaner.filesCleaned);
          System.out.println("Total disk space freed " + dotFileCleaner.bytesRemoved);
      } catch (Exception e) {
          e.printStackTrace();
          System.exit(-1);
      }
    }
}
