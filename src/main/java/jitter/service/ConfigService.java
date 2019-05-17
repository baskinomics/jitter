package jitter.service;

import java.io.File;
import java.util.Optional;

import jitter.config.Config;

/**
 * 
 */
public interface ConfigService {
    /**
     * 
     * @param file
     * @return
     */
    Config getConfig(Optional<File> file);
}