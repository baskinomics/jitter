package com.baskinomics.jitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.baskinomics.jitter.config.Config;
import com.baskinomics.jitter.domain.model.Report;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * TODO Documentation.
 * 
 * @author Sean Baskin
 */
@Command(name = "jitter", description = "Reports on the status of your git repositories.", mixinStandardHelpOptions = true)
public class JitterCommand implements Runnable {
    /**
     * Class logger.
     */
    private static final Logger logger = LogManager.getLogger(JitterCommand.class);

    /**
     * The verbosity level.
     */
    @Option(
        names = { "-v", "--verbose"}, 
        description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.")
    private List<Boolean> verbose = new ArrayList<>();

    /**
     * The YAML configuration file.
     */
    @Option(names = { "-c", "--config" }, description = "The configuration file to use.")
    private File config;

    /**
     * Runs a Picoli-based command in a Micronaut application context.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        PicocliRunner.run(JitterCommand.class, args);
    }

    /**
     * TODO Documentaton.
     */
    public void run() {
        logger.log(Level.INFO, "Checking [-v=<verbose>] flag.");
        switch (verbose.size()) {
            case 1:
                setRootLoggerLevel(Level.INFO);
                break;

            case 2:
                setRootLoggerLevel(Level.DEBUG);
                break;

            default:
                // Use the configuration default
                break;
        }

        logger.log(Level.INFO, "Checking [-c=<config>] flag.");
        if (config.exists() && config.isFile()) {
            logger.log(Level.DEBUG, "Configuration file [{}] is being used.", config.getAbsolutePath());

            // Instantiate YAML instance
            final var yaml = new Yaml(new Constructor(Config.class));
            try {
                // Should this be using the class loader? Can't remember.
                final var inputStream = new FileInputStream(config);
                // Deserialize yaml file to config instance.
                final Config config = yaml.load(inputStream);
                // Generate reports for each valid git repository.
                final var statusReports = new ArrayList<Report>();
                statusReports.addAll(
                    config.getRepositories().stream()
                        .flatMap(repo -> Stream.of(new File(repo + "/.git")))
                        .filter(repo -> repo.exists() && repo.isDirectory())
                        .flatMap(repo -> Stream.of(new Report(repo)))
                        .collect(Collectors.toList()));
                // Print each report to STDOUT
                statusReports.forEach(report -> {
                    try {
                        System.out.println(report.generateReport());
                    } catch (NoWorkTreeException | GitAPIException | IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException | NoWorkTreeException e) {
                e.printStackTrace();
            }
        }        
    }

    /**
     * Sets the root logger's level to the given {@code level}.
     * 
     * @param level The {@link Level} to set.
     * @see LoggerContext
     * @see LoggerConfig
     */
    private void setRootLoggerLevel(final Level level) {
        final var loggerContext = (LoggerContext) LogManager.getContext(false);
        final var loggerConfig =  loggerContext.getConfiguration();
        loggerConfig.getRootLogger().setLevel(level);
        loggerContext.updateLoggers();
        
        logger.log(Level.DEBUG, "verbose: {}", verbose);
        logger.log(Level.DEBUG, "Root logger level has been set to Level.DEBUG.");
    }
}
