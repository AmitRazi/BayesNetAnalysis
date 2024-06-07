import java.util.ArrayList;
import java.util.List;

public class Variable {
    private final String name;
    private final List<String> outcomes;
    private List<Variable> parents = new ArrayList<>();
    private final List<Variable> children = new ArrayList<>();
    private boolean isEvidence = false;

    public Variable(String name, List<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
    }

    public Variable(Variable other) {
        this.name = other.name;
        this.outcomes = new ArrayList<>(other.outcomes);
        this.parents = new ArrayList<>(other.parents);
        this.isEvidence = other.isEvidence;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getOutcomes() {
        return this.outcomes;
    }

    public void addParent(Variable parentName) {
        this.parents.add(parentName);
    }

    public boolean isChildOf(Variable parent) {
        return this.parents.contains(parent);
    }

    public boolean isParentOf(Variable child) {
        return this.children.contains(child);
    }

    public void addChild(Variable childName) {
        this.children.add(childName);
    }

    public List<Variable> getParents() {
        return this.parents;
    }

    public List<Variable> getChildren() {
        return this.children;
    }

    public boolean isEvidence() {
        return this.isEvidence;
    }

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
