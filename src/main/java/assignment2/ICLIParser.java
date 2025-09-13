/**
 * Interface for parsing and validating command-line arguments.
 */

package assignment2;

public interface ICLIParser {
    /**
     * Parses server URL from command-line arguments.
     * @param args Command-line arguments.
     * @return Server URL.
     * @throws IllegalArgumentException if URL is missing or invalid.
     */
    String parseServerUrl(String[] args) throws IllegalArgumentException;

    /**
     * Parses optional station ID from command-line arguments.
     * @param args Command-line arguments.
     * @return Station ID or null if not provided.
     */
    String parseStationId(String[] args);

    /**
     * Parses file path from command-line arguments.
     * @param args Command-line arguments.
     * @return File path.
     * @throws IllegalArgumentException if file path is missing.
     */
    String parseFilePath(String[] args) throws IllegalArgumentException;

    /**
     * Validates URL format.
     * @param url URL string to validate.
     * @throws IllegalArgumentException if URL is invalid.
     */
    void validateUrl(String url) throws IllegalArgumentException;

    /**
     * Extracts host from URL.
     * @param url Server URL.
     * @return Host name.
     */
    String parseHost(String url);

    /**
     * Extracts port from URL.
     * @param url Server URL.
     * @return Port number.
     */
    int parsePort(String url);
}