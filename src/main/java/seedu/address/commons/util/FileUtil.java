package seedu.address.commons.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";

    public static boolean isFileExists(Path file) {
        return Files.exists(file) && Files.isRegularFile(file);
    }

    /**
     * Returns true if {@code path} can be converted into a {@code Path} via {@link Paths#get(String)},
     * otherwise returns false.
     * @param path A string representing the file path. Cannot be null.
     */
    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException ipe) {
            return false;
        }
        return true;
    }

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (!isFileExists(file)) {
            createFile(file);
        }
    }

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     */
    public static void createFile(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    public static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists
     */
    public static String readFromFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file), CHARSET);
    }

    /**
     * Writes given string to a file.
     * Will create the file if it does not exist yet.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        Files.write(file, content.getBytes(CHARSET));
    }

    /**
     * Returns the first matching {@code .csv} file path that corresponds to the given {@code partialPath}.
     * The search is performed within the user's {@code ~/Downloads} directory.
     *
     * If {@code partialPath} is empty or {@code null}, the method lists the user's home directory
     * and returns the first {@code .csv} file found. If {@code partialPath} refers to a directory
     * or ends with a path separator, it lists the directory's contents and returns the first
     * matching {@code .csv} file. Otherwise, it searches for files in the parent directory
     * whose names start with the given prefix.
     *
     * @param partialPath A partially typed file path or file name. May be {@code null}.
     * @return The string representation of the first matching {@code .csv} file path, or {@code null}
     *         if no match is found or an error occurs.
     */
    public static String getFirstMatchingFile(String partialPath) {
        try {
            final Path cwd = Paths.get(System.getProperty("user.home"), "Downloads");

            String input = (partialPath == null) ? "" : partialPath.trim();

            if (input.isEmpty()) {
                Path home = Paths.get(System.getProperty("user.home"));
                if (!Files.isDirectory(home)) {
                    return null;
                }
                try (var stream = Files.list(home)) {
                    return stream
                            .map(Path::getFileName)
                            .map(Path::toString)
                            .filter(name -> name.endsWith(".csv")) // optional
                            .sorted()
                            .findFirst()
                            .map(name -> home.resolve(name).toString())
                            .orElse(null);
                }
            }

            Path raw = Paths.get(input);
            boolean endsWithSeparator =
                    input.endsWith("/") || input.endsWith("\\"); // handle both

            // Resolve to absolute for listing
            Path resolved = raw.isAbsolute() ? raw : cwd.resolve(raw).normalize();

            // Decide directory to list and prefix to match
            Path dirToList;
            String prefix;

            if (endsWithSeparator || Files.isDirectory(resolved)) {
                // User typed a directory (or ended with slash): list inside it
                dirToList = resolved;
                prefix = "";
            } else {
                // User typed something like "path/to/pa" → list parent and match "pa"
                Path parent = resolved.getParent();
                dirToList = (parent == null) ? cwd : parent;
                Path leaf = resolved.getFileName(); // guaranteed non-null here
                prefix = leaf.toString();
            }

            if (!Files.isDirectory(dirToList)) {
                return null;
            }

            // Compute what to return: keep original parent from user input so we
            // return a path that "completes" what they typed
            Path userParent = raw.getParent(); // may be null
            try (var stream = Files.list(dirToList)) {
                Optional<String> first = stream
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .filter(name -> name.startsWith(prefix))
                        .filter(name -> name.endsWith(".csv")) // optional: only CSVs
                        .sorted()
                        .findFirst();

                if (first.isEmpty()) {
                    return null;
                }

                Path completed = (userParent == null)
                        ? Paths.get(first.get())
                        : userParent.resolve(first.get());

                // Return as a string exactly what should appear after the command word
                return completed.normalize().toString();
            }
        } catch (Exception e) {
            return null;
        }
    }
}
