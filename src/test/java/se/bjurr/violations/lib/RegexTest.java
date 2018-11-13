package se.bjurr.violations.lib;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static se.bjurr.violations.lib.model.Violation.violationBuilder;
import static se.bjurr.violations.lib.reports.Parser.REGEX;

import java.util.List;
import org.junit.Test;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.parsers.RegexParser;

public class RegexTest {

  RegexParser parser = new RegexParser();


  @Test
  public void testRegex() throws Exception {
    String warning = "myfile.java, row 32, col 15: Dangeroos kangaroos (rule 2) (Wildlife error)";
    String regex =
        "(?<fileName>.*), row (?<lineNumber>\\d+), col (?<column>\\d+): (?<message>.*) \\((?<rule>.*)\\) \\((?<category>.*)\\)";

    parser.setParameter("regex", regex);

    List<Violation> violations = parser.parseReportOutput(warning);

    assertEquals(1, violations.size());

    Violation expected = violationBuilder()
            .setParser(REGEX)
            .setSeverity(SEVERITY.WARN)
            .setFile("myfile.java")
            .setStartLine(32)
            .setColumn(15)
            .setMessage("Dangeroos kangaroos")
            .setCategory("Wildlife error")
            .setRule("rule 2")
            .build();
    
    assertEquals(expected, violations.get(0));
  }

  @Test
  public void nonMatchingLinesAreIgnored() throws Exception {
    String warning =
        "myfile.java, line 32: Dangeroos kangaroos\n"
            + "Some irrelevant info\n"
            + "anotherfile.c, line 10: Errorrrr";
    String regex = "(?<fileName>.*), line (?<lineNumber>\\d+): (?<message>.*)";

    parser.setParameter("regex", regex);

    List<Violation> violations = parser.parseReportOutput(warning);

    assertEquals(2, violations.size());
  }

  @Test(expected = IllegalStateException.class)
  public void throwsExceptionIfNoRegexSet() throws Exception {
    RegexParser p = new RegexParser();
    p.parseReportOutput("Hello");
  }

  @Test
  public void setParameterThrowsExceptionOnUnknownParameterName() {
    try {
      parser.setParameter("NotAParameter", "");
      fail("Expected exception not thrown");
    } catch (Exception ex) {
      assertEquals(
          "Illegal parameter name passed to the regex parser: \"NotAParameter\"", ex.getMessage());
    }
  }

  @Test
  public void setRegexThrowsExceptionOnMissingNamedGroup() {
    try {
      parser.setParameter("regex", "(?<fileName>.*)(?<lineNumber>.*)");
      fail("Exception not thrown");
    } catch (IllegalArgumentException ex) {
      assertEquals(
          "The regular expression must contain named groups called \"fileName\", \"lineNumber\" and \"message\".",
          ex.getMessage());
    }
    try {
      parser.setParameter("regex", "(?<message>.*)(?<lineNumber>.*)");
      fail("Exception not thrown");
    } catch (IllegalArgumentException ex) {
      assertEquals(
          "The regular expression must contain named groups called \"fileName\", \"lineNumber\" and \"message\".",
          ex.getMessage());
    }
    try {
      parser.setParameter("regex", "(?<fileName>.*)(?<message>.*)");
      fail("Exception not thrown");
    } catch (IllegalArgumentException ex) {
      assertEquals(
          "The regular expression must contain named groups called \"fileName\", \"lineNumber\" and \"message\".",
          ex.getMessage());
    }
    try {
      parser.setParameter("regex", "(?<fileName>a)(?<message>b)(?<lineNumber>c)");
    } catch (IllegalArgumentException ex) {
      fail("Exception thrown on good input");
    }
  }

  @Test
  public void checkParameterList() {
    List<String> params = parser.parameterNames();
    assertEquals(1, params.size());
    assertEquals("regex", params.get(0));
  }
  
  @Test
  public void exceptionWhenSettingInvalidRegex() {
    String illegalRegex = "A regex) (?<fileName>a)(?<message>b)(?<lineNumber>c)";
    try {
      parser.setParameter("regex", illegalRegex);
      fail("Expected exception not thrown");
    }
    catch (IllegalArgumentException ex) {
      assertEquals("The string \"" + illegalRegex + "\" is not a valid regular expression.",
                   ex.getMessage());
    }
  }
  
  @Test
  public void getDescriptionOfRegexParameter() {
    String desc = parser.parameterDescription("regex");
    assertFalse(desc.isEmpty());
  }
}
