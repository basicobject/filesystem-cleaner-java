package main.java.fscleaner;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DuplicateFileCleaner extends SimpleFileVisitor<Path> implements HelperMethods {
    public AtomicInteger filesCleaned = new AtomicInteger(0);
    public AtomicLong bytesRemoved = new AtomicLong(0);
    private final ConcurrentHashMap<String, Integer> checksumHashMap = new ConcurrentHashMap<>();

    private ExecutorService executor = Executors.newFixedThreadPool(8);
    private ConcurrentLinkedQueue<Future<?>> queue = new ConcurrentLinkedQueue();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        var result = executor.submit(() -> {
            if(attrs.isRegularFile()) {
                var checksum = getChecksum(file);

                if (checksum.length() > 0) {
                    if(checksumHashMap.containsKey(checksum)) {
                        try {
                            System.out.println("Duplicate file found " + file.getFileName());
                            checksumHashMap.put(checksum, checksumHashMap.get(checksum) + 1);
                            Files.delete(file);
                            filesCleaned.incrementAndGet();
                            bytesRemoved.set(bytesRemoved.get() + attrs.size());;
                        } catch (IOException e){
                            System.out.println("Unable to delete duplicate file " + file.getFileName());
                        }
                    } else {
                        checksumHashMap.put(checksum, 1);
                    }
                }
            }
        });

        queue.add(result);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if (exc instanceof AccessDeniedException) {
            System.out.println("Access denied Skipping file " + file.getFileName().toString());
            return FileVisitResult.CONTINUE;
        }

        return super.visitFileFailed(file, exc);
    }

    public void shutdownExecutorService() {
        executor.shutdown();
    }

    public boolean allDone() {
        var list = queue.stream().toList();
        return list.stream().map((x) -> x.isDone()).allMatch((x) -> x == true);
    }

    private String getChecksum(Path file)  {
        try(var stream = Files.newInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA1");

            var bytes = new byte[4096];
            var bytesRead = 0;

            while((bytesRead = stream.read(bytes)) != -1) {
                digest.update(bytes, 0, bytesRead);
            }

            var digestBytes = digest.digest();
            var buffer = new StringBuffer("");
            for (var c: digestBytes) {
                buffer.append(Integer.toString((c & 0xff) + 0x100, 16)).substring(1);
            }

//            System.out.println("Checksum for file " + file.getFileName() + " " + buffer.toString());
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Failing to initialize the algorithm");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Error in calculating checksum");
        }

        return "";
    }
}


