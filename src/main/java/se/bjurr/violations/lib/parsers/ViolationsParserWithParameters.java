package se.bjurr.violations.lib.parsers;

import java.util.List;

/**
 * This interface can be used for violation parsers that may take
 * additional arguments.
 */
public interface ViolationsParserWithParameters extends ViolationsParser {

    /**
     * Sets the value of one parameter used by this violations parser. Note that a
     * null value or an empty string should be acceptable - in this case,
     * parseReportOutput should use the default parameter value or throw an
     * exception if no default is available.
     *
     * @param name  Name of the parameter
     * @param value Value of the parameter (as a string)
     * @throws IllegalArgumentException If the value is illegal or if the parameter
     *                                  does not exist. Please add a clear
     *                                  description of what went wrong.
     */
    public void setParameter(String name, String value) throws IllegalArgumentException;

    /**
     * A helpful description of the parameter that can be shown to an end user.
     *
     * @param name The name of the parameter to be described
     * @return The description
     */
    public String parameterDescription(String name);

    /** A list of the parameters that this violations parser can take */
    public List<String> parameterNames();

    /** Clears all parameters back to their default values */
    public void clearParameters();
}
