import java.util.HashSet;
import java.util.Set;

public class BayesBallQuery {
    private Variable startVariable;
    private Variable endVariable;
    private Set<Variable> evidenceVariables;
    private boolean isIndependent;

    public  BayesBallQuery(){
        evidenceVariables = new HashSet<>();
        isIndependent = true;

    }

    public Variable getStartVariable() {
        return startVariable;
    }

    public void setStartVariable(Variable startVariable) {
        this.startVariable = startVariable;
    }

    public Variable getEndVariable() {
        return endVariable;
    }

    public void setEndVariable(Variable endVariable) {
        this.endVariable = endVariable;
    }

    public void addEvidenceVariable(Variable variable){
        evidenceVariables.add(variable);
    }

    public boolean isIndependent(){
        return isIndependent;
    }

    public void setIndependent(boolean isIndependent){
        this.isIndependent = isIndependent;
    }

    public boolean includesEvidence(Variable variable){
        return evidenceVariables.contains(variable);
    }
}
