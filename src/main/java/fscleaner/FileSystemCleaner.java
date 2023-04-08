package main.java.fscleaner;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemCleaner {
    private final Path rootPath;
//    private final DotFileCleaner dotFileCleaner = new DotFileCleaner();
    private final DuplicateFileCleaner duplicateFileCleaner = new DuplicateFileCleaner();
    FileSystemCleaner(String rootPath) {
        this.rootPath = Path.of(rootPath);
    }

    void clean() {
      try {
//          Files.walkFileTree(rootPath, dotFileCleaner);
          Files.walkFileTree(rootPath, duplicateFileCleaner);
          System.out.println("Total number of files deleted " + duplicateFileCleaner.filesCleaned);
          System.out.println("Total disk space freed " + duplicateFileCleaner.humanBytesRemoved(duplicateFileCleaner.bytesRemoved));
      } catch (Exception e) {
          e.printStackTrace();
          System.exit(-1);
      }
    }
}
