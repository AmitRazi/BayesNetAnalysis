import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class represents a factor in a Bayesian Network.
 * A factor is a function over a subset of variables, representing the probability distribution.
 */
public class Factor {
    private Variable parentVariable; // The parent variable of this factor
    private Map<String, Variable> variablesMap; // Map of variables in this factor
    private List<FactorRow> factorRows; // List of factor rows representing the conditional probabilities

    /**
     * Constructor for Factor with parent variable.
     *
     * @param parentVariable the parent variable
     * @param variablesMap   map of variables in this factor
     * @param factorRows     list of factor rows
     */
    public Factor(Variable parentVariable, Map<String, Variable> variablesMap, List<FactorRow> factorRows) {
        this.parentVariable = parentVariable;
        this.variablesMap = variablesMap;
        this.factorRows = factorRows;
    }

    /**
     * Constructor for Factor without parent variable.
     *
     * @param variablesMap map of variables in this factor
     * @param factorRows   list of factor rows
     */
    public Factor(Map<String, Variable> variablesMap, List<FactorRow> factorRows) {
        this.variablesMap = variablesMap;
        this.factorRows = factorRows;
    }

    /**
     * Copy constructor for Factor.
     *
     * @param other the factor to copy
     */
    public Factor(Factor other) {
        this.factorRows = other.getFactorRows().stream().map(FactorRow::new).collect(Collectors.toList());
        this.variablesMap = other.variablesMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.parentVariable = other.getParentVariable();
    }

    /**
     * Restricts this factor based on the given evidence.
     *
     * @param variableName  the name of the variable to restrict
     * @param evidenceState the state of the variable as evidence
     */
    public void restrict(String variableName, String evidenceState) {
        if (this.variablesMap.containsKey(variableName)) {
            this.factorRows = this.factorRows.stream()
                                             .filter(row -> row.matchesEvidence(variableName, evidenceState))
                                             .collect(Collectors.toList());
            this.removeVariableFromRows(variableName);
            this.variablesMap.remove(variableName);
        }
    }

    /**
     * Gets the map of variables in this factor.
     *
     * @return the map of variables
     */
    public Map<String, Variable> getVariablesMap() {
        return this.variablesMap;
    }

    /**
     * Gets the list of factor rows.
     *
     * @return the list of factor rows
     */
    public List<FactorRow> getFactorRows() {
        return this.factorRows;
    }

    /**
     * Sets the list of factor rows.
     *
     * @param factorRows the new list of factor rows
     */
    public void setFactorRows(List<FactorRow> factorRows) {
        this.factorRows = factorRows;
    }

    /**
     * Removes a variable from all factor rows.
     *
     * @param variable the name of the variable to remove
     */
    public void removeVariableFromRows(String variable) {
        this.factorRows.forEach(row -> row.removeVariable(variable));
    }

    /**
     * Checks if this factor contains a variable.
     *
     * @param variableName the name of the variable to check
     * @return true if the variable is present, false otherwise
     */
    public boolean containsVariable(String variableName) {
        return this.variablesMap.containsKey(variableName);
    }

    /**
     * Gets the parent variable of this factor.
     *
     * @return the parent variable
     */
    public Variable getParentVariable() {
        return this.parentVariable;
    }

    /**
     * Sets the parent variable of this factor.
     *
     * @param parentVariable the new parent variable
     */
    public void setParentVariable(Variable parentVariable) {
        this.parentVariable = parentVariable;
    }

    /**
     * Returns the rows with the specific variable and state.
     *
     * @param variableName the name of the variable
     * @param state the state of the variable
     * @return the list of rows that match the specific variable and state
     */
    public List<FactorRow> getRowsWithVariableAndState(Variable variable, String state) {
        return this.factorRows.stream()
                              .filter(row -> row.getVariableState(variable.getName()).equals(state))
                              .collect(Collectors.toList());
    }


}
