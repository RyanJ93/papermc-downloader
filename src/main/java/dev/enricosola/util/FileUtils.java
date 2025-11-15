package dev.enricosola.util;

import dev.enricosola.entity.Version;
import dev.enricosola.entity.Build;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;

public class FileUtils {
    private static final String BASE_FILE_TEMPLATE = "papermc_%s-%s.jar";

    /**
     * Constructs a file name for a server build based on the provided version and build details.
     *
     * @param version The {@link Version} object representing the server version, which contains an identifier.
     * @param build The {@link Build} object representing the specific build, which includes its ID.
     * @return A string representing the file name in the format "papermc_<version_id>-<build_id>.jar".
     */
    public static String buildFileName(Version version, Build build) {
        return String.format(FileUtils.BASE_FILE_TEMPLATE, version.getNumber(), build.getId());
    }

    /**
     * Constructs a complete file path based on the provided path, version, and build information.
     *
     * @param path The base path or directory where the file should be located, if null or blank, a default value of "./" is used.
     * @param version The {@link Version} object representing the server version, containing an identifier and associated builds.
     * @param build The {@link Build} object representing the specific build to be downloaded, including its ID and download URL.
     * @return A properly constructed file path pointing to the .jar file for the specified version and build.
     */
    public static String buildPath(String path, Version version, Build build) {
        if (path == null || path.isBlank()) {
            path = "./";
        }
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                String filename = FileUtils.buildFileName(version, build);
                path += path.endsWith("/") ? filename : "/" + filename;
            }
        } else {
            String extension = FileUtils.getExtension(path);
            if ( extension.isBlank() ) {
                String filename = FileUtils.buildFileName(version, build);
                path += path.endsWith("/") ? filename : "/" + filename;
            } else if ( !extension.equals("jar") ) {
                path += ".jar";
            }
        }
        return path;
    }

    /**
     * Checks if a file or directory exists at the specified path.
     *
     * @param path The path of the file or directory to check.
     * @return True if the file or directory exists, otherwise false.
     */
    public static boolean exists(String path) {
        return new File(path).exists();
    }

    /**
     * Deletes a file at the specified path if it exists.
     *
     * @param path The path of the file to be deleted.
     * @throws RuntimeException If an I/O error occurs while attempting to delete the file.
     */
    public static void cleanup(String path) {
        if ( FileUtils.exists(path) ) {
            try {
                Files.delete(Path.of(path));
            } catch (IOException ex) {
                throw new RuntimeException("Unable to delete previously downloaded file.", ex);
            }
        }
    }

    /**
     * Extracts the file extension from the provided file path.
     *
     * @param path The file path as a string. If the path is null or the file name does not contain an extension, an empty string is returned.
     * @return The file extension as a string (e.g., "txt", "jpg"). Returns an empty string if the file has no extension.
     */
    public static String getExtension(String path) {
        if (path == null) {
            return "";
        }
        String filename = Path.of(path).getFileName().toString();
        int index = filename.lastIndexOf('.');
        if (index <= 0 || index == filename.length() - 1) {
            return "";
        }
        return filename.substring(index + 1);
    }
}
