package se.bjurr.violations.lib;

import static java.util.logging.Level.FINE;
import static se.bjurr.violations.lib.reports.ReportsFinder.findAllReports;
import static se.bjurr.violations.lib.util.Utils.checkNotNull;
import static se.bjurr.violations.lib.util.Utils.setReporter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.parsers.ViolationsParserWithParameters;
import se.bjurr.violations.lib.reports.Parser;

public class ViolationsApi {
  private static Logger LOG = Logger.getLogger(ViolationsApi.class.getSimpleName());

  private String pattern;
  private Parser parser;
  private File startFile;
  private String reporter;
  private Map<String, String> parameters = new HashMap<String, String>();

  public static String getDetailedReport(final List<Violation> violations) {
    return new DetailedReportCreator(violations) //
        .create();
  }

  public static ViolationsApi violationsApi() {
    return new ViolationsApi();
  }

  private ViolationsApi() {}

  public ViolationsApi findAll(final Parser parser) {
    this.parser = checkNotNull(parser, "parser");
    return this;
  }

  public ViolationsApi withReporter(final String reporter) {
    this.reporter = checkNotNull(reporter, "reporter");
    return this;
  }

  /**
   * Pattern when using this becomes
   * violationsApi().withPattern("a").inFolder("b").givenParameter("paramA",
   * "aValue").findAll(parser).violations()
   */
  public ViolationsApi givenParameter(String name, String value) {
    parameters.put(name, value);
    return this;
  }

  public ViolationsApi inFolder(final String folder) {
    startFile = new File(checkNotNull(folder, "folder"));
    if (!startFile.exists()) {
      throw new RuntimeException(folder + " not found");
    }
    return this;
  }

  public List<Violation> violations() {
    final List<File> includedFiles = findAllReports(startFile, pattern);
    if (LOG.isLoggable(FINE)) {
      LOG.log(FINE, "Found " + includedFiles.size() + " reports:");
      for (final File f : includedFiles) {
        LOG.log(FINE, f.getAbsolutePath());
      }
    }
    addParametersToParser();
    final List<Violation> foundViolations = parser.findViolations(includedFiles);
    final boolean reporterWasSupplied =
        reporter != null && !reporter.trim().isEmpty() && !reporter.equals(parser.name());
    if (reporterWasSupplied) {
      setReporter(foundViolations, reporter);
    }

    if (LOG.isLoggable(FINE)) {
      LOG.log(FINE, "Found " + foundViolations.size() + " violations:");
      for (final Violation v : foundViolations) {
        LOG.log(
            FINE,
            v.getReporter()
                + " "
                + v.getSeverity()
                + " ("
                + v.getRule()
                + ") "
                + v.getFile()
                + " "
                + v.getStartLine()
                + " -> "
                + v.getEndLine());
      }
    }
    return foundViolations;
  }

  private void addParametersToParser() {
    if (parser.getViolationsParser() instanceof ViolationsParserWithParameters) {
      ViolationsParserWithParameters p =
          (ViolationsParserWithParameters) parser.getViolationsParser();
      p.clearParameters();
      parameters.forEach((name, value) -> p.setParameter(name, value));
    }
  }

  private String makeWindowsFriendly(final String regularExpression) {
    return regularExpression.replace("/", "(?:/|\\\\)");
  }

  public ViolationsApi withPattern(final String regularExpression) {
    pattern = makeWindowsFriendly(regularExpression);
    return this;
  }
}
