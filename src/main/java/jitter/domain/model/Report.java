package jitter.domain.model;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Repository;

import java.io.File;

/**
 * The class {@code Report} represenSts a stylized and formatted result of invoking the {@code git status} command.
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
     * TODO Documentation.
     *
     * @return TODO
     */
    public File getGitDirectory() {
        return this.gitDirectory;
    }
}