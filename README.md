# BayesNetAnalysis

## Overview
BayesNetAnalysis is a Java implementation of several key algorithms used in the manipulation and analysis of Bayesian Networks. This project includes functionality for the Bayes-Ball algorithm to determine conditional independence, variable elimination for probabilistic queries, and XML-based network parsing. It is primarily aimed at demonstrating the practical application and implementation of these algorithms.

## Features
- **Bayes-Ball Algorithm:** Assess conditional independence between two variables in a Bayesian network.
- **Variable Elimination:** Implement variable elimination for exact probabilistic inference.
- **XML Network Parsing:** Support for loading Bayesian networks from XML files, enabling straightforward integration and testing.
- **Query Parsing:** Parse and execute queries for variable elimination based on user-defined conditions and evidence.

## Installation

To run BayesNetAnalysis, you need Java installed on your system (Java 8 or higher recommended). You can clone the repository directly using:

```
git clone https://github.com/AmitRazi/BayesNetAnalysis.git
```

## Usage

First, compile the Java files in the `src` directory using a Java compiler such as `javac`:

```bash
javac *.java
```

Then, you can run the compiled classes with Java. Assuming you have a main class setup (adjust accordingly if you use a different class as your entry point):

```bash
java Main
```

### Example Usage

```java
BayesianNetwork network = new BayesianNetworkParser().parseBayesianNetwork("path/to/your/network.xml");
VariableElimination ve = new VariableElimination(network, new VariableEliminationQuery());
ve.executeQuery();
System.out.println("Result: " + ve.getResult());
```

## Documentation

For more detailed information on how to utilize each component of BayesNetAnalysis, refer to the inline comments within each class file. These comments provide thorough explanations of the methods and their functionalities.
