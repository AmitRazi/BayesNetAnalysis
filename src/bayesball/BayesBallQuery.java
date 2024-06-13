package bayesball;

import inference.Variable;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a query for the Bayes Ball algorithm.
 * It contains the start and end variables, evidence variables, and the independence result.
 */
public class BayesBallQuery {
    private Variable startVariable; // The start variable in the Bayes Ball query
    private Variable endVariable; // The end variable in the Bayes Ball query
    private final Set<Variable> evidenceVariables; // The set of evidence variables
    private boolean isIndependent; // The result of the independence check

    /**
     * Constructor initializes the evidence variables set and sets the independence result to true by default.
     */
    public BayesBallQuery() {
        evidenceVariables = new HashSet<>();
        isIndependent = true;
    }

    /**
     * Gets the start variable.
     *
     * @return the start variable
     */
    public Variable getStartVariable() {
        return startVariable;
    }

    /**
     * Sets the start variable.
     *
     * @param startVariable the start variable to set
     */
    public void setStartVariable(Variable startVariable) {
        this.startVariable = startVariable;
    }

    /**
     * Gets the end variable.
     *
     * @return the end variable
     */
    public Variable getEndVariable() {
        return endVariable;
    }

    /**
     * Sets the end variable.
     *
     * @param endVariable the end variable to set
     */
    public void setEndVariable(Variable endVariable) {
        this.endVariable = endVariable;
    }

    /**
     * Adds an evidence variable.
     *
     * @param variable the evidence variable to add
     */
    public void addEvidenceVariable(Variable variable) {
        evidenceVariables.add(variable);
    }

    /**
     * Checks if the query result is independent.
     *
     * @return true if the variables are independent, false otherwise
     */
    public boolean isIndependent() {
        return isIndependent;
    }

    /**
     * Sets the independence result of the query.
     *
     * @param isIndependent the independence result to set
     */
    public void setIndependent(boolean isIndependent) {
        this.isIndependent = isIndependent;
    }

    /**
     * Checks if a variable is included in the evidence variables set.
     *
     * @param variable the variable to check
     * @return true if the variable is in the evidence variables set, false otherwise
     */
    public boolean includesEvidence(Variable variable) {
        return evidenceVariables.contains(variable);
    }
}
