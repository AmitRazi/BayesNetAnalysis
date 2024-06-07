
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VariableElimination {
    private final List<Factor> factorList;
    private final Map<String, Variable> variableMap;
    private final Query query;
    private int multi = 0;
    private int add = 0;

    public VariableElimination(BayesianNetwork network, Query query) {
        this.query = query;
        this.factorList = network.getFactorList().stream().map(Factor::new).collect(Collectors.toList());
        this.variableMap = network.getVariableMap()
                                  .entrySet()
                                  .stream()
                                  .collect(Collectors.toMap(Map.Entry::getKey,
                                                            (entry) -> new Variable(entry.getValue())));
    }

    public void query() {
        this.restrict(this.query.getEvidenceVariables());
        this.filterIrrelevantVariables();

        for (String variableName : this.query.getEliminationVariables()) {
            List<Factor> relevantFactors = this.findRelevantFactors(variableName);
            if (relevantFactors.size() != 0) {
                Factor multipliedFactor = this.multiplyFactors(relevantFactors);
                multipliedFactor.removeVariableFromRows(variableName);
                multipliedFactor.setFactorRows(this.sumOut(multipliedFactor));
                this.factorList.removeAll(relevantFactors);
                this.factorList.add(multipliedFactor);
                this.variableMap.remove(variableName);
            }
        }

        Factor finalFactor = this.findFactorContainingQueryVariable(query.getQueryVariable().getKey().getName());
        this.normalize(finalFactor);
        System.out.println();
    }

    private void filterIrrelevantVariables() {
        Set<String> relevantVariables = this.getRelevantVariables();
        List<Factor> toDelete = new ArrayList<>();

        for (Factor factor : this.factorList) {
            if (!relevantVariables.contains(factor.getParentVariable().getName())) {
                toDelete.add(factor);
            }
        }

        this.factorList.removeAll(toDelete);
    }

    private Factor findFactorContainingQueryVariable(String queryVariable) {
        for (Factor factor : this.factorList) {
            if (factor.getVariablesMap().containsKey(queryVariable)) {
                return factor;
            }
        }
        // If no single factor contains the query variable, multiply the remaining factors
        return this.multiplyFactors(this.factorList);
    }


    private Set<String> getRelevantVariables() {
        Set<String> relevantVariables = new HashSet<>();
        Variable queryVariable = this.variableMap.get(this.query.getQueryVariable().getKey().getName());
        relevantVariables.add(queryVariable.getName());
        relevantVariables.addAll(getAncestors(queryVariable));

        for (Pair<Variable, String> evidencePair : this.query.getEvidenceVariables()) {
            if(relevantVariables.contains(evidencePair.getKey())) continue;
            Variable evidenceVariable = this.variableMap.get(evidencePair.getKey().getName());
            relevantVariables.add(evidenceVariable.getName());
            relevantVariables.addAll(getAncestors(evidenceVariable));
        }

        return relevantVariables;
    }

    private Set<String> getAncestors(Variable variable) {
        Set<String> ancestors = new HashSet<>();
        getAncestorsHelper(variable, ancestors);
        return ancestors;
    }

    private void getAncestorsHelper(Variable variable, Set<String> ancestors) {
        for (Variable parent : variable.getParents()) {
            if (ancestors.add(parent.getName())) {
                getAncestorsHelper(parent, ancestors);
            }
        }
    }

    private void normalize(Factor multipliedFactor) {
        double probabilitySum = multipliedFactor.getFactorRows()
                                                .stream()
                                                .map(FactorRow::getProbability)
                                                .reduce(0.0, Double::sum);
        this.add += this.variableMap.get(this.query.getQueryVariable().getKey().getName()).getOutcomes().size() - 1;
        multipliedFactor.getFactorRows().forEach((row) -> row.setProbability(row.getProbability() / probabilitySum));
    }

    private List<Factor> findRelevantFactors(String variableName) {
        return this.factorList.stream()
                              .filter((factor) -> factor.findVariable(variableName))
                              .collect(Collectors.toList());
    }

    private Factor multiplyFactors(List<Factor> factors) {
        factors.sort(Comparator.comparingInt((o) -> o.getVariablesMap().size()));
        Factor accFactor = factors.get(0);

        for (int i = 1; i < factors.size(); ++i) {
            accFactor = this.multiplyFactors(accFactor, factors.get(i));
        }

        return accFactor;
    }

    private Factor multiplyFactors(Factor f1, Factor f2) {
        List<String> commonVariables = this.findCommonVariables(f1, f2);
        List<FactorRow> newRows = new ArrayList<>();

        for (FactorRow row1 : f1.getFactorRows()) {
            for (FactorRow row2 : f2.getFactorRows()) {
                if (this.hasMatchingVariables(row1, row2, commonVariables)) {
                    Map<String, String> combinedStateMap = new HashMap<>(row1.getVariablesStateMap());
                    combinedStateMap.putAll(row2.getVariablesStateMap());
                    newRows.add(new FactorRow(combinedStateMap, row1.getProbability() * row2.getProbability()));
                    ++this.multi;
                    System.out.println("Multiplying rows: " + row1 + " * " + row2);
                }
            }
        }

        Map<String, Variable> combinedVariableMap = new HashMap<>(f1.getVariablesMap());
        combinedVariableMap.putAll(f2.getVariablesMap());
        return new Factor(combinedVariableMap, newRows);
    }

    private boolean hasMatchingVariables(FactorRow row1, FactorRow row2, List<String> commonVariables) {
        for (String variable : commonVariables) {
            if (!row1.getVariableState(variable).equals(row2.getVariableState(variable))) {
                return false;
            }
        }
        return true;
    }

    private List<String> findCommonVariables(Factor f1, Factor f2) {
        Set<String> f1Variables = f1.getVariablesMap().keySet();
        Set<String> f2Variables = f2.getVariablesMap().keySet();
        Set<String> intersection = new HashSet<>(f1Variables);
        intersection.retainAll(f2Variables);
        return intersection.stream().toList();
    }

    private List<FactorRow> sumOut(Factor newFactor) {
        List<FactorRow> rows = newFactor.getFactorRows();
        Set<FactorRow> newRows = new HashSet<>();

        for (int i = 0; i < rows.size(); ++i) {
            for (int j = i + 1; j < rows.size(); ++j) {
                if (rows.get(i).getVariablesStateMap().equals(rows.get(j).getVariablesStateMap())) {
                    newRows.add(new FactorRow(rows.get(i).getVariablesStateMap(),
                                              rows.get(i).getProbability() + rows.get(j).getProbability()));
                    ++this.add;
                    System.out.println("Adding rows: " + rows.get(i).toString() + " + " + rows.get(j).toString());
                }
            }
        }

        return newRows.stream().toList();
    }

    private void restrict(List<Pair<Variable, String>> evidenceVariables) {

        for (Pair<Variable, String> evidenceVariable : evidenceVariables) {
            this.variableMap.remove(evidenceVariable.getKey());
            this.factorList.forEach((factor) -> factor.restrict(evidenceVariable.getKey().getName(),
                                                                evidenceVariable.getValue()));
        }

    }
}
