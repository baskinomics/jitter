package jitter.domain.model;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;

/**
 * 
 */
public class Report {

    /**
     * 
     */
    private Repository repository;

    /**
     * 
     */
    private Status status;

    /**
     * 
     */
    public Report(final Repository repository) {
        this.repository = repository;
        try (Git git = new Git(this.repository)) {
            //
            this.status = git.status().call();
        } catch (NoWorkTreeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return
     */
    public String generateReport() {
        final var builder = new StringBuilder();
        try {
            builder.append(String.format("[%s -> %s]\n", 
                    this.repository.getWorkTree().getName(), 
                    this.repository.getBranch()));

            // Determine if the repository is in a clean state.
            final var isClean = status.isClean();
            builder.append(String.format("clean: %b\n", isClean));

            if (!isClean) {
                // Modified files
                final var modified = status.getModified();
                if (!modified.isEmpty())
                    builder.append(String.format("modified: %s\n", modified.toString()));

                // Untracked files
                final var untracked = status.getUntracked();
                if (!untracked.isEmpty())
                    builder.append(String.format("untracked: %s\n", untracked.toString()));
            }
        } catch (NoWorkTreeException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return builder.toString();
    }
}