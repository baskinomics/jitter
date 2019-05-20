package jitter.service;

import jitter.domain.model.Config;

import java.io.File;
import java.util.Optional;

/**
 * TODO Documentation.
 */
public interface ConfigService {
    /**
     * Provides a {@link Config} instance that is either deserialized from the given {@code file} value if present, or
     * a new instance if not.
     *
     * @param file TODO
     * @return TODO
     */
    Config getConfig(Optional<File> file);
}