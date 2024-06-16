package bayesball;

import core.BayesianNetwork;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses queries for the Bayes Ball algorithm, which is used for determining d-separation
 * in Bayesian Networks.
 */
public class BayesBallQueryParser {

    private final BayesianNetwork network; // The Bayesian Network
    private final BayesBallQuery bayesBallQuery; // The Bayes Ball query object

    /**
     * Constructor initializes the network and BayesBallQuery.
     *
     * @param network the Bayesian Network
     */
    public BayesBallQueryParser(BayesianNetwork network) {
        this.network = network;
        this.bayesBallQuery = new BayesBallQuery();
    }

    /**
     * Parses a query string into a BayesBallQuery object.
     *
     * @param query the query string
     * @return the parsed BayesBallQuery object
     */
    public BayesBallQuery parseQuery(String query) {
        String[] parts = query.split("\\|");
        parseStartAndEndVariables(parts[0]); // Parse the start and end variables
        if (parts.length > 1) {
            parseEvidenceVariables(parts[1]); // Parse the evidence variables if present
        }
        return bayesBallQuery;
    }

    /**
     * Parses the evidence variables from the query string.
     *
     * @param part the part of the query string containing evidence variables
     */
    private void parseEvidenceVariables(String part) {
        Pattern pattern = Pattern.compile("([\\w']+)\\s*=\\s*([^,]+)");
        Matcher matcher = pattern.matcher(part);
        while (matcher.find()) {
            String evidenceVariable = matcher.group(1);
            bayesBallQuery.addEvidenceVariable(network.getVariableMap().get(evidenceVariable)); // Add the evidence variable to the query
        }
    }

    /**
     * Parses the start and end variables from the query string.
     *
     * @param part the part of the query string containing start and end variables
     */
    private void parseStartAndEndVariables(String part) {
        String[] variables = part.split("-");
        bayesBallQuery.setStartVariable(network.getVariableMap().get(variables[0])); // Set the start variable
        bayesBallQuery.setEndVariable(network.getVariableMap().get(variables[1])); // Set the end variable
    }
}
