package main.java.fscleaner;

import java.util.List;

public interface HelperMethods {
     default String humanBytesRemoved(long bytesCount) {
        List<String> UNITS = List.of("bytes", "KB", "MB", "GB", "TB");
        var i = 0;

        while(bytesCount > 1000 && i <= UNITS.size() - 1) {
            bytesCount = bytesCount / 1000;
            i++;
        }

        return Long.toString(bytesCount) + " " + UNITS.get(i);
    }
}
