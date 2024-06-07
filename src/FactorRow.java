
import java.util.HashMap;
import java.util.Map;

public class FactorRow {
    private Map<String, String> variablesStateMap;
    private double probability;

    public FactorRow(Map<String, String> variablesStateMap, double probability) {
        this.variablesStateMap = variablesStateMap;
        this.probability = probability;
    }

    public FactorRow(FactorRow other) {
        this.probability = other.getProbability();
        this.variablesStateMap = new HashMap(other.getVariablesStateMap());
    }

    public boolean matchesEvidence(String variableName, String evidenceState) {
        return ((String)this.variablesStateMap.get(variableName)).equals(evidenceState);
    }

    public String getVariableState(String variable) {
        return (String)this.variablesStateMap.get(variable);
    }

    public double getProbability() {
        return this.probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public void addVariable(String variableName, String variableState) {
        this.variablesStateMap.put(variableName, variableState);
    }

    public Map<String, String> getVariablesStateMap() {
        return this.variablesStateMap;
    }

    public void setVariablesStateMap(Map<String, String> variablesStateMap) {
        this.variablesStateMap = variablesStateMap;
    }

    public void removeVariable(String variable) {
        this.variablesStateMap.remove(variable);
    }
}
