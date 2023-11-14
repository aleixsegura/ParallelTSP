/* ---------------------------------------------------------------
Práctica 2.
Código fuente: RootChildProblem.java
Grau Informàtica
48056540H - Aleix Segura Paz.
21161168H - Aniol Serrano Ortega.
--------------------------------------------------------------- */
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;

public class RootChildProblem implements Callable<Node> {
    private static final PriorityBlockingQueue<Node> nodePriorityQueue = new PriorityBlockingQueue<>(10,
            Collections.reverseOrder(Comparator.comparingInt(Node::getCost)));

    private volatile static Node solution = null;

    private int processedNodes;
    private int purgedNodes;
    private final TSP tsp;
    private final Node root;


    public RootChildProblem(TSP tsp, Node root){
        this.tsp = tsp;
        this.root = root;
        processedNodes = 0;
        purgedNodes = 0;
    }

    // Getters
    public int getProcessedNodes() { return processedNodes; }
    public int getPurgedNodes() { return purgedNodes; }
    public static PriorityBlockingQueue<Node> getNodePriorityQueue() { return nodePriorityQueue; }
    public static Node getSolution() { return solution; }

    /**
     * Each thread adds a sub problem to a PriorityBlockingQueue, then the paths are explored concurrently and
     * each sub problem work with shared instance 'solution' that represents the current best solution and
     * the queue so the nodes in queue and the current best solution affect each thread.
     * @return current best solution or best solution if every path has been explored.
     */
    @Override
    public Node call(){
        pushNode(root);
        Node min;
        int row;

        while ((min = popNode()) != null) {
            processedNodes++;
            row = min.getVertex();

            if (min.getLevel() == TSP.nCities - 1) {
                min.addPathStep(row, 0); // Afegir el cami de tornada.

                if (solution == null || min.getCost() < solution.getCost()) {   // si encara no hi ha solucio possible afegir, si l'actual es millor que la que hi ha substiuir
                    solution = min;
                    purgeWorseNodes(min.getCost());
                }
            }

            for (int column = 0; column < TSP.nCities; column++){
                if (!min.cityVisited(column) && min.getCostMatrix(row, column) != TSP.INF){
                    Node child = new Node(tsp, min, min.getLevel() + 1, row, column);
                    int childCost = min.getCost() + min.getCostMatrix(row, column) + child.calculateCost();
                    child.setCost(childCost);

                    if (solution == null || child.getCost() < solution.getCost())
                        pushNode(child);
                    else if (solution != null && child.getCost() > solution.getCost())
                        purgedNodes++;
                }
            }
        }
        return solution;
    }

    private void pushNode(Node node) { nodePriorityQueue.add(node); }

    private Node popNode() { return nodePriorityQueue.poll(); }

    private void purgeWorseNodes(int minCost) {
        int pendingNodes = nodePriorityQueue.size();
        nodePriorityQueue.removeIf(node -> node.getCost() >= minCost);
        purgedNodes += pendingNodes - nodePriorityQueue.size();
    }
}
