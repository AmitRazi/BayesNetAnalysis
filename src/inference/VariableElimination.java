import utils.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class performs Variable Elimination for a Bayesian Network.
 * Variable Elimination is an exact inference algorithm used for probabilistic queries.
 */
public class VariableElimination {
    private final List<Factor> factorList; // List of factors in the Bayesian Network
    private final Map<String, Variable> variableMap; // Map of variables in the Bayesian Network
    private final VariableEliminationQuery variableEliminationQuery; // Query to be executed
    private QueryResult queryResult;

    /**
     * Constructor initializes factorList and variableMap from the given BayesianNetwork and Query.
     *
     * @param network the Bayesian Network
     * @param variableEliminationQuery the query to be executed
     */
    public VariableElimination(BayesianNetwork network, VariableEliminationQuery variableEliminationQuery) {
        this.variableEliminationQuery = variableEliminationQuery;
        this.factorList = network.getFactorList().stream()
                                 .map(Factor::new)
                                 .collect(Collectors.toList());
        this.variableMap = network.getVariableMap().entrySet().stream()
                                  .collect(Collectors.toMap(Map.Entry::getKey,
                                                            entry -> new Variable(entry.getValue())));
        this.queryResult = new QueryResult();
    }

    public String getResult() {
        return queryResult.toString();
    }

    /**
     * Executes the variable elimination algorithm based on the provided query.
     */
    public void executeQuery() {
        try {
            restrictFactorsBasedOnEvidence();
            filterOutIrrelevantVariables();
            sortFactorsByNumOfRows();
            eliminateVariables();
            processFinalFactorForQueryVariable();
            setQueryResult();
        } catch (Exception e) {
            System.err.println("Error during variable elimination: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sorts factors by the number of rows.
     */
    private void sortFactorsByNumOfRows() {
        Collections.sort(factorList, (f1, f2) -> Integer.compare(f1.getFactorRows().size(), f2.getFactorRows().size()));
    }

    /**
     * Sets the query result based on the final factor's probability.
     */
    private void setQueryResult() {
        queryResult.setProbability(factorList.get(0).getRowsWithVariableAndState(variableEliminationQuery.getQueryVariable().getKey(),
                                                                                 variableEliminationQuery.getQueryVariable().getValue()).get(0).getProbability());
    }

    /**
     * Restricts factors based on evidence variables.
     */
    private void restrictFactorsBasedOnEvidence() {
        restrict(variableEliminationQuery.getEvidenceVariables());
    }

    /**
     * Filters out irrelevant variables from factorList.
     */
    private void filterOutIrrelevantVariables() {
        filterIrrelevantVariables();
    }

    /**
     * Eliminates variables as specified in the query.
     */
    private void eliminateVariables() {
        for (String variableName : variableEliminationQuery.getEliminationVariables()) {
            eliminateVariable(variableName);
        }
    }

    /**
     * Processes the final factor for the query variable.
     */
    private void processFinalFactorForQueryVariable() {
        Factor finalFactor = multiplyFactors(findFactorsContainingQueryVariable(variableEliminationQuery.getQueryVariable().getKey().getName()));
        normalize(finalFactor);
        factorList.clear();
        factorList.add(finalFactor);
    }

    /**
     * Filters out irrelevant variables from factorList.
     */
    private void filterIrrelevantVariables() {
        Set<String> relevantVariables = getRelevantVariables();
        factorList.removeIf(factor -> !relevantVariables.contains(factor.getParentVariable().getName()));
    }

    /**
     * Finds factors that contain the query variable.
     *
     * @param queryVariable the query variable name
     * @return a list of factors containing the query variable
     */
    private List<Factor> findFactorsContainingQueryVariable(String queryVariable) {
        return factorList.stream()
                         .filter(factor -> factor.getVariablesMap().containsKey(queryVariable))
                         .collect(Collectors.toList());
    }

    /**
     * Gets a set of relevant variables based on the query variable and evidence variables.
     *
     * @return a set of relevant variable names
     */
    private Set<String> getRelevantVariables() {
        Set<String> relevantVariables = new HashSet<>();
        Variable queryVariable = variableMap.get(variableEliminationQuery.getQueryVariable().getKey().getName());
        relevantVariables.add(queryVariable.getName());
        relevantVariables.addAll(getAncestors(queryVariable));

        for (Pair<Variable, String> evidencePair : variableEliminationQuery.getEvidenceVariables()) {
            Variable evidenceVariable = variableMap.get(evidencePair.getKey().getName());
            if (relevantVariables.add(evidenceVariable.getName())) {
                relevantVariables.addAll(getAncestors(evidenceVariable));
            }
        }

        return relevantVariables;
    }

    /**
     * Retrieves ancestors of a given variable.
     *
     * @param variable the variable whose ancestors are to be found
     * @return a set of ancestor variable names
     */
    private Set<String> getAncestors(Variable variable) {
        Set<String> ancestors = new HashSet<>();
        findAncestors(variable, ancestors);
        return ancestors;
    }

    /**
     * Helper method to recursively find ancestors of a variable.
     *
     * @param variable the variable whose ancestors are to be found
     * @param ancestors the set of ancestor variable names
     */
    private void findAncestors(Variable variable, Set<String> ancestors) {
        for (Variable parent : variable.getParents()) {
            if (ancestors.add(parent.getName())) {
                findAncestors(parent, ancestors);
            }
        }
    }

    /**
     * Normalizes the probabilities in the final factor.
     *
     * @param factor the factor to be normalized
     */
    private void normalize(Factor factor) {
        double probabilitySum = factor.getFactorRows().stream()
                                      .mapToDouble(FactorRow::getProbability)
                                      .sum();
        queryResult.incrementAdditionOperations(variableMap.get(variableEliminationQuery.getQueryVariable().getKey().getName()).getOutcomes().size() - 1);
        factor.getFactorRows().forEach(row -> row.setProbability(row.getProbability() / probabilitySum));
    }

    /**
     * Finds factors that are relevant for a given variable.
     *
     * @param variableName the variable name
     * @return a list of relevant factors
     */
    private List<Factor> findRelevantFactors(String variableName) {
        return factorList.stream()
                         .filter(factor -> factor.containsVariable(variableName))
                         .collect(Collectors.toList());
    }

    /**
     * Multiplies a list of factors, returning a single factor as the result.
     *
     * @param factors the list of factors to be multiplied
     * @return the resulting factor after multiplication
     */
    private Factor multiplyFactors(List<Factor> factors) {
        factors.sort(Comparator.comparingInt(factor -> factor.getVariablesMap().size()));
        Factor accumulatedFactor = factors.get(0);

        for (int i = 1; i < factors.size(); i++) {
            accumulatedFactor = multiplyTwoFactors(accumulatedFactor, factors.get(i));
        }

        return accumulatedFactor;
    }

    /**
     * Multiplies two factors and returns the resulting factor.
     *
     * @param f1 the first factor
     * @param f2 the second factor
     * @return the resulting factor after multiplication
     */
    private Factor multiplyTwoFactors(Factor f1, Factor f2) {
        List<String> commonVariables = findCommonVariables(f1, f2);
        List<FactorRow> newRows = new ArrayList<>();

        for (FactorRow row1 : f1.getFactorRows()) {
            for (FactorRow row2 : f2.getFactorRows()) {
                if (hasMatchingVariables(row1, row2, commonVariables)) {
                    Map<String, String> combinedStateMap = new HashMap<>(row1.getVariablesStateMap());
                    combinedStateMap.putAll(row2.getVariablesStateMap());
                    newRows.add(new FactorRow(combinedStateMap, row1.getProbability() * row2.getProbability()));
                    queryResult.incrementMultiplicationOperations(1);
                }
            }
        }

        Map<String, Variable> combinedVariableMap = new HashMap<>(f1.getVariablesMap());
        combinedVariableMap.putAll(f2.getVariablesMap());

        return new Factor(combinedVariableMap, newRows);
    }

    /**
     * Checks if two factor rows have matching variables.
     *
     * @param row1 the first factor row
     * @param row2 the second factor row
     * @param commonVariables the list of common variable names
     * @return true if the rows have matching variables, false otherwise
     */
    private boolean hasMatchingVariables(FactorRow row1, FactorRow row2, List<String> commonVariables) {
        return commonVariables.stream()
                              .allMatch(variable -> row1.getVariableState(variable).equals(row2.getVariableState(variable)));
    }

    /**
     * Finds common variables between two factors.
     *
     * @param f1 the first factor
     * @param f2 the second factor
     * @return a list of common variable names
     */
    private List<String> findCommonVariables(Factor f1, Factor f2) {
        Set<String> f1Variables = f1.getVariablesMap().keySet();
        Set<String> f2Variables = f2.getVariablesMap().keySet();
        return f1Variables.stream()
                          .filter(f2Variables::contains)
                          .collect(Collectors.toList());
    }

    /**
     * Sums out a variable from a factor, returning a new list of factor rows.
     *
     * @param factor the factor from which the variable is to be summed out
     * @param sumOutVariableName the name of the variable to be summed out
     * @return a list of new factor rows after summing out the variable
     */
    private List<FactorRow> sumOut(Factor factor, String sumOutVariableName) {
        List<FactorRow> rows = factor.getFactorRows();
        Map<Map<String, String>, Double> sumOutMap = new HashMap<>();

        for (FactorRow row : rows) {
            Map<String, String> stateMap = row.getVariablesStateMap();
            stateMap.remove(sumOutVariableName);

            if (sumOutMap.containsKey(stateMap)) {
                sumOutMap.put(stateMap, sumOutMap.get(stateMap) + row.getProbability());
                queryResult.incrementAdditionOperations(1); // Count the addition operation
            } else {
                sumOutMap.put(stateMap, row.getProbability());
            }
        }

        List<FactorRow> summedRows = sumOutMap.entrySet().stream().map(entry -> new FactorRow(entry.getKey(),
                                                                                              entry.getValue())).collect(
                Collectors.toList());
        return summedRows;
    }

    /**
     * Restricts the factors based on the given evidence variables.
     *
     * @param evidenceVariables the list of evidence variables
     */
    private void restrict(List<Pair<Variable, String>> evidenceVariables) {
        evidenceVariables.forEach(evidenceVariable -> {
            variableMap.remove(evidenceVariable.getKey().getName());
            factorList.forEach(factor -> factor.restrict(evidenceVariable.getKey().getName(), evidenceVariable.getValue()));
        });
    }

    /**
     * Eliminates a variable by multiplying relevant factors and summing out the variable.
     *
     * @param variableName the name of the variable to be eliminated
     */
    private void eliminateVariable(String variableName) {
        List<Factor> relevantFactors = findRelevantFactors(variableName);
        if (!relevantFactors.isEmpty()) {
            Factor multipliedFactor = multiplyFactors(relevantFactors);
            multipliedFactor.removeVariableFromRows(variableName);
            List<FactorRow> rows = sumOut(multipliedFactor, variableName);
            multipliedFactor.setFactorRows(rows);
            factorList.removeAll(relevantFactors);
            factorList.add(multipliedFactor);
            variableMap.remove(variableName);
        }
    }

    /**
     * Inner class to store the query result, including the probability and the number of operations performed.
     */
    private static class QueryResult {
        private double probability;
        private int additionOperations;
        private int multiplicationOperations;

        /**
         * Increments the count of addition operations.
         *
         * @param incrementBy the number to increment by
         */
        public void incrementAdditionOperations(int incrementBy) {
            additionOperations += incrementBy;
        }

        /**
         * Increments the count of multiplication operations.
         *
         * @param incrementBy the number to increment by
         */
        public void incrementMultiplicationOperations(int incrementBy) {
            multiplicationOperations += incrementBy;
        }

        /**
         * Sets the probability, rounding it to 5 decimal places.
         *
         * @param probability the probability to set
         */
        public void setProbability(double probability) {
            BigDecimal result = new BigDecimal(probability).setScale(5, RoundingMode.HALF_UP);
            this.probability = result.doubleValue();
        }

        @Override
        public String toString() {
            String formattedProbability = String.format("%.5f", probability);
            return formattedProbability + "," + additionOperations + "," + multiplicationOperations;
        }
    }
}
