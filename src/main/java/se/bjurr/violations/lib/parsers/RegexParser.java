package se.bjurr.violations.lib.parsers;

import static se.bjurr.violations.lib.model.Violation.violationBuilder;
import static se.bjurr.violations.lib.reports.Parser.REGEX;
import static se.bjurr.violations.lib.util.ViolationParserUtils.getLines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;

public class RegexParser implements ViolationsParserWithParameters {

  private Pattern regex;

  @Override
  public List<Violation> parseReportOutput(String reportContent) throws Exception {
    if (regex == null)
      throw new IllegalStateException(
          "The REGEX violations parser requires that the regex parameter has been set using the setParameter method");

    List<Violation> violations = new ArrayList<>();
    List<String> lines = getLines(reportContent);
    for (String line : lines) {
      Matcher matcher = regex.matcher(line);
      if (matcher.find()) {
        violations.add(
            violationBuilder()
                .setParser(REGEX)
                .setStartLine(parseInt(getGroup(matcher, "lineNumber")))
                .setFile(getGroup(matcher, "fileName"))
                .setRule(getGroup(matcher, "rule"))
                .setColumn(parseInt(getGroup(matcher, "column")))
                .setSeverity(SEVERITY.WARN)
                .setMessage(getGroup(matcher, "message"))
                .setCategory(getGroup(matcher, "category"))
                .build());
      }
    }
    return violations;
  }

  @Override
  public void setParameter(String name, String value) throws IllegalArgumentException {
    if (!"regex".equals(name)) {
      throw new IllegalArgumentException(
          "Illegal parameter name passed to the regex parser: \"" + name + "\"");
    }
    if (!containsAllMandatoryGroups(value)) {
      throw new IllegalArgumentException(
          "The regular expression must contain named groups called \"fileName\", \"lineNumber\" and \"message\".");
    }
    try {
        regex = Pattern.compile(value);
    } catch (PatternSyntaxException ex) {
        throw new IllegalArgumentException("The string \"" + value + "\" is not a valid regular expression.");
    }
  }

  private boolean containsAllMandatoryGroups(String value) {
    if (value == null) return false;
    if (!value.contains("(?<message>")) return false;
    if (!value.contains("(?<fileName>")) return false;
    if (!value.contains("(?<lineNumber>")) return false;
    return true;
  }

  @Override
  public String parameterDescription(String name) {
    if ("regex".equals(name)) {
      return "A Java-flavored regular expression. The regex must contain the named groups \"fileName\", \"lineNumber\", and \"message\","
          + "and may optionally contain any of the named groups \"rule\", \"category\" or \"column\". "
          + "The regex is checked against each line of the report content.";
    }
    return "";
  }

  @Override
  public List<String> parameterNames() {
    return Arrays.asList("regex");
  }

  private String getGroup(Matcher matcher, String groupName) {
    try {
      return matcher.group(groupName);
    } catch (IllegalArgumentException ex) { // Group name doesn't exist
      return "";
    }
  }

  private Integer parseInt(String val) {
    try {
      return Integer.parseInt(val);
    } catch (NumberFormatException nfe) {
      return 0;
    }
  }

  @Override
  public void clearParameters() {
    regex = null;
  }
}
