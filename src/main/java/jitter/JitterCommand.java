package jitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import io.micronaut.configuration.picocli.PicocliRunner;
import jitter.config.Config;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "jitter", description = "Reports on the status of your git repositories.", mixinStandardHelpOptions = true)
public class JitterCommand implements Runnable {

    @Option(names = { "-v", "--verbose" }, description = "...")
    boolean verbose;

    @Option(names = { "-c", "--config" }, description = "The configuration file to use.")
    private File config;

    /**
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
            final Yaml yaml = new Yaml(new Constructor(Config.class));
            try {
                // Should this be using the class loader? Can't remember.
                final InputStream inputStream = new FileInputStream(config);
                // Deserialize yaml file to config instance
                final Config config = yaml.load(inputStream);
                // 
                for (String repository : config.getRepositories()) {
                    //
                    final File repositoryDir = new File(repository + "/.git");
                    if (repositoryDir.exists() && repositoryDir.isDirectory()) {
                        final Repository repo = new FileRepositoryBuilder().setGitDir(repositoryDir).build();
                        final Git git = new Git(repo);
                        final boolean isClean = git.status().call().isClean();
                        System.out.println(String.format("%s is clean? %s", repositoryDir, isClean));
                    }
                }
            } catch (IOException | NoWorkTreeException | GitAPIException e) {
                e.printStackTrace();
            }
        }        
    }
}
