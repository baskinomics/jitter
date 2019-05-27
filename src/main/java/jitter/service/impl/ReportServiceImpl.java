package jitter.service.impl;

import jitter.domain.model.Config;
import jitter.domain.model.Report;
import jitter.service.ReportService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO Documentation.
 */
@Singleton
public class ReportServiceImpl implements ReportService {
    /**
     * Class logger.
     */
    private static final Logger logger = LogManager.getLogger(ReportServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReportOutput(Report report) throws NoWorkTreeException, GitAPIException, IOException {
        final var repository = new FileRepositoryBuilder().setGitDir(report.getGitDirectory()).build();
        final var status = Git.open(report.getGitDirectory()).status().call();
        final var builder = new StringBuilder();

        logger.log(Level.DEBUG, "Generating report for {}:{}",
                repository.getWorkTree().getName(), repository.getBranch());

        builder.append("\u001B[1m");
        builder.append(String.format("[%s:%s]",
                repository.getWorkTree().getName(),
                repository.getBranch()));
        builder.append("\u001b[0m\n");

        logger.debug("isClean: {}", status.isClean());
        if (!status.isClean()) {
            // Added
            if (!status.getAdded().isEmpty())
                status.getAdded().forEach(file -> {
                    builder.append(String.format("\u001B[32madded: %s\u001b[0m\n", file));
                });

            // Modified files
            if (!status.getModified().isEmpty())
                status.getModified().forEach(file -> {
                    builder.append(String.format("\u001B[31mmodified: %s\u001b[0m\n", file));
                });

            // Untracked files
            if (!status.getUntracked().isEmpty())
                status.getUntracked().forEach(file -> {
                    builder.append(String.format("\u001B[31muntracked: %s\u001b[0m\n", file));
                });

            // Removed files
            if (!status.getRemoved().isEmpty())
                status.getRemoved().forEach(file -> builder.append(String.format("\u001B[31mremoved: %s\u001b[0m\n", file)));
        } else {
            builder.append("CLEAN\n");
        }

        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Report> getReports(final Config config) {
        return config.getRepositories().stream()
                .flatMap(path -> Stream.of(new File(path + "/.git")))
                .filter(gitDirectory -> gitDirectory.exists() && gitDirectory.isDirectory())
                .flatMap(validGitDirectory -> Stream.of(new Report(validGitDirectory)))
                .collect(Collectors.toList());
    }

}