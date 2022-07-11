package main.java.fscleaner;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class DotFileCleaner extends SimpleFileVisitor<Path> {
    final static List<String> SUFFIXES = List.of(".DS_Store", "._.DS_Store",".JPG", ".jpg", ".JPEG", ".jpeg");
    final static List<String> PREFIXES = List.of("._IMG");
    public int filesCleaned = 0;
    public long bytesRemoved = 0;

    public String humanBytesRemoved() {
        if(bytesRemoved < 1000) return bytesRemoved + " bytes";
        else {
            return (bytesRemoved/1000) + "KB";
        }
    }
    @Override
    public FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs)  {
            if (attrs.isRegularFile() && isDotFile(file) && safeToRemove(file)) {
                System.out.println("Safe to remove file " + file);
                try {
                    Files.delete(file);
                    filesCleaned ++;
                    bytesRemoved += Files.size(file);
                } catch (IOException e) {
                    System.err.println("Failed to delete file " + file.getFileName());
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
        return SUFFIXES.stream().anyMatch(stringName::endsWith) || PREFIXES.stream().anyMatch(stringName::startsWith);
    }
}
