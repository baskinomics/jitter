package jitter.service;

import java.io.IOException;
import java.util.List;

import jitter.config.Config;
import jitter.domain.model.Report;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;

/**
 * 
 */
public interface ReportService {
    /**
     * Generates a pretty-printed string containing the relevant status changes.
     * 
     * @param report The report for which an output is to be generated.
     * @return The pretty-printed string.
     * @throws NoWorkTreeException
     * @throws GitAPIException
     * @throws IOException
     */
    String getReportOutput(Report report) throws NoWorkTreeException, GitAPIException, IOException;

    /**
     *
     * @param config
     * @return
     */
    List<Report> getReports(Config config);
}