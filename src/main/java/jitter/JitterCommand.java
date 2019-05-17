package jitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import io.micronaut.configuration.picocli.PicocliRunner;
import jitter.config.Config;
import jitter.domain.model.Report;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * 
 */
@Command(name = "jitter", description = "Reports on the status of your git repositories.", mixinStandardHelpOptions = true)
public class JitterCommand implements Runnable {

    /**
     * 
     */
    @Option(names = { "-v", "--verbose" }, description = "...")
    boolean verbose;

    /**
     * 
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
        // TODO Set a default git directory.
        if (config.exists() && config.isFile()) {
            // Instantiate YAML instance
            final var yaml = new Yaml(new Constructor(Config.class));
            try {
                // Should this be using the class loader? Can't remember.
                final var inputStream = new FileInputStream(config);
                // Deserialize yaml file to config instance
                final Config config = yaml.load(inputStream);
                //
                final var statusReports = new ArrayList<Report>();
                statusReports.addAll(
                    config.getRepositories().stream()
                        .flatMap(repo -> Stream.of(new File(repo + "/.git")))
                        .filter(repo -> repo.exists() && repo.isDirectory())
                        .flatMap(repo -> Stream.of(new Report(repo)))
                        .collect(Collectors.toList()));

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
}
