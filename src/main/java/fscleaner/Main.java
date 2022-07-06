package main.java.fscleaner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please input the root directory path as argument");
            System.exit(-1);
        }

        System.out.println("Cleaning the root directory " + args[0]);

        var cleaner = new FileSystemCleaner(args[0]);
        cleaner.clean();
    }
}
