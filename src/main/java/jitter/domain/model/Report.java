package jitter.domain.model;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * The class {@code Report} represents a stylized and formatted result of invoking the {@code git status} command.
 * 
 * @author Sean Baskin
 * @see Git
 * @see Status
 */
public class Report {
    /**
     * The {@link File} instance representing the git directory.
     */
    private File gitDirectory;

    /**
     * Allocates a {@code Report} object and initializes it with the given {@code gitDirectory}.
     * 
     * @param gitDirectory The {@link File} instance representing the git directory.
     * @see Repository
     */
    public Report(final File gitDirectory) {
        this.gitDirectory = gitDirectory;
    }

    /**
     * Generates a pretty-printed string containing the relevant status changes.
     *  
     * @return The pretty-printed string.
     * @throws GitAPIException
     * @throws NoWorkTreeException
     * @throws IOException
     */
    public String generateReport() throws NoWorkTreeException, GitAPIException, IOException {
        final var repository = new FileRepositoryBuilder().setGitDir(gitDirectory).build();
        final var status = Git.open(gitDirectory).status().call();
        final var builder = new StringBuilder();
        try {
            builder.append("\u001B[1m");
            builder.append(String.format("[%s:%s]", 
                repository.getWorkTree().getName(), 
                repository.getBranch()));
            builder.append("\u001b[0m\n");

            if (status.isClean()) {
                builder.append("CLEAN\n");
            } else {
                // Added
                if (!status.getAdded().isEmpty())
                    status.getAdded().forEach(file -> builder.append(String.format("\u001B[32madded: %s\u001b[0m\n", file)));

                // Modified files
                if (!status.getModified().isEmpty())
                    status.getModified().forEach(file -> builder.append(String.format("\u001B[31mmodified: %s\u001b[0m\n", file)));

                // Untracked files
                if (!status.getUntracked().isEmpty())
                    status.getUntracked().forEach(file -> builder.append(String.format("\u001B[31muntracked: %s\u001b[0m\n", file)));

                // Removed files
                if (!status.getRemoved().isEmpty())
                    status.getRemoved().forEach(file -> builder.append(String.format("\u001B[31mremoved: %s\u001b[0m\n", file)));
            }
        } catch (NoWorkTreeException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return builder.toString();
    }
}