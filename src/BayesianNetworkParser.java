import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class parses a Bayesian Network from an XML file and creates the corresponding BayesianNetwork object.
 * It also parses queries from strings.
 */
public class BayesianNetworkParser {
    private final Map<String, Variable> variablesMap = new LinkedHashMap<>(); // Map of variable names to Variable objects
    private final List<Factor> factorList = new ArrayList<>(); // List of factors in the Bayesian Network
    private final BayesianNetwork bayesianNetwork = new BayesianNetwork(); // The Bayesian Network

    /**
     * Gets the parsed Bayesian Network.
     *
     * @return the Bayesian Network
     */
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

    /**
     * Parses the definitions of the Bayesian Network from the XML document.
     *
     * @param doc the XML document
     */
    private void parseDefinitions(Document doc) {
        NodeList definitionList = doc.getElementsByTagName("DEFINITION");

        for (int i = 0; i < definitionList.getLength(); ++i) {
            Element definitionElement = (Element) definitionList.item(i);
            parseSingleDefinition(definitionElement);
        }
    }

    /**
     * Parses a single definition element and adds the corresponding factor to the factor list.
     *
     * @param definitionElement the definition element
     */
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

    /**
     * Generates factor rows based on the variables and their probability table.
     *
     * @param variables       the list of variables
     * @param probabilityTable the probability table
     * @return the list of factor rows
     */
    private List<FactorRow> generateRows(List<Variable> variables, List<Double> probabilityTable) {
        List<FactorRow> result = new ArrayList<>();
        int[] probabilityTableIndex = new int[]{0};
        generateRowsHelper(variables, probabilityTable, new HashMap<>(), result, 0, probabilityTableIndex);
        return result;
    }

    /**
     * Helper method to recursively generate factor rows.
     *
     * @param variables           the list of variables
     * @param probabilityTable    the probability table
     * @param currentStateMap     the current state map of variable outcomes
     * @param result              the resulting list of factor rows
     * @param index               the current index in the variable list
     * @param probabilityTableIndex the index in the probability table
     */
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

    /**
     * Parses the variables of the Bayesian Network from the XML document.
     *
     * @param doc the XML document
     */
    private void parseVariables(Document doc) {
        NodeList variableElementsList = doc.getElementsByTagName("VARIABLE");

        for (int i = 0; i < variableElementsList.getLength(); ++i) {
            Element variableElement = (Element) variableElementsList.item(i);
            String name = variableElement.getElementsByTagName("NAME").item(0).getTextContent();
            List<String> outcomes = parseOutcomes(variableElement);
            variablesMap.put(name, new Variable(name, outcomes));
        }
    }

    /**
     * Parses the outcomes of a variable from the XML element.
     *
     * @param variableElement the variable element
     * @return the list of outcomes
     */
    private List<String> parseOutcomes(Element variableElement) {
        List<String> outcomes = new ArrayList<>();
        NodeList outcomesListElement = variableElement.getElementsByTagName("OUTCOME");
        for (int j = 0; j < outcomesListElement.getLength(); ++j) {
            outcomes.add(outcomesListElement.item(j).getTextContent());
        }
        return outcomes;
    }

}
