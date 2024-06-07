import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BnParser {
    private final Map<String, Variable> variablesMap = new HashMap<>();
    private final List<Factor> factorList = new ArrayList<>();
    private final BayesianNetwork bayesianNetwork = new BayesianNetwork();

    public BayesianNetwork getBayesianNetwork() {
        return bayesianNetwork;
    }

    /**
     * Parses the Bayesian network from an XML file.
     *
     * @param xmlPath the path to the XML file
     */
    public void parseBayesianNetwork(String xmlPath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlPath);
            parseVariables(doc);
            parseDefinitions(doc);

            bayesianNetwork.setVariableMap(variablesMap);
            bayesianNetwork.setFactorList(factorList);

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException("Error parsing Bayesian Network XML file: " + e.getMessage(), e);
        }
    }

    private void parseDefinitions(Document doc) {
        NodeList definitionList = doc.getElementsByTagName("DEFINITION");

        for (int i = 0; i < definitionList.getLength(); ++i) {
            Element definitionElement = (Element) definitionList.item(i);
            parseSingleDefinition(definitionElement);
        }
    }

    private void parseSingleDefinition(Element definitionElement) {
        Variable newVariable = variablesMap.get(definitionElement.getElementsByTagName("FOR").item(0).getTextContent());
        List<Variable> variableList = new ArrayList<>();
        NodeList parentsElementList = definitionElement.getElementsByTagName("GIVEN");

        for (int j = 0; j < parentsElementList.getLength(); ++j) {
            Variable parentVariable = variablesMap.get(parentsElementList.item(j).getTextContent());
            newVariable.addParent(parentVariable);
            parentVariable.addChild(newVariable);
            variableList.add(parentVariable);
        }

        List<Double> table = Arrays.stream(definitionElement.getElementsByTagName("TABLE").item(0).getTextContent().split(" "))
                                   .map(Double::parseDouble)
                                   .collect(Collectors.toList());
        variableList.add(newVariable);
        List<FactorRow> factorRows = generateRows(variableList, table);
        factorList.add(new Factor(newVariable, variableList.stream().collect(Collectors.toMap(Variable::getName, variable -> variable, (existing, replacement) -> existing, LinkedHashMap::new)), factorRows));
    }

    private List<FactorRow> generateRows(List<Variable> variables, List<Double> probabilityTable) {
        List<FactorRow> result = new ArrayList<>();
        int[] probabilityTableIndex = new int[]{0};
        generateRowsHelper(variables, probabilityTable, new HashMap<>(), result, 0, probabilityTableIndex);
        return result;
    }

    private void generateRowsHelper(List<Variable> variables, List<Double> probabilityTable, Map<String, String> currentStateMap, List<FactorRow> result, int index, int[] probabilityTableIndex) {
        if (index == variables.size()) {
            result.add(new FactorRow(new HashMap<>(currentStateMap), probabilityTable.get(probabilityTableIndex[0])));
            probabilityTableIndex[0]++;
        } else {
            Variable currentVariable = variables.get(index);
            for (String outcome : currentVariable.getOutcomes()) {
                currentStateMap.put(currentVariable.getName(), outcome);
                generateRowsHelper(variables, probabilityTable, currentStateMap, result, index + 1, probabilityTableIndex);
                currentStateMap.remove(currentVariable.getName());
            }
        }
    }

    private void parseVariables(Document doc) {
        NodeList variableElementsList = doc.getElementsByTagName("VARIABLE");

        for (int i = 0; i < variableElementsList.getLength(); ++i) {
            Element variableElement = (Element) variableElementsList.item(i);
            String name = variableElement.getElementsByTagName("NAME").item(0).getTextContent();
            List<String> outcomes = parseOutcomes(variableElement);
            variablesMap.put(name, new Variable(name, outcomes));
        }
    }

    private List<String> parseOutcomes(Element variableElement) {
        List<String> outcomes = new ArrayList<>();
        NodeList outcomesListElement = variableElement.getElementsByTagName("OUTCOME");
        for (int j = 0; j < outcomesListElement.getLength(); ++j) {
            outcomes.add(outcomesListElement.item(j).getTextContent());
        }
        return outcomes;
    }

    /**
     * Parses a query from a string.
     *
     * @param queryStr the query string
     * @return the parsed Query object
     */
    public Query parseQuery(String queryStr) {
        Query query = new Query();
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(queryStr);
        matcher.find();
        String parsedQuery = matcher.group(1);
        String[] variables = parsedQuery.split("[|,]");
        extractQueryVariable(query, variables);
        extractEvidence(query, variables);
        extractEliminationOrder(query, queryStr);
        return query;
    }

    private void extractEliminationOrder(Query query, String queryStr) {
        Pattern pattern = Pattern.compile(".*\\)(.*)");
        Matcher match = pattern.matcher(queryStr);
        match.find();
        Queue<String> eliminationVariables = Arrays.stream(match.group(1).split("-")).map(String::trim).collect(Collectors.toCollection(ArrayDeque::new));
        query.setEliminationVariables(eliminationVariables);
    }

    private void extractEvidence(Query query, String[] variables) {
        for (int i = 1; i < variables.length; ++i) {
            Variable evidence = variablesMap.get(variables[i].split("=")[0]);
            evidence.setEvidence(true);
            String evidenceOutcome = variables[i].split("=")[1];
            query.addEvidenceVariable(new Pair<>(evidence, evidenceOutcome));
        }
    }

    private void extractQueryVariable(Query query, String[] variables) {
        Variable queryVariable = variablesMap.get(variables[0].split("=")[0]);
        String queryVariableOutcome = variables[0].split("=")[1];
        query.setQueryVariable(new Pair<>(queryVariable, queryVariableOutcome));
    }
}
