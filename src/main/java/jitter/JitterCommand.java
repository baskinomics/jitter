package jitter;

import io.micronaut.configuration.picocli.PicocliRunner;
import jitter.service.ConfigService;
import jitter.service.ReportService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Command-line tool that generates a status report for the repositories defined within a user-provided configuration file.
 *
 * @author Sean Baskin
 */
@Command(
        name = "jitter",
        description = "Reports on the status of your git repositories.",
        mixinStandardHelpOptions = true)
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
    private List<Boolean> verbosity = new ArrayList<>();

    /**
     * The YAML configuration file.
     */
    @Option(
        names = {"-c", "--config"}, 
        description = "The configuration file to use.")
    private File configFile;

    /**
     * Singleton bean that exposes operations against the configuration file.
     */
    @Inject
    public ConfigService configService;

    /**
     * Singleton bean that exposes operations related to reports.
     */
    @Inject
    public ReportService reportService;

    /**
     * Runs a Picoli-based command in a Micronaut application context.
     *
     * @param args The program arguments.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        PicocliRunner.run(JitterCommand.class, args);
    }

    /**
     * Business logic of the Picocli command.
     */
    public void run() {
        // Verbosity
        logger.log(Level.INFO, "Checking [-v=<verbose>] flag.");
        setVerbosity(verbosity);

        // Config
        logger.log(Level.INFO, "Checking [-c=<config>] flag.");
        final var config = configService.getConfig(Optional.ofNullable(this.configFile));

        // Generate reports for each valid git repository.
        logger.log(Level.INFO, "Collating reports.");
        reportService.getReports(config).forEach(report -> {
            try {
                System.out.println(reportService.getReportOutput(report));
            } catch (NoWorkTreeException | GitAPIException | IOException e) {
                logger.error(e.getMessage());
            }
        });
    }

    /**
     * Determines the verbosity level for the given {@code verbosity} and sets the root logger level to a corresponding
     * value.
     *
     * @param verbosity The desired verbosity level.
     * @see LoggerContext
     * @see LoggerConfig
     */
    private void setVerbosity(final List<Boolean> verbosity) {
        final var loggerContext = (LoggerContext) LogManager.getContext(false);
        final var loggerConfig = loggerContext.getConfiguration();
        switch (verbosity.size()) {
            case 0:
                logger.log(Level.DEBUG, "Option -v was not provided. Using logger configuration default.");
                break;
            case 1:
                loggerConfig.getRootLogger().setLevel(Level.INFO);
                logger.log(Level.DEBUG, "Root logger level has been set to {}.", Level.INFO);
                break;
            case 2:
                loggerConfig.getRootLogger().setLevel(Level.DEBUG);
                logger.log(Level.DEBUG, "Root logger level has been set to {}.", Level.DEBUG);
                break;
            default:
                logger.log(Level.DEBUG, "Using configuration default root logger level {}",
                        loggerConfig.getRootLogger().getLevel().name());
                break;
        }

        // Update loggers
        loggerContext.updateLoggers();
        logger.log(Level.DEBUG, "Loggers have been updated.");
    }

    /**
     * Returns {@link this#configFile}.
     * 
     * @return The {@code configFile}.
     */
    public File getConfigFile() {
        return this.configFile;
    }

    /**
     * Returns {@link this#verbosity}.
     * 
     * @return The {@code verbosity}.
     */
    public List<Boolean> getVerbosity() {
        return Collections.unmodifiableList(this.verbosity);
    }
}
