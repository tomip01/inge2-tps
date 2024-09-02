package inge2.dataflow.pointstoanalysis;

import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;

import java.util.HashSet;
import java.util.Set;

public class PointsToVisitor extends AbstractStmtSwitch<Void> {

    private final PointsToGraph pointsToGraph;

    public PointsToVisitor(PointsToGraph pointsToGraph) {
        this.pointsToGraph = pointsToGraph;
    }

    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        boolean isLeftLocal = stmt.getLeftOp() instanceof JimpleLocal;
        boolean isRightLocal = stmt.getRightOp() instanceof JimpleLocal;

        boolean isLeftField = stmt.getLeftOp() instanceof JInstanceFieldRef;
        boolean isRightField = stmt.getRightOp() instanceof JInstanceFieldRef;

        boolean isRightNew = stmt.getRightOp() instanceof AnyNewExpr;

        if (isRightNew) { // x = new A()
            processNewObject(stmt);
        } else if (isLeftLocal && isRightLocal) { // x = y
            processCopy(stmt);
        } else if (isLeftField && isRightLocal) { // x.f = y
            processStore(stmt);
        } else if (isLeftLocal && isRightField) { // x = y.f
            processLoad(stmt);
        }
    }

    private void processNewObject(AssignStmt stmt) {
        String leftVariableName = stmt.getLeftOp().toString();
        Node nodeName = pointsToGraph.getNodeName(stmt);

        // al asignar a una variable un nuevo objeto, le asignamos el conjunto de nodos
        // que solo contiene a un nuevo nodo, que representa nuevo objeto creado en esa línea de código

        Set<Node> newSet = new HashSet<>();
        newSet.add(nodeName);

        pointsToGraph.setNodesForVariable(leftVariableName, newSet);
    }

    private void processCopy(AssignStmt stmt) {
        String leftVariableName = stmt.getLeftOp().toString();
        String rightVariableName = stmt.getRightOp().toString();

        // buscamos el conjunto de nodos de la variable derecha y se lo asignamos a la izquierda

        Set<Node> rightVariableNodes = pointsToGraph.getNodesForVariable(rightVariableName);
        pointsToGraph.setNodesForVariable(leftVariableName, rightVariableNodes);

    }

    private void processStore(AssignStmt stmt) { // x.f = y
        JInstanceFieldRef leftFieldRef = (JInstanceFieldRef) stmt.getLeftOp();
        String leftVariableName = leftFieldRef.getBase().toString();
        String fieldName = leftFieldRef.getField().getName();
        String rightVariableName = stmt.getRightOp().toString();

        // Buscamos todos los nodos a los que apunta cada variable.
        // Para cada nodo de la variable izquierda, agregamos el eje con el campo fieldName
        // para todos los nodos de la variable derecha

        Set<Node> rightVariableNodes = pointsToGraph.getNodesForVariable(rightVariableName);
        Set<Node> leftVariableNodes = pointsToGraph.getNodesForVariable(leftVariableName);

        for (Node leftNode : leftVariableNodes) {
            for (Node rightNode : rightVariableNodes) {
                pointsToGraph.addEdge(leftNode, fieldName, rightNode);
            }
        }
    }

    private void processLoad(AssignStmt stmt) { // x = y.f
        String leftVariableName = stmt.getLeftOp().toString();
        JInstanceFieldRef rightFieldRef = (JInstanceFieldRef) stmt.getRightOp();
        String rightVariableName = rightFieldRef.getBase().toString();
        String fieldName = rightFieldRef.getField().getName();

        // buscamos todos los nodos que pueden ser accedidos mediante el campo en la variable derecha
        // y se lo asignamos a la variable de la izquierda

        Set<Node> rightVariableNodes = pointsToGraph.getNodesForVariable(rightVariableName);
        Set<Node> allReachableNodesForFieldName = new HashSet<>();

        for (Node rightNode : rightVariableNodes) {
            Set<Node> reachableRightNodes = pointsToGraph.getReachableNodesByField(rightNode, fieldName);
            allReachableNodesForFieldName.addAll(reachableRightNodes);
        }

        pointsToGraph.setNodesForVariable(leftVariableName, allReachableNodesForFieldName);
    }
}
