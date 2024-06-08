import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BayesBallQueryParser {

    private final BayesianNetwork network;
    private final BayesBallQuery bayesBallQuery;

    public BayesBallQueryParser(BayesianNetwork network) {
        this.network = network;
        this.bayesBallQuery = new BayesBallQuery();
    }

    public BayesBallQuery parseQuery(String query){
        String[] parts = query.split("\\|");
        parseStartAndEndVariables(parts[0]);
        if(parts.length > 1){
            parseEvidenceVariables(parts[1]);
        }
        return bayesBallQuery;
    }

    private void parseEvidenceVariables(String part) {
        Pattern pattern = Pattern.compile("([\\w']+)\\s*=\\s*([^,]+)");
        Matcher matcher = pattern.matcher(part);
        while (matcher.find()) {
            String evidenceVariable = matcher.group(1);
            bayesBallQuery.addEvidenceVariable(network.getVariableMap().get(evidenceVariable));
        }
    }

    private void parseStartAndEndVariables(String part) {
        String[] variables = part.split("-");
        bayesBallQuery.setStartVariable(network.getVariableMap().get(variables[0]));
        bayesBallQuery.setEndVariable(network.getVariableMap().get(variables[1]));
    }
}
