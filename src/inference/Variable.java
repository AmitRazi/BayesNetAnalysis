package inference;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a variable in a Bayesian Network.
 * A variable can have parents and children, and it can be marked as evidence.
 */
public class Variable {
    private final String name; // The name of the variable
    private final List<String> outcomes; // Possible outcomes of the variable
    private List<Variable> parents = new ArrayList<>(); // List of parent variables
    private final List<Variable> children = new ArrayList<>(); // List of child variables
    private boolean isEvidence = false; // Indicates if this variable is an evidence variable

    /**
     * Constructor for the Variable class.
     *
     * @param name     the name of the variable
     * @param outcomes the possible outcomes of the variable
     */
    public Variable(String name, List<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
    }

    /**
     * Copy constructor for the Variable class.
     *
     * @param other the variable to copy
     */
    public Variable(Variable other) {
        this.name = other.name;
        this.outcomes = new ArrayList<>(other.outcomes);
        this.parents = new ArrayList<>(other.parents);
        this.isEvidence = other.isEvidence;
    }

    /**
     * Gets the name of the variable.
     *
     * @return the name of the variable
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the possible outcomes of the variable.
     *
     * @return the list of possible outcomes
     */
    public List<String> getOutcomes() {
        return this.outcomes;
    }

    /**
     * Adds a parent variable.
     *
     * @param parent the parent variable to add
     */
    public void addParent(Variable parent) {
        this.parents.add(parent);
    }

    /**
     * Checks if this variable is a child of the given parent variable.
     *
     * @param parent the parent variable to check
     * @return true if this variable is a child of the parent, false otherwise
     */
    public boolean isChildOf(Variable parent) {
        return this.parents.contains(parent);
    }

    /**
     * Checks if this variable is a parent of the given child variable.
     *
     * @param child the child variable to check
     * @return true if this variable is a parent of the child, false otherwise
     */
    public boolean isParentOf(Variable child) {
        return this.children.contains(child);
    }

    /**
     * Adds a child variable.
     *
     * @param child the child variable to add
     */
    public void addChild(Variable child) {
        this.children.add(child);
    }

    /**
     * Gets the list of parent variables.
     *
     * @return the list of parent variables
     */
    public List<Variable> getParents() {
        return this.parents;
    }

    /**
     * Gets the list of child variables.
     *
     * @return the list of child variables
     */
    public List<Variable> getChildren() {
        return this.children;
    }

    /**
     * Checks if this variable is an evidence variable.
     *
     * @return true if this variable is an evidence variable, false otherwise
     */
    public boolean isEvidence() {
        return this.isEvidence;
    }

    /**
     * Sets this variable as an evidence variable.
     *
     * @param evidence true to set as evidence, false otherwise
     */
    public void setEvidence(boolean evidence) {
        this.isEvidence = evidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable variable)) return false;

        if (isEvidence != variable.isEvidence) return false;
        return name.equals(variable.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (isEvidence ? 1 : 0);
        return result;
    }
}
