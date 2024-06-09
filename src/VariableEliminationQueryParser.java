import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VariableEliminationQueryParser {
    private final BayesianNetwork bayesianNetwork;

    public VariableEliminationQueryParser(BayesianNetwork network){
        this.bayesianNetwork = network;
    }

    /**
     * Parses a query from a string.
     *
     * @param queryStr the query string
     * @return the parsed Query object
     */
    public VariableEliminationQuery parseQuery(String queryStr) {
        VariableEliminationQuery variableEliminationQuery = new VariableEliminationQuery();
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(queryStr);
        matcher.find();
        String parsedQuery = matcher.group(1);
        String[] variables = parsedQuery.split("[|,]");
        extractQueryVariable(variableEliminationQuery, variables);
        extractEvidence(variableEliminationQuery, variables);
        extractEliminationOrder(variableEliminationQuery, queryStr);
        return variableEliminationQuery;
    }

    /**
     * Extracts the elimination order from the query string.
     *
     * @param variableEliminationQuery the Query object
     * @param queryStr the query string
     */
    private void extractEliminationOrder(VariableEliminationQuery variableEliminationQuery, String queryStr) {
        Pattern pattern = Pattern.compile(".*\\)(.*)");
        Matcher match = pattern.matcher(queryStr);
        match.find();
        // Extract elimination variables from the query string after the closing parenthesis
        Queue<String> eliminationVariables = Arrays.stream(match.group(1).split("-")).map(String::trim).collect(
                Collectors.toCollection(ArrayDeque::new));
        variableEliminationQuery.setEliminationVariables(eliminationVariables);
    }

    /**
     * Extracts the evidence variables from the parsed variables.
     *
     * @param variableEliminationQuery the Query object
     * @param variables the parsed variables
     */
    private void extractEvidence(VariableEliminationQuery variableEliminationQuery, String[] variables) {
        for (int i = 1; i < variables.length; ++i) {
            Variable evidence = bayesianNetwork.getVariableMap().get(variables[i].split("=")[0]);
            evidence.setEvidence(true);
            String evidenceOutcome = variables[i].split("=")[1];
            variableEliminationQuery.addEvidenceVariable(new Pair<>(evidence, evidenceOutcome));
        }
    }

    /**
     * Extracts the query variable from the parsed variables.
     *
     * @param variableEliminationQuery the Query object
     * @param variables the parsed variables
     */
    private void extractQueryVariable(VariableEliminationQuery variableEliminationQuery, String[] variables) {
        Variable queryVariable = bayesianNetwork.getVariableMap().get(variables[0].split("=")[0]);
        String queryVariableOutcome = variables[0].split("=")[1];
        variableEliminationQuery.setQueryVariable(new Pair<>(queryVariable, queryVariableOutcome));
    }
}
