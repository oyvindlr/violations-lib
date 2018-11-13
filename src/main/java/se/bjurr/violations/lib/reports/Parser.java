package se.bjurr.violations.lib.reports;

import static java.util.logging.Level.SEVERE;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.parsers.AndroidLintParser;
import se.bjurr.violations.lib.parsers.CLangParser;
import se.bjurr.violations.lib.parsers.CPDParser;
import se.bjurr.violations.lib.parsers.CPPCheckParser;
import se.bjurr.violations.lib.parsers.CSSLintParser;
import se.bjurr.violations.lib.parsers.CheckStyleParser;
import se.bjurr.violations.lib.parsers.CodeNarcParser;
import se.bjurr.violations.lib.parsers.CppLintParser;
import se.bjurr.violations.lib.parsers.DocFXParser;
import se.bjurr.violations.lib.parsers.FindbugsParser;
import se.bjurr.violations.lib.parsers.Flake8Parser;
import se.bjurr.violations.lib.parsers.FxCopParser;
import se.bjurr.violations.lib.parsers.GendarmeParser;
import se.bjurr.violations.lib.parsers.GoLintParser;
import se.bjurr.violations.lib.parsers.GoogleErrorProneParser;
import se.bjurr.violations.lib.parsers.JCReportParser;
import se.bjurr.violations.lib.parsers.JSHintParser;
import se.bjurr.violations.lib.parsers.KlocworkParser;
import se.bjurr.violations.lib.parsers.KotlinGradleParser;
import se.bjurr.violations.lib.parsers.KotlinMavenParser;
import se.bjurr.violations.lib.parsers.LintParser;
import se.bjurr.violations.lib.parsers.MyPyParser;
import se.bjurr.violations.lib.parsers.PCLintParser;
import se.bjurr.violations.lib.parsers.PMDParser;
import se.bjurr.violations.lib.parsers.PerlCriticParser;
import se.bjurr.violations.lib.parsers.PiTestParser;
import se.bjurr.violations.lib.parsers.PyDocStyleParser;
import se.bjurr.violations.lib.parsers.PyLintParser;
import se.bjurr.violations.lib.parsers.RegexParser;
import se.bjurr.violations.lib.parsers.ResharperParser;
import se.bjurr.violations.lib.parsers.SbtScalacParser;
import se.bjurr.violations.lib.parsers.SimianParser;
import se.bjurr.violations.lib.parsers.StyleCopParser;
import se.bjurr.violations.lib.parsers.ViolationsParser;
import se.bjurr.violations.lib.parsers.XMLLintParser;
import se.bjurr.violations.lib.parsers.YAMLlintParser;
import se.bjurr.violations.lib.parsers.ZPTLintParser;
import se.bjurr.violations.lib.util.Utils;

public enum Parser {
  ANDROIDLINT(new AndroidLintParser()), //
  CHECKSTYLE(new CheckStyleParser()), //
  CODENARC(new CodeNarcParser()), //
  CLANG(new CLangParser()), //
  CPD(new CPDParser()), //
  CPPCHECK(new CPPCheckParser()), //
  CPPLINT(new CppLintParser()), //
  CSSLINT(new CSSLintParser()), //
  DOCFX(new DocFXParser()), //
  FINDBUGS(new FindbugsParser()), //
  FLAKE8(new Flake8Parser()), //
  FXCOP(new FxCopParser()), //
  GENDARME(new GendarmeParser()), //
  JCREPORT(new JCReportParser()), //
  JSHINT(new JSHintParser()), //
  KLOCWORK(new KlocworkParser()), //
  KOTLINMAVEN(new KotlinMavenParser()), //
  KOTLINGRADLE(new KotlinGradleParser()), //
  LINT(new LintParser()), //
  MYPY(new MyPyParser()), //
  GOLINT(new GoLintParser()), //
  GOOGLEERRORPRONE(new GoogleErrorProneParser()), //
  PCLINT(new PCLintParser()), //
  PERLCRITIC(new PerlCriticParser()), //
  PITEST(new PiTestParser()), //
  PMD(new PMDParser()), //
  PYDOCSTYLE(new PyDocStyleParser()), //
  PYLINT(new PyLintParser()), //
  REGEX(new RegexParser()), //
  RESHARPER(new ResharperParser()), //
  SBTSCALAC(new SbtScalacParser()), //
  SIMIAN(new SimianParser()), //
  STYLECOP(new StyleCopParser()), //
  XMLLINT(new XMLLintParser()), //
  YAMLLINT(new YAMLlintParser()), //
  ZPTLINT(new ZPTLintParser());

  private static Logger LOG = Logger.getLogger(Parser.class.getSimpleName());
  private transient ViolationsParser violationsParser;

  private Parser(final ViolationsParser violationsParser) {
    this.violationsParser = violationsParser;
  }

  public List<Violation> findViolations(final List<File> includedFiles) {
    final List<Violation> violations = new ArrayList<>();
    for (final File file : includedFiles) {
      try {
        final String string = Utils.toString(new FileInputStream(file));
        violations.addAll(violationsParser.parseReportOutput(string));
      } catch (final Throwable e) {
        LOG.log(SEVERE, "Error when parsing " + file.getAbsolutePath() + " as " + this.name(), e);
      }
    }
    return violations;
  }

  public ViolationsParser getViolationsParser() {
    return violationsParser;
  }
}
