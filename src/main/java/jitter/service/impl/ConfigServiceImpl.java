package jitter.service.impl;

import jitter.domain.model.Config;
import jitter.service.ConfigService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * TODO Documentation.
 */
@Singleton
public class ConfigServiceImpl implements ConfigService {

    /**
     * Class logger.
     */
    private static final Logger logger = LogManager.getLogger(ConfigServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Config getConfig(final Optional<File> file) {
        if (file.isPresent() && file.get().exists() && file.get().isFile()) {
            logger.log(Level.DEBUG, "Configuration file [{}] is being used.",
                    file.get().getAbsolutePath());

            // Instantiate YAML instance
            final var yaml = new Yaml(new Constructor(Config.class));
            try {
                // Should this be using the class loader? Can't remember.
                final var inputStream = new FileInputStream(file.get());
                // Deserialize yaml file to config instance.
                return yaml.load(inputStream);
            } catch (FileNotFoundException e) {
                logger.debug("Unable to find file.");
                logger.error(e.getMessage());
                return new Config();
            }
        } else {
            logger.log(Level.DEBUG, "No configuration file provided.");
            System.out.println("No configuration file provided.");
            return new Config();
        }
    }

}