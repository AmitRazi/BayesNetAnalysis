import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        BnParser parser = new BnParser();
        parser.parseBayesianNetwork("alarm_net.xml");
        Query query = parser.parseQuery("P(J=T|B=T) A-E-M");
        BayesianNetwork bayesianNetwork = parser.bayesianNetwork;
        VariableElimination ve = new VariableElimination(bayesianNetwork, query);
        ve.query();
    }
}