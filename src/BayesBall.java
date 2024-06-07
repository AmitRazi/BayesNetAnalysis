//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BayesBall {
    private final Variable startVariable;
    private Variable destinationVariable;
    private boolean isIndependent = false;

    public BayesBall(Variable startVariable, Variable destinationVariable) {
        this.startVariable = startVariable;
        this.destinationVariable = destinationVariable;
    }

    public void findAllPaths() {
        List<List<Variable>> paths = new ArrayList();
        Set<Variable> visited = new HashSet();
        List<Variable> currentPath = new ArrayList();
        this.findPathsDFS(this.startVariable, currentPath, visited, paths);
    }

    private void findPathsDFS(Variable currentVariable, List<Variable> currentPath, Set<Variable> visited, List<List<Variable>> paths) {
        visited.add(currentVariable);
        currentPath.add(currentVariable);
        if (currentVariable.getName().equals(this.destinationVariable.getName())) {
            paths.add(new ArrayList(currentPath));
            this.validatePath(currentPath);
        } else {
            Iterator var5 = currentVariable.getChildren().iterator();

            Variable parent;
            while(var5.hasNext()) {
                parent = (Variable)var5.next();
                if (!visited.contains(parent)) {
                    this.findPathsDFS(parent, currentPath, visited, paths);
                }
            }

            var5 = currentVariable.getParents().iterator();

            while(var5.hasNext()) {
                parent = (Variable)var5.next();
                if (!visited.contains(parent)) {
                    this.findPathsDFS(parent, currentPath, visited, paths);
                }
            }
        }

        currentPath.remove(currentPath.size() - 1);
        visited.remove(currentVariable);
    }

    private void validatePath(List<Variable> currentPath) {
        boolean directionUp = false;

        for(int i = 0; i < currentPath.size() - 2; ++i) {
            Variable previous = (Variable)currentPath.get(i);
            Variable current = (Variable)currentPath.get(i + 1);
            Variable next = (Variable)currentPath.get(i + 2);
            if (previous.isParentOf(current)) {
                directionUp = true;
            } else {
                directionUp = false;
            }

            if (current.isEvidence() && !directionUp) {
                this.isIndependent = true;
                return;
            }

            if (current.isEvidence() && directionUp && !next.isParentOf(current)) {
                this.isIndependent = true;
                return;
            }

            if (!current.isEvidence() && directionUp && next.isParentOf(current)) {
                this.isIndependent = true;
                return;
            }
        }

        this.isIndependent = false;
    }

    public boolean isIndependent() {
        return this.isIndependent;
    }
}
