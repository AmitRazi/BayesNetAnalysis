import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Factor {
    private Variable parentVariable;
    private Map<String, Variable> variablesMap;
    private List<FactorRow> factorRows;

    public Factor(Variable parentVariable, Map<String, Variable> variablesMap, List<FactorRow> factorRows) {
        this.parentVariable = parentVariable;
        this.variablesMap = variablesMap;
        this.factorRows = factorRows;
    }

    public Factor(Map<String, Variable> variablesMap, List<FactorRow> factorRows) {
        this.variablesMap = variablesMap;
        this.factorRows = factorRows;
    }

    public Factor(Factor other) {
        this.factorRows = other.getFactorRows().stream().map(FactorRow::new).collect(Collectors.toList());
        this.variablesMap = other.variablesMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                                            Map.Entry::getValue));
        this.parentVariable = other.getParentVariable();
    }

    public void addVariable(Variable variable) {
        this.variablesMap.put(variable.getName(), variable);
    }

    public void addRow(FactorRow factorRow) {
        this.factorRows.add(factorRow);
    }

    public void restrict(String variableName, String evidenceState) {
        if (this.variablesMap.get(variableName) != null) {
            this.factorRows =this.factorRows.stream().filter((row) -> row.matchesEvidence(variableName, evidenceState)).collect(Collectors.toList());
            this.removeVariableFromRows(variableName);
            this.variablesMap.remove(variableName);
        }
    }

    public Map<String, Variable> getVariablesMap() {
        return this.variablesMap;
    }

    public void setVariablesMap(Map<String, Variable> variablesMap) {
        this.variablesMap = variablesMap;
    }

    public List<FactorRow> getFactorRows() {
        return this.factorRows;
    }

    public void setFactorRows(List<FactorRow> factorRows) {
        this.factorRows = factorRows;
    }

    public void removeVariableFromRows(String variable) {
        this.factorRows.forEach((row) -> row.removeVariable(variable));
    }

    public boolean findVariable(String variableName) {
        return this.variablesMap.get(variableName) != null;
    }

    public Variable getParentVariable() {
        return this.parentVariable;
    }

    public void setParentVariable(Variable parentVariable) {
        this.parentVariable = parentVariable;
    }
}
