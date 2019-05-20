package jitter.service;

import jitter.domain.model.Config;
import jitter.domain.model.Report;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;

import java.io.IOException;
import java.util.List;

/**
 * TODO Documentation.
 */
public interface ReportService {
    /**
     * Generates a pretty-printed string containing the relevant status changes.
     * 
     * @param report The report for which an output is to be generated.
     * @return The pretty-printed string.
     * @throws NoWorkTreeException TODO
     * @throws GitAPIException TODO
     * @throws IOException TODO
     */
    String getReportOutput(Report report) throws NoWorkTreeException, GitAPIException, IOException;

    /**
     * Returns a collection of {@link Report} instances for the given {@code config}.
     *
     * @param config TODO
     * @return TODO
     */
    List<Report> getReports(Config config);
}