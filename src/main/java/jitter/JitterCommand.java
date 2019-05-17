package jitter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;

import io.micronaut.configuration.picocli.PicocliRunner;
import jitter.domain.model.Report;
import jitter.service.ConfigService;
import jitter.service.ReportService;
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
            names = {"-v", "--verbose"},
            description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.")
    private List<Boolean> verbose = new ArrayList<>();

    /**
     * The YAML configuration file.
     */
    @Option(names = {"-c", "--config"}, description = "The configuration file to use.")
    private File configFile;

    @Inject
    public ConfigService configService;

    @Inject
    public ReportService reportService;

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
        // Verbosity
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

        // Config
        logger.log(Level.INFO, "Checking [-c=<config>] flag.");
        final var config = configService.getConfig(Optional.ofNullable(this.configFile));

        // Generate reports for each valid git repository.
        logger.log(Level.INFO, "Collating reports.");
        reportService.getReports(config).forEach(report -> {
            try {
                System.out.println(reportService.getReportOutput(report));
            } catch (NoWorkTreeException | GitAPIException | IOException e) {
                e.printStackTrace();
            }
        });
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
        final var loggerConfig = loggerContext.getConfiguration();
        loggerConfig.getRootLogger().setLevel(level);
        loggerContext.updateLoggers();

        logger.log(Level.DEBUG, "verbose: {}", verbose);
        logger.log(Level.DEBUG, "Root logger level has been set to {}.", level.name());
    }
}
