import java.util.*;

public class BayesBall {
    private final Variable startVariable;
    private final Variable destinationVariable;
    private boolean isIndependent;
    private final Map<Variable,VisitCount> visitCountMap;

    public BayesBall(Variable startVariable,
                     Variable destinationVariable
                    ) {
        this.startVariable = startVariable;
        this.destinationVariable = destinationVariable;
        this.isIndependent = true;
        this.visitCountMap = new HashMap<>();
    }

    public void findAllPaths(){
        List<Variable> currentPath = new ArrayList<>();
        findPathsDFS(startVariable, currentPath, null,false);
    }

    private void findPathsDFS(Variable currentVariable, List<Variable> currentPath, Variable previousVariable, boolean fromChild) {
        if (!isIndependent) return;

        VisitCount visitCount = visitCountMap.getOrDefault(currentVariable, new VisitCount());

        if (previousVariable != null) {
            if (!fromChild) { // Coming from parent
                if (visitCount.getFromParentCount() >= 1) return;
                visitCount.incrementFromParent();
            } else { // Coming from child
                if (visitCount.getFromChildCount() >= 1) return;
                visitCount.incrementFromChild();
            }
        }

        visitCountMap.put(currentVariable, visitCount);
        currentPath.add(currentVariable);

        if (currentVariable.equals(destinationVariable)) {
            validatePath(new ArrayList<>(currentPath));
        } else {
            for (Variable child : currentVariable.getChildren()) {
                findPathsDFS(child, currentPath, currentVariable, false);
            }
            for (Variable parent : currentVariable.getParents()) {
                findPathsDFS(parent, currentPath, currentVariable, true);
            }
        }

        currentPath.remove(currentPath.size() - 1);
        if (previousVariable != null) {
            if (!fromChild) {
                visitCount.decrementFromParent();
            } else {
                visitCount.decrementFromChild();
            }
        }

        visitCountMap.put(currentVariable, visitCount);
    }

    private void validatePath(List<Variable> path){
        boolean directionUp;

        for(int i = 0 ; i < path.size() - 1 ; i++){
            Variable prev = path.get(i);
            Variable current = path.get(i+1);
            Variable next = (i + 2 < path.size()) ? path.get(i + 2) : null;

            directionUp = prev.isParentOf(current);

            if(current.isEvidence()){
               if(directionUp && next!=null && !next.isParentOf(current)){
                   return;
               } else if(!directionUp){
                   return;
               }
            } else{
                if(directionUp && next!=null && !next.isChildOf(current)) {
                    return;
                }

            }
        }
        isIndependent = false;
    }

    public boolean isIndependent(){
        return isIndependent;
    }

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