import java.util.*;
import java.util.stream.Collectors;

public class VariableElimination {
    private final List<Factor> factorList;
    private final Map<String, Variable> variableMap;
    private final Query query;
    private int multiplications = 0;
    private int additions = 0;

    // Constructor initializes factorList and variableMap from the given BayesianNetwork and Query
    public VariableElimination(BayesianNetwork network, Query query) {
        this.query = query;
        this.factorList = network.getFactorList().stream()
                                 .map(Factor::new)
                                 .collect(Collectors.toList());
        this.variableMap = network.getVariableMap().entrySet().stream()
                                  .collect(Collectors.toMap(Map.Entry::getKey,
                                                            entry -> new Variable(entry.getValue())));
    }

    // Executes the variable elimination algorithm based on the provided query
    public void executeQuery() {
        try {
            restrictFactorsBasedOnEvidence();
            filterOutIrrelevantVariables();
            eliminateVariables();
            processFinalFactorForQueryVariable();
        } catch (Exception e) {
            System.err.println("Error during variable elimination: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Restricts factors based on evidence variables
    private void restrictFactorsBasedOnEvidence() {
        restrict(query.getEvidenceVariables());
    }

    // Filters out irrelevant variables from factorList
    private void filterOutIrrelevantVariables() {
        filterIrrelevantVariables();
    }

    // Eliminates variables as specified in the query
    private void eliminateVariables() {
        for (String variableName : query.getEliminationVariables()) {
            eliminateVariable(variableName);
        }
    }

    // Processes the final factor for the query variable
    private void processFinalFactorForQueryVariable() {
        Factor finalFactor = multiplyFactors(findFactorsContainingQueryVariable(query.getQueryVariable().getKey().getName()));
        normalize(finalFactor);
    }

    // Filters out irrelevant variables from factorList
    private void filterIrrelevantVariables() {
        Set<String> relevantVariables = getRelevantVariables();
        factorList.removeIf(factor -> !relevantVariables.contains(factor.getParentVariable().getName()));
    }

    // Finds factors that contain the query variable
    private List<Factor> findFactorsContainingQueryVariable(String queryVariable) {
        return factorList.stream()
                         .filter(factor -> factor.getVariablesMap().containsKey(queryVariable))
                         .collect(Collectors.toList());
    }

    // Gets a set of relevant variables based on the query variable and evidence variables
    private Set<String> getRelevantVariables() {
        Set<String> relevantVariables = new HashSet<>();
        Variable queryVariable = variableMap.get(query.getQueryVariable().getKey().getName());
        relevantVariables.add(queryVariable.getName());
        relevantVariables.addAll(getAncestors(queryVariable));

        for (Pair<Variable, String> evidencePair : query.getEvidenceVariables()) {
            Variable evidenceVariable = variableMap.get(evidencePair.getKey().getName());
            if (relevantVariables.add(evidenceVariable.getName())) {
                relevantVariables.addAll(getAncestors(evidenceVariable));
            }
        }

        return relevantVariables;
    }

    // Retrieves ancestors of a given variable
    private Set<String> getAncestors(Variable variable) {
        Set<String> ancestors = new HashSet<>();
        findAncestors(variable, ancestors);
        return ancestors;
    }

    // Helper method to recursively find ancestors of a variable
    private void findAncestors(Variable variable, Set<String> ancestors) {
        for (Variable parent : variable.getParents()) {
            if (ancestors.add(parent.getName())) {
                findAncestors(parent, ancestors);
            }
        }
    }

    // Normalizes the probabilities in the final factor
    private void normalize(Factor factor) {
        double probabilitySum = factor.getFactorRows().stream()
                                      .mapToDouble(FactorRow::getProbability)
                                      .sum();
        additions += variableMap.get(query.getQueryVariable().getKey().getName()).getOutcomes().size() - 1;
        factor.getFactorRows().forEach(row -> row.setProbability(row.getProbability() / probabilitySum));
    }

    // Finds factors that are relevant for a given variable
    private List<Factor> findRelevantFactors(String variableName) {
        return factorList.stream()
                         .filter(factor -> factor.containsVariable(variableName))
                         .collect(Collectors.toList());
    }

    // Multiplies a list of factors, returning a single factor as the result
    private Factor multiplyFactors(List<Factor> factors) {
        factors.sort(Comparator.comparingInt(factor -> factor.getVariablesMap().size()));
        Factor accumulatedFactor = factors.get(0);

        for (int i = 1; i < factors.size(); i++) {
            accumulatedFactor = multiplyTwoFactors(accumulatedFactor, factors.get(i));
        }

        return accumulatedFactor;
    }

    // Multiplies two factors and returns the resulting factor
    private Factor multiplyTwoFactors(Factor f1, Factor f2) {
        List<String> commonVariables = findCommonVariables(f1, f2);
        List<FactorRow> newRows = new ArrayList<>();

        for (FactorRow row1 : f1.getFactorRows()) {
            for (FactorRow row2 : f2.getFactorRows()) {
                if (hasMatchingVariables(row1, row2, commonVariables)) {
                    Map<String, String> combinedStateMap = new HashMap<>(row1.getVariablesStateMap());
                    combinedStateMap.putAll(row2.getVariablesStateMap());
                    newRows.add(new FactorRow(combinedStateMap, row1.getProbability() * row2.getProbability()));
                    multiplications++;
                }
            }
        }

        Map<String, Variable> combinedVariableMap = new HashMap<>(f1.getVariablesMap());
        combinedVariableMap.putAll(f2.getVariablesMap());

        return new Factor(combinedVariableMap, newRows);
    }

    // Checks if two factor rows have matching variables
    private boolean hasMatchingVariables(FactorRow row1, FactorRow row2, List<String> commonVariables) {
        return commonVariables.stream()
                              .allMatch(variable -> row1.getVariableState(variable).equals(row2.getVariableState(variable)));
    }

    // Finds common variables between two factors
    private List<String> findCommonVariables(Factor f1, Factor f2) {
        Set<String> f1Variables = f1.getVariablesMap().keySet();
        Set<String> f2Variables = f2.getVariablesMap().keySet();
        return f1Variables.stream()
                          .filter(f2Variables::contains)
                          .collect(Collectors.toList());
    }

    // Sums out a variable from a factor, returning a new list of factor rows
    private List<FactorRow> sumOut(Factor factor) {
        List<FactorRow> rows = factor.getFactorRows();
        Set<FactorRow> summedRows = new HashSet<>();

        for (int i = 0; i < rows.size(); i++) {
            for (int j = i + 1; j < rows.size(); j++) {
                if (rows.get(i).getVariablesStateMap().equals(rows.get(j).getVariablesStateMap())) {
                    summedRows.add(new FactorRow(rows.get(i).getVariablesStateMap(),
                                                 rows.get(i).getProbability() + rows.get(j).getProbability()));
                    additions++;
                }
            }
        }

        return new ArrayList<>(summedRows);
    }

    // Restricts the factors based on the given evidence variables
    private void restrict(List<Pair<Variable, String>> evidenceVariables) {
        evidenceVariables.forEach(evidenceVariable -> {
            variableMap.remove(evidenceVariable.getKey());
            factorList.forEach(factor -> factor.restrict(evidenceVariable.getKey().getName(), evidenceVariable.getValue()));
        });
    }

    // Eliminates a variable by multiplying relevant factors and summing out the variable
    private void eliminateVariable(String variableName) {
        List<Factor> relevantFactors = findRelevantFactors(variableName);
        if (!relevantFactors.isEmpty()) {
            Factor multipliedFactor = multiplyFactors(relevantFactors);
            multipliedFactor.removeVariableFromRows(variableName);
            multipliedFactor.setFactorRows(sumOut(multipliedFactor));
            factorList.removeAll(relevantFactors);
            factorList.add(multipliedFactor);
            variableMap.remove(variableName);
        }
    }
}
