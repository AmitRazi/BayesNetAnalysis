import java.io.*;

public class Main {
    public static void main(String[] args)  {


        BayesianNetwork network = null;
        String inputFilePath = "ExerciseExampleTernary.txt";
        String outputFilePath = "output.txt";
        int lineIndex = 0;
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFilePath));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFilePath))){

            String line;
            while((line = bufferedReader.readLine()) != null){
                if(lineIndex == 0){
                    BayesianNetworkParser parser = new BayesianNetworkParser();
                    parser.parseBayesianNetwork(line);
                    network = parser.getBayesianNetwork();
                    lineIndex++;
                } else{
                    if(line.startsWith("P(")) {
                        VariableEliminationQueryParser variableEliminationQueryParser = new VariableEliminationQueryParser(network);
                        VariableEliminationQuery variableEliminationQuery = variableEliminationQueryParser.parseQuery(line);
                        VariableElimination ve = new VariableElimination(network, variableEliminationQuery);
                        ve.executeQuery();
                        bufferedWriter.write(ve.getResult()+"\n");
                    } else {
                        BayesBallQueryParser bayesBallQueryParser = new BayesBallQueryParser(network);
                        BayesBallQuery bayesBallQuery = bayesBallQueryParser.parseQuery(line);
                        BayesBall bayesBall = new BayesBall(bayesBallQuery);
                        bayesBall.executeQuery();
                        bufferedWriter.write(bayesBallQuery.isIndependent() ? "yes\n" : "no\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
