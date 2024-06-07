import java.util.List;
import java.util.Map;

public class BayesianNetwork {
    private Map<String, Variable> variableMap;
    private List<Factor> factorList;

    public BayesianNetwork() {
    }

    public Map<String, Variable> getVariableMap() {
        return this.variableMap;
    }

    public void setVariableMap(Map<String, Variable> variableMap) {
        this.variableMap = variableMap;
    }

    public List<Factor> getFactorList() {
        return this.factorList;
    }

    public void setFactorList(List<Factor> factorList) {
        this.factorList = factorList;
    }

    public void addVariable(String variableName, Variable variable) {
        this.variableMap.put(variableName, variable);
    }

    public void addFactor(Factor factor) {
        this.factorList.add(factor);
    }
}
