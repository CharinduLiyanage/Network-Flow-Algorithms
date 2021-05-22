import java.io.IOException;
import java.util.Arrays;

public class GraphTest {
    public static void main(String[] args) {

        // Initializing Graph.
        Graph graph = new Graph();

        // Reading Graph from file.
        try {
            graph.readFile("inputs/input_1.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Printing the graph...");
        System.out.println(graph);

        // Demo Search Edge.
        System.out.println("Searching an Edge...");
        int node = 0;
        int[][] searchResults = graph.searchEdges(node);

        // Printing the search output in raw format.
        System.out.println(Arrays.deepToString(searchResults));

        // Demonstrating the Search Output.
        // Demonstrating Incoming Edges.
        System.out.println("Incoming Edges...");
        StringBuilder incomingEdgesStr = new StringBuilder();
        for (int originNode = 0; originNode < searchResults[0].length; originNode++) {
            int capacity = searchResults[0][originNode];
            if (capacity != 0) {
                incomingEdgesStr.append(originNode).append(" -> ").append(node).append(", ").append(capacity).append("\n");
            }
        }
        System.out.println(incomingEdgesStr);

        // Demonstrating Incoming Edges.
        System.out.println("Outgoing Edges...");
        StringBuilder outgoingEdgesStr = new StringBuilder();
        for (int destinationNode = 0; destinationNode < searchResults[0].length; destinationNode++) {
            int capacity = searchResults[1][destinationNode];
            if (capacity != 0) {
                outgoingEdgesStr.append(node).append(" -> ").append(destinationNode).append(", ").append(capacity).append("\n");
            }
        }
        System.out.println(outgoingEdgesStr);

        // Demonstrating Deleting an edge.
        graph.deleteEdge(0, 1);
        System.out.println("Graph after Deleting 0 -> 1 Node...");
        System.out.println(graph);

        // Demonstrating Adding an Edge.
        System.out.println("Graph after Adding 0 -> 1 Node with 6 capacity...");
        graph.addEdge(0, 1, 6);
        System.out.println(graph);

        // Calculating the Maximum Flow.
        graph.calculateMaxFlow();

        // Writing Output.
        System.out.println("Writing Maximum flow output to the updated graph...");
        try {
            graph.writeFile("outputs/input_1_result.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
