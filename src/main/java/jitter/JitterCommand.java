package jitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import io.micronaut.configuration.picocli.PicocliRunner;
import jitter.config.Config;
import jitter.domain.model.Report;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "jitter", description = "Reports on the status of your git repositories.", mixinStandardHelpOptions = true)
public class JitterCommand implements Runnable {

    @Option(names = { "-v", "--verbose" }, description = "...")
    boolean verbose;

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
        if (config.exists() && config.isFile()) {
            // Instantiate YAML instance
            final var yaml = new Yaml(new Constructor(Config.class));
            try {
                // Should this be using the class loader? Can't remember.
                final var inputStream = new FileInputStream(config);
                // Deserialize yaml file to config instance
                final Config config = yaml.load(inputStream);
                // 
                for (String repository : config.getRepositories()) {
                    //
                    final File repositoryDir = new File(repository + "/.git");
                    if (repositoryDir.exists() && repositoryDir.isDirectory()) {
                        final var repo = new FileRepositoryBuilder().setGitDir(repositoryDir).build();
                        final var report = new Report(repo);
                        System.out.println(report.generateReport());
                    }
                }
            } catch (IOException | NoWorkTreeException e) {
                e.printStackTrace();
            }
        }        
    }
}
