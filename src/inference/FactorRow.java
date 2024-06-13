package inference;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a row in a factor table of a Bayesian Network.
 * Each row contains a mapping of variable states to their corresponding probability.
 */
public class FactorRow {
    private Map<String, String> variablesStateMap; // Map of variable names to their states
    private double probability; // Probability of the variable states in this row

    /**
     * Constructor for FactorRow.
     *
     * @param variablesStateMap the map of variable names to their states
     * @param probability the probability of the variable states in this row
     */
    public FactorRow(Map<String, String> variablesStateMap, double probability) {
        this.variablesStateMap = variablesStateMap;
        this.probability = probability;
    }

    /**
     * Copy constructor for FactorRow.
     *
     * @param other the FactorRow to copy
     */
    public FactorRow(FactorRow other) {
        this.probability = other.getProbability();
        this.variablesStateMap = new HashMap<>(other.getVariablesStateMap());
    }

    /**
     * Checks if this row matches the given evidence.
     *
     * @param variableName the name of the variable
     * @param evidenceState the state of the variable as evidence
     * @return true if the row matches the evidence, false otherwise
     */
    public boolean matchesEvidence(String variableName, String evidenceState) {
        return this.variablesStateMap.get(variableName).equals(evidenceState);
    }

    /**
     * Gets the state of a given variable in this row.
     *
     * @param variable the name of the variable
     * @return the state of the variable
     */
    public String getVariableState(String variable) {
        return this.variablesStateMap.get(variable);
    }

    /**
     * Gets the probability of the variable states in this row.
     *
     * @return the probability
     */
    public double getProbability() {
        return this.probability;
    }

    /**
     * Sets the probability of the variable states in this row.
     *
     * @param probability the new probability
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }

    /**
     * Adds a variable and its state to this row.
     *
     * @param variableName the name of the variable
     * @param variableState the state of the variable
     */
    public void addVariable(String variableName, String variableState) {
        this.variablesStateMap.put(variableName, variableState);
    }

    /**
     * Gets the map of variable names to their states.
     *
     * @return the map of variable states
     */
    public Map<String, String> getVariablesStateMap() {
        return this.variablesStateMap;
    }

    /**
     * Sets the map of variable names to their states.
     *
     * @param variablesStateMap the new map of variable states
     */
    public void setVariablesStateMap(Map<String, String> variablesStateMap) {
        this.variablesStateMap = variablesStateMap;
    }

    /**
     * Removes a variable from this row.
     *
     * @param variable the name of the variable to remove
     */
    public void removeVariable(String variable) {
        this.variablesStateMap.remove(variable);
    }


    @Override
    public String toString() {
        return "FactorRow{" +
                "variablesStateMap=" + variablesStateMap +
                ", probability=" + probability +
                '}';
    }
}
