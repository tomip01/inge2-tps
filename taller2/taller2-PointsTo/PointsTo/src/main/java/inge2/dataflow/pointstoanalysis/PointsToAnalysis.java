package inge2.dataflow.pointstoanalysis;

import soot.Unit;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

import java.util.HashSet;
import java.util.Set;

public class PointsToAnalysis extends ForwardFlowAnalysis<Unit, PointsToGraph> {

    private PointsToGraph lastPointsToGraph;

    public PointsToAnalysis(UnitGraph graph) {
        super(graph);
        doAnalysis();
    }

    public PointsToGraph getLastPointsToGraph() {
        return lastPointsToGraph;
    }

    /**
     * This method is called for each unit in the control flow graph.
     * @param in the input flow
     * @param unit the current node
     * @param out the returned flow
     */
    @Override
    protected void flowThrough(PointsToGraph in, Unit unit, PointsToGraph out) {
        out.copy(in);

        PointsToVisitor visitor = new PointsToVisitor(out);
        unit.apply(visitor);

        this.lastPointsToGraph = out;
    }

    @Override
    protected PointsToGraph newInitialFlow() {
        return new PointsToGraph();
    }

    /**
     * This method merges the two input flows into a single output flow.
     * @param input1 the first input flow
     * @param input2 the second input flow
     * @param output the returned flow
     */
    @Override
    protected void merge(PointsToGraph input1, PointsToGraph input2, PointsToGraph output) {
        output.copy(input1);
        output.union(input2);
    }

    @Override
    protected void copy(PointsToGraph source, PointsToGraph dest) {
        dest.copy(source);
    }
    /**
     * Retorna true si alguno de los objetos apuntados por leftVariableName y rightVariableName coinciden.
     * @param leftVariableName
     * @param rightVariableName
     * @return
     */
    public boolean mayAlias(String leftVariableName, String rightVariableName) {

        // para que haya un alias, las variables deben apuntar a algún mismo nodo
        // Para eso, creamos una copia de ambos conjuntos
        // nos quedamos con la intersección y devolvemos si no es vacía

        Set<Node> leftVariableNodes = new HashSet<>(getLastPointsToGraph().getNodesForVariable(leftVariableName));
        Set<Node> rightVariableNodes = new HashSet<>(getLastPointsToGraph().getNodesForVariable(rightVariableName));

        leftVariableNodes.retainAll(rightVariableNodes);

        return !leftVariableNodes.isEmpty();
    }

    /**
     * Retorna true si alguno de los objetos apuntados por leftVariableName.fieldName y rightVariableName coinciden.
     * @param leftVariableName
     * @param fieldName
     * @param rightVariableName
     * @return
     */
    public boolean mayAlias(String leftVariableName, String fieldName, String rightVariableName) {

        // para que haya un alias, algún camino desde la variable izquieda por medio del campo,
        // debe coincidir con algún nodo que apunte la variable derecha
        // recorremos todos los nodos alcanzables por el campo del conjunto de nodos de la variable izquierda
        // verificamos si hay alias con la variable derecha

        Set<Node> leftVariableNodes = getLastPointsToGraph().getNodesForVariable(leftVariableName);
        Set<Node> rightVariableNodes = getLastPointsToGraph().getNodesForVariable(rightVariableName);


        for (Node leftNode: leftVariableNodes){
            Set<Node> reachableNodes = getLastPointsToGraph().getReachableNodesByField(leftNode, fieldName);
            for (Node reachableNode: reachableNodes) {
                for (Node rightNode : rightVariableNodes) {
                    if (reachableNode.equals(rightNode)) return true;
                }
            }
        }

        return false;
    }
}
