/**
 * Interface for parsing and validating command-line arguments.
 */
public interface ICLIParser {
    /**
     * Parses server URL from command-line arguments.
     * @param args Command-line arguments.
     * @return Server URL.
     */
    String parseServerUrl(String[] args);

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
     */
    String parseFilePath(String[] args);

    /**
     * Validates URL format.
     * @param url URL string to validate.
     */
    void validateUrl(String url);

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