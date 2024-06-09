import java.util.List;
import java.util.Map;

/**
 * This class represents a Bayesian Network.
 * A Bayesian Network is a probabilistic graphical model that represents a set of variables
 * and their conditional dependencies via a directed acyclic graph (DAG).
 */
public class BayesianNetwork {

    // A map that holds the variables in the Bayesian Network, where the key is the variable name
    // and the value is the corresponding Variable object.
    private Map<String, Variable> variableMap;

    // A list that holds the factors in the Bayesian Network.
    // Factors are functions over a subset of variables, representing the probability distributions.
    private List<Factor> factorList;

    /**
     * Default constructor for the BayesianNetwork class.
     * Initializes an empty Bayesian Network.
     */
    public BayesianNetwork() {
    }

    /**
     * Gets the map of variables in the Bayesian Network.
     * @return A map where the key is the variable name and the value is the corresponding Variable object.
     */
    public Map<String, Variable> getVariableMap() {
        return this.variableMap;
    }

    /**
     * Sets the map of variables in the Bayesian Network.
     * @param variableMap A map where the key is the variable name and the value is the corresponding Variable object.
     */
    public void setVariableMap(Map<String, Variable> variableMap) {
        this.variableMap = variableMap;
    }

    /**
     * Gets the list of factors in the Bayesian Network.
     * @return A list of Factor objects representing the probability distributions over subsets of variables.
     */
    public List<Factor> getFactorList() {
        return this.factorList;
    }

    /**
     * Sets the list of factors in the Bayesian Network.
     * @param factorList A list of Factor objects representing the probability distributions over subsets of variables.
     */
    public void setFactorList(List<Factor> factorList) {
        this.factorList = factorList;
    }
}
