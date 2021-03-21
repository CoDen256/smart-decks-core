package coden.decks.core.config;

import static java.util.Objects.requireNonNull;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * The {@code Config} represents the basic configuration file, that can be extended with custom fields
 * that have to be read. It reads the config file and extract the fields.
 */
public class Config {

    /** The inner properties in form of key-value */
    private final Properties properties;

    /**
     * Creates a new {@code Config} from the file on the given path.
     *
     * @param path
     *         the path to config file
     */
    public Config(String path) {
        this(read(path));
    }

    /**
     * Creates a new {@code Config} from the given {@link Properties}
     *
     * @param properties
     *         the configuration properties
     */
    public Config(Properties properties) {
        this.properties = requireNonNull(properties);
    }

    /**
     * Reads config file from the given {@link InputStream}
     *
     * @param is
     *         the input stream
     */
    public Config(InputStream is) {
        this(read(is));
    }

    /**
     * The utility method to get list from the comma separated list property.
     * For example: "test.someList = 1,2, 3, string, test"
     *
     * @param property
     *         the property in list format
     * @return the list of values
     */
    protected List<String> getCommaSeparatedStringList(String property) {
        try {
            String csv = getString(property);
            return Arrays.asList(csv.split("\\s?,\\s?"));
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    /**
     * The utility method to read string property
     * @param property the property name to read
     * @return the value of the given property.
     */
    protected String getString(String property) {
        String value = properties.getProperty(property);
        if (value == null) throw new IllegalArgumentException("Missing configuration property '" + property + "'.");
        return value;
    }

    /**
     * The utility method to read file on the given path to {@link Properties}
     * @param path the path to file to read from
     * @return the properties
     */
    protected static Properties read(String path) {
        try {
            return read(new FileInputStream(requireNonNull(path)));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * The utility method to read given input stream to {@link Properties}
     * @param in the input stream
     * @return the properties
     */
    protected static Properties read(InputStream in) {
        // the properties class uses backslash as escape character, so we need to replace it
        try (Reader reader =
                     new InputStreamReader(new BufferedInputStream(requireNonNull(in)), StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(reader);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Throwable t) {
            throw new UncheckedIOException(new IOException(t.getMessage(), t));
        }
    }
}
