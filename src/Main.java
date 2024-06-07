import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        BnParser parser = new BnParser();
        parser.parseBayesianNetwork("alarm_net.xml");
        BayesianNetwork network = parser.getBayesianNetwork();


        Variable L = new Variable("L",List.of("T","F"));
        Variable R = new Variable("R",List.of("T","F"));
        Variable D = new Variable("D",List.of("T","F"));
        Variable T = new Variable("T",List.of("T","F"));
        Variable B = new Variable("B",List.of("T","F"));
        Variable Tprime = new Variable("Tprime'",List.of("T","F"));

        R.addParent(L);
        L.addChild(R);
        D.addParent(R);
        R.addChild(D);
        R.addChild(T);
        B.addChild(T);
        T.addParent(B);
        T.addParent(R);
        Tprime.addParent(T);
        T.addChild(Tprime);

        BayesBall bayesBall3 = new BayesBall(L, Tprime);
        bayesBall3.findAllPaths();
        System.out.println("Test 3: L and B are independent given T: " + bayesBall3.isIndependent());
    }

}