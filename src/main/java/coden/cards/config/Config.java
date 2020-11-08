package coden.cards.config;

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

public class Config {

    public final String sender = getString("sender");

    private final Properties properties;

    public Config(final String path) { this(read(path)); }
    public Config(final Properties properties) {
        this.properties = requireNonNull(properties);
    }

    public static Properties read(final String path) {
        try {
            return read(new FileInputStream(requireNonNull(path)));
        } catch (final FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Properties read(final InputStream in) {
        // the properties class uses backslash as escape character, so we need to replace it
        try (Reader reader =
                new InputStreamReader(new BufferedInputStream(requireNonNull(in)), StandardCharsets.UTF_8)) {
            final Properties properties = new Properties();
            properties.load(reader);
            return properties;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        } catch (final Throwable t) {
            throw new UncheckedIOException(new IOException(t.getMessage(), t));
        }
    }

    public final List<String> getCommaSeparatedStringList(final String property) {
        try {
            final String csv = getString(property);
            return List.of(csv.split(","));
        } catch (final IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    protected String getProperty(final String property) {
        final String value = properties.getProperty(property);
        if (value == null) throw new IllegalArgumentException("Missing configuration property '" + property + "'.");
        return value;
    }

    public final String getString(final String property) {
        return getProperty(property);
    }
}
