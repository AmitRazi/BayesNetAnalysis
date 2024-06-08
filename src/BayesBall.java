import java.util.*;

/**
 * This class implements the Bayes-Ball algorithm to determine conditional independence
 * between two variables in a Bayesian Network.
 */
public class BayesBall {
    private BayesBallQuery bayesBallQuery;
    private final Map<Variable, VisitCount> visitCountMap;


    public BayesBall(BayesBallQuery query) {
        this.bayesBallQuery = query;
        this.visitCountMap = new HashMap<>();
    }

    /**
     * Initiates the search for all paths between the start and destination variables.
     */
    public void findAllPaths() {
        List<Variable> currentPath = new ArrayList<>();
        findPathsDFS(bayesBallQuery.getStartVariable(), currentPath, null, false);
    }

    /**
     * Performs a depth-first search to find paths between the start and destination variables.
     *
     * @param currentVariable   the current variable in the path
     * @param currentPath       the current path of variables
     * @param previousVariable  the previous variable in the path
     * @param fromChild         indicates if the current variable was reached from a child
     */
    private void findPathsDFS(Variable currentVariable, List<Variable> currentPath, Variable previousVariable, boolean fromChild) {
        if (!bayesBallQuery.isIndependent()) return;

        VisitCount visitCount = visitCountMap.getOrDefault(currentVariable, new VisitCount());

        if (previousVariable != null) {
            if (!fromChild) {
                if (visitCount.getFromParentCount() >= 1) return;
                visitCount.incrementFromParent();
            } else {
                if (visitCount.getFromChildCount() >= 1) return;
                visitCount.incrementFromChild();
            }
        }

        visitCountMap.put(currentVariable, visitCount);
        currentPath.add(currentVariable);

        if (currentVariable.equals(bayesBallQuery.getEndVariable())) {
            validatePath(new ArrayList<>(currentPath));
        } else {
            explorePaths(currentVariable, currentPath);
        }

        currentPath.remove(currentPath.size() - 1);
        adjustVisitCount(visitCount, fromChild);
        visitCountMap.put(currentVariable, visitCount);
    }

    /**
     * Explores the paths from the current variable to its parents and children.
     *
     * @param currentVariable the current variable in the path
     * @param currentPath     the current path of variables
     */
    private void explorePaths(Variable currentVariable, List<Variable> currentPath) {
        for (Variable child : currentVariable.getChildren()) {
            findPathsDFS(child, currentPath, currentVariable, false);
        }
        for (Variable parent : currentVariable.getParents()) {
            findPathsDFS(parent, currentPath, currentVariable, true);
        }
    }

    /**
     * Adjusts the visit count for the current variable based on the direction of traversal.
     *
     * @param visitCount the visit count for the current variable
     * @param fromChild  indicates if the current variable was reached from a child
     */
    private void adjustVisitCount(VisitCount visitCount, boolean fromChild) {
        if (!fromChild) {
            visitCount.decrementFromParent();
        } else {
            visitCount.decrementFromChild();
        }
    }

    /**
     * Validates if the current path shows independence between the start and destination variables.
     *
     * @param path the current path of variables
     */
    private void validatePath(List<Variable> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Variable prev = path.get(i);
            Variable current = path.get(i + 1);
            Variable next = (i + 2 < path.size()) ? path.get(i + 2) : null;

            boolean directionUp = prev.isParentOf(current);

            if (bayesBallQuery.includesEvidence(current)) {
                if (directionUp && next != null && !next.isParentOf(current)) {
                    return;
                } else if (!directionUp) {
                    return;
                }
            } else {
                if (directionUp && next != null && !next.isChildOf(current)) {
                    return;
                }
            }
        }
        bayesBallQuery.setIndependent(false);
    }

    /**
     * Returns whether the start variable is independent of the destination variable.
     *
     * @return true if independent, false otherwise
     */
    public boolean isIndependent() {
        return bayesBallQuery.isIndependent();
    }

    public void executeQuery() {
        findAllPaths();
    }

    /**
     * Inner class to track the number of times a variable is visited from parents and children.
     */
    private static class VisitCount {
        private int fromParentCount = 0;
        private int fromChildCount = 0;

        public void incrementFromParent() {
            fromParentCount++;
        }

        public void incrementFromChild() {
            fromChildCount++;
        }

        public void decrementFromParent() {
            fromParentCount--;
        }

        public void decrementFromChild() {
            fromChildCount--;
        }

        public int getFromParentCount() {
            return fromParentCount;
        }

        public int getFromChildCount() {
            return fromChildCount;
        }
    }
}
