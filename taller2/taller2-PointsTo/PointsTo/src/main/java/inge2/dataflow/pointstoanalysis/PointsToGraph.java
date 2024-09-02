package inge2.dataflow.pointstoanalysis;

import soot.Unit;
import soot.jimple.AssignStmt;
import soot.tagkit.LineNumberTag;

import java.util.*;

public class PointsToGraph {

    /**
     * Nodos del grafo.
     *
     * Cada nodo representa todos los objetos creados por cada sentencia "new".
     * Es decir, tenemos un nodo por cada "new" en el programa.
     */
    public Set<Node> nodes;

    /**
     * Ejes del grafo.
     *
     * Un eje (n1, f, n2) indica que el los objetos representados por el nodo n1 tienen un campo f que apunta al/los
     * objetos representados por n2.
     */
    public Set<Axis> axis;

    /**
     * Mapping de variables locales a nodos.
     * Representa el conjunto de objetos a los que puede apuntar una variable local.
     */
    public Map<String, Set<Node>> mapping;

    public PointsToGraph(){
        nodes = new HashSet<>();
        axis = new HashSet<>();
        mapping = new HashMap<>();
    }

    public void clear() {
        nodes.clear();
        axis.clear();
        mapping.clear();
    }

    /**
     * Devuelve el nombre del nodo correspondiente a la sentencia <code>stmt</code>.
     * @param stmt
     * @return
     */
    public Node getNodeName(AssignStmt stmt) {
        LineNumberTag lineNumberTag = (LineNumberTag) stmt.getTag("LineNumberTag");
        return new Node(String.valueOf(lineNumberTag.getLineNumber()));
    }

    /**
     * Devuelve el conjunto de nodos a los que apunta la variable <code>variableName</code>.
     * @param variableName
     * @return
     */
    public Set<Node> getNodesForVariable(String variableName) {

        // dentro del mapping hay un conjunto de nodos para cada variable
        // obtenemos el set correspondiente a la variable
        // si no existe la clave, devolvemos un conjunto vacío

        if(!mapping.containsKey(variableName)) return new HashSet<Node>();
        return mapping.get(variableName);
    }

    /**
     * Setea el conjunto de nodos a los que apunta la variable <code>variableName</code>.
     * @param variableName
     * @param nodes
     */
    public void setNodesForVariable(String variableName, Set<Node> nodes) {

        // dentro del mapping hay un conjunto de nodos para cada variable
        // copiamos el set pasado por parámetro en la clave correpondiende del map

        mapping.put(variableName, nodes);
    }

    /**
     * Agrega un eje al grafo.
     * @param leftNode
     * @param fieldName
     * @param rightNode
     */
    public void addEdge(Node leftNode, String fieldName, Node rightNode) {

        // Axis es un conjunto de ejes donde un eje es la tripla (leftNode, fieldName, rightNode)
        // agregamos al conjunto de ejes, la tripla (leftNode, fieldName, rightNode)

        Axis newAxis = new Axis(leftNode,fieldName, rightNode);

        axis.add(newAxis);
    }

    /**
     * Devuelve el conjunto de nodos alcanzables desde el nodo <code>node</code> por el campo <code>fieldName</code>.
     * @param node
     * @param fieldName
     * @return
     */
    public Set<Node> getReachableNodesByField(Node node, String fieldName) {

        // recorremos todos los ejes que el leftNode sea el nodo izquierdo pasado por parámetro
        // y que el fieldName sea el campo pasado por parámetro
        // nos quedamos con todos los ejes que cumplan esto

        Set<Node> res = new HashSet<>();

        for (Axis axis : axis) {
            if (axis.leftNode.equals(node) && axis.fieldName.equals(fieldName)) {
                res.add(axis.rightNode);
            }
        }

        return res;
    }

    /**
     * Copia de un grafo (modifica el this).
     * @param in
     */
    public void copy(PointsToGraph in) {
        this.clear();
        this.union(in);
    }

    /**
     * Union de dos grafos (modifica el this).
     * this = this U in
     * Recordar que hay que unir:
     * los nodos, los ejes y el supermo mapping de variables a nodos
     * @param in el grafo a unir
     */
    public void union(PointsToGraph in) {

        // Para unir dos grafos PointTo es hacer una unión de grafos clásica
        // Hacemos union de conjuntos de nodos del grafo
        // Hacemos union de conjuntos de variables ya que se van a usar todas las variables
        // Para todas las variables que tengan ejes, unimos el conjunto de ejes
        // si coinciden dos variables, hacemos union de sus conjuntos

        nodes.addAll(in.nodes);

        for (String inVariable : in.mapping.keySet()) {

            Set<Node> currentSetNodes = this.getNodesForVariable(inVariable);
            Set<Node> inSetNodes = in.getNodesForVariable(inVariable);
            currentSetNodes.addAll(inSetNodes);
            this.setNodesForVariable(inVariable, currentSetNodes);

        }

        for (Axis axis : in.axis) {
            this.addEdge(axis.leftNode, axis.fieldName, axis.rightNode);
        }

    }
}
