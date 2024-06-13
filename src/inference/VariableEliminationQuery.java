package inference;

import utils.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * This class represents a query in a Bayesian Network.
 * A query consists of a query variable, evidence variables, and elimination variables.
 */
public class VariableEliminationQuery {
    private Pair<Variable, String> queryVariable; // The variable to query
    private final List<Pair<Variable, String>> evidenceVariables = new ArrayList<>(); // List of evidence variables
    private Queue<String> eliminationVariables = new ArrayDeque<>(); // Queue of variables to eliminate

    /**
     * Default constructor for the Query class.
     */
    public VariableEliminationQuery() {
    }

    /**
     * Gets the query variable.
     *
     * @return the query variable as a utils.Pair of Variable and its state
     */
    public Pair<Variable, String> getQueryVariable() {
        return this.queryVariable;
    }

    /**
     * Sets the query variable.
     *
     * @param queryVariable the query variable as a utils.Pair of Variable and its state
     */
    public void setQueryVariable(Pair<Variable, String> queryVariable) {
        this.queryVariable = queryVariable;
    }

    /**
     * Gets the list of evidence variables.
     *
     * @return the list of evidence variables as Pairs of Variable and their states
     */
    public List<Pair<Variable, String>> getEvidenceVariables() {
        return this.evidenceVariables;
    }

    /**
     * Adds an evidence variable to the list.
     *
     * @param evidence the evidence variable as a utils.Pair of Variable and its state
     */
    public void addEvidenceVariable(Pair<Variable, String> evidence) {
        this.evidenceVariables.add(evidence);
    }

    /**
     * Gets the queue of elimination variables.
     *
     * @return the queue of elimination variables as Strings
     */
    public Queue<String> getEliminationVariables() {
        return this.eliminationVariables;
    }

    /**
     * Sets the queue of elimination variables.
     *
     * @param eliminationVariables the queue of elimination variables as Strings
     */
    public void setEliminationVariables(Queue<String> eliminationVariables) {
        this.eliminationVariables = eliminationVariables;
    }
}
