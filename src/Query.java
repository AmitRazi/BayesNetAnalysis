
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Query {
    private Pair<Variable, String> queryVariable;
    private final List<Pair<Variable, String>> evidenceVariables = new ArrayList<>();
    private Queue<String> eliminationVariables = new ArrayDeque<>();

    public Query() {
    }

    public Pair<Variable, String> getQueryVariable() {
        return this.queryVariable;
    }

    public void setQueryVariable(Pair<Variable, String> queryVariable) {
        this.queryVariable = queryVariable;
    }

    public List<Pair<Variable, String>> getEvidenceVariables() {
        return this.evidenceVariables;
    }

    public void addEvidenceVariable(Pair<Variable, String> evidence) {
        this.evidenceVariables.add(evidence);
    }

    public Queue<String> getEliminationVariables() {
        return this.eliminationVariables;
    }

    public void setEliminationVariables(Queue<String> eliminationVariables) {
        this.eliminationVariables = eliminationVariables;
    }
}
