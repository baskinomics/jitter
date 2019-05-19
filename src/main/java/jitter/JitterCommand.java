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
 * TODO Documentation.
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
    private List<Boolean> verbose = new ArrayList<>();

    /**
     * The YAML configuration file.
     */
    @Option(names = {"-c", "--config"}, description = "The configuration file to use.")
    private File configFile;

    /**
     * TODO Documentation.
     */
    @Inject
    public ConfigService configService;

    /**
     * TODO Documentation.
     */
    @Inject
    public ReportService reportService;

    /**
     * Runs a Picoli-based command in a Micronaut application context.
     *
     * @param args TODO
     * @throws Exception TODO
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
        setVerbosity(verbose);

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
                logger.log(Level.DEBUG, "Option -v was not provided. Using configuration default.");
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

        loggerContext.updateLoggers();
        logger.log(Level.DEBUG, "Loggers have been updated.");
    }

    /**
     * @return
     */
    public File getConfigFile() {
        return this.configFile;
    }

    /**
     * @return
     */
    public List<Boolean> getVerbose() {
        return Collections.unmodifiableList(this.verbose);
    }
}
