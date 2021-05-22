/*
 * Name     :   Charindu Liyanage
 * UoW ID   :   w1761962
 * "I confirm that I understand what plagiarism / collusion / contract cheating is and have read and understood the
 * section on Assessment Offences in the Essential Information for Students. The work that I have submitted is entirely
 * my own. Any work from other authors is duly referenced and acknowledged."
 */
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Class to create Graph objects.
 */
public class Graph {
    private int noOfVertices; // Number of vertices in graph.
    private int[][] graph;  // Adjacency matrix to store the graph.
    private int source;  // Source nodes in the graph.
    private int target;  //  Destination nodes in the graph.
    private int maxFlow = 0;  // Max flow of the graph.
    private LinkedList<int[]> augmentedPaths = new LinkedList<>();  // Augmented paths in the graph.
    private long processTime;  // Time taken to calculated Maximum flow.

    /**
     * Default constructor to create an Graph object.
     */
    public Graph() {
    }

    /**
     * Method to read the graph from a text file.
     * @param fileLocation Location of the text file.
     * @throws IOException Input exception when the file is not found or found empty.
     * @throws NullPointerException When the file is empty.
     * @throws NumberFormatException When the input file is corrupted.
     * @throws ArrayIndexOutOfBoundsException When the input file is corrupted.
     */
    public void readFile(String fileLocation) throws IOException, NullPointerException, NumberFormatException, ArrayIndexOutOfBoundsException {
        // Opening the file.
        File file = new File(fileLocation);

        // Initialising the reader.
        BufferedReader br = new BufferedReader(new FileReader(file));

        // Reading the no of vertices in the graph.
        while (noOfVertices == 0) {
            String noOfVerticesString = br.readLine().trim();

            // Ignoring the line if the line is blank.
            if (noOfVerticesString.isBlank()) {
                continue;
            }

            int noOfVertices = Integer.parseInt(noOfVerticesString);

            // Setting the basic properties of the graph.
            setNoOfVertices(noOfVertices);
            setGraph(new int[noOfVertices][noOfVertices]);
            setSource(0);
            setTarget(noOfVertices - 1);
        }

        // Reading the edges of the graph line by line.
        String st;
        while ((st = br.readLine()) != null) {

            // Ignoring the line if the line is blank.
            if (st.isBlank()) {
                continue;
            }

            // Removing possible user errors in the input file.
            st = edgeDetailFormatEditor(st);

            String[] edgeDetails = st.split(" ", 3);
            int start = Integer.parseInt(edgeDetails[0]);
            int end = Integer.parseInt(edgeDetails[1]);
            int capacity = Integer.parseInt(edgeDetails[2]);

            // Adding the edges to the graph.
            addEdge(start, end, capacity);
        }

        // Closing the reader.
        br.close();
    }

    /**
     * Method to correct possible user errors in the input file.
     * @param edgeDetailString String read from the text file.
     * @return Updated and edited string.
     */
    private String edgeDetailFormatEditor(String edgeDetailString) {
        // String builder for building the new String.
        StringBuilder converted = new StringBuilder();

        // Checking character by character to remove excess whitespace.
        boolean isPreviousWhitespace = true;
        for (char ch : edgeDetailString.toCharArray()) {
            // If the character is a space.
            if (Character.isSpaceChar(ch)) {
                // Checking if the precious character was a whitespace.
                if (isPreviousWhitespace) {
                    continue;
                }
                isPreviousWhitespace = true;
            }
            else {
                isPreviousWhitespace = false;
            }
            converted.append(ch);
        }

        // Removing the last whitespace and returning the corrected string.
        return converted.toString().trim();
    }

    /**
     * Method to write the max flow and the augmented paths to a file and print to the console.
     * @param fileLocation location of the text file to be saved.
     * @param isConsoleOut Boolean to check if the output need to be printed to the console.
     * @throws IOException Output Exception when saving fails.
     */
    public void writeFile(String fileLocation, boolean isConsoleOut) throws IOException {
        // Initializing the writer.
        FileWriter myWriter = new FileWriter(fileLocation);

        // Creating the initial message.
        String initialMessage = "Max Flow: " + this.maxFlow +
                "\n\nProcess Time(ms): " + this.processTime + "\n\nAugmented Paths: ";

        // StringBuilder to create the final output.
        StringBuilder output = new StringBuilder();
        output.append(initialMessage);
        // Adding augmented paths.
        for (int[] parent: augmentedPaths) {
            int i = target;
            StringBuilder augmentedPathOutput = new StringBuilder();
            while (i != -1) {
                if (i != target) {
                    augmentedPathOutput.insert(0, (i + " -> "));
                } else {
                    augmentedPathOutput.insert(0, (i));
                }
                i = parent[i];
            }
            output.append("\n").append(augmentedPathOutput);
        }

        // Adding 'None' to the output if there is no augmented paths.
        if (augmentedPaths.size() == 0) {
            output.append("\nNone");
        }

        // Writing the output to the file.
        myWriter.write(String.valueOf(output));
        // Closing the writer.
        myWriter.close();

        // Checking whether the console output is true.
        if (isConsoleOut) {
            // Printing the error massage saying theres too many augmented paths if there is more than 10 augmented paths.
            if (augmentedPaths.size() > 10) {
                System.out.println(initialMessage);
                System.out.println("ERROR - There are too much augmented paths to show in the console,\n" +
                        "         whole result can be found at '" + fileLocation + "'.");
            } else {
                System.out.println(output);
            }
        }
    }

    /**
     * Method to add edges to the graph
     * @param start Origin node of the edge.
     * @param end Destination node of the edge.
     * @param capacity Capacity of the edge.
     */
    public void addEdge(int start, int end, int capacity) {
        this.graph[start][end] = capacity;
    }

    /**
     * Method to delete an edge from the graph.
     * @param start Origin node of the edge.
     * @param end Destination node of the edge.
     */
    public void deleteEdge(int start, int end) {
        this.graph[start][end] = 0;
    }

    /**
     * Method to search edges connected toa node.
     * @param node The node on the graph.
     * @return A 2d array containing both incoming edges (0th index) and outgoing edges(1st index).
     */
    public int[][] searchEdges(int node) {
        // [[incoming edges], [outgoing edges]]
        int[][] edges = new int[2][this.noOfVertices];

        // Adding the outgoing edges.
        edges[1] = this.graph[node];

        // Adding the incoming edges.
        for (int originNode = 0; originNode < this.noOfVertices; originNode++) {
            edges[0][originNode] = this.graph[originNode][node];
        }

        return edges;
    }

    /**
     * Method to find the Augmented paths of a given graph (Breadth First Search is used).
     * @param residualGraph The residual graph to find augmented paths on.
     * @return Returns a boolean value, 'true' if a path has been found, 'false' if no path has been found.
     */
    private boolean findAugmentedPath(int[][] residualGraph) {

        // Array to sore the path.
        int[] path = new int[this.noOfVertices];

        // Array to mark all visited nodes, all nodes are marked as not visited in the beginning(false).
        boolean[] visited = new boolean[this.noOfVertices];

        // Queue to use the breadth first search.
        LinkedList<Integer> queue = new LinkedList<>();

        // Adding source node to the queue and mark it as visited.
        queue.add(this.source);
        visited[this.source] = true;
        path[this.source] = -1;

        // Standard Breadth First Search loop
        while (queue.size() != 0) {
            int node = queue.poll();

            // Loop to find if there is an edge to all the nodes from the node selected as the start node.
            for (int destinationNode = 0; destinationNode < this.noOfVertices; destinationNode++) {
                // Checking whether the residual capacity is more than 0, from the node to the destination node.
                if (!visited[destinationNode] && residualGraph[node][destinationNode] > 0) {

                    // Connection to the target found, returning 'true'.
                    if (destinationNode == target) {
                        path[destinationNode] = node;
                        this.augmentedPaths.addLast(path);
                        return true;
                    }

                    // Adding the destination node to the list and marking it as visited.
                    queue.add(destinationNode);
                    path[destinationNode] = node;
                    visited[destinationNode] = true;
                }
            }
        }

        // When a path to the source is not found, returning 'false'.
        return false;
    }



    /**
     * Method to calculate the max flow of the graph (Ford Fulkerson algorithm is used).
     */
    public void calculateMaxFlow() {
        // Storing the process start time.
        Instant start = Instant.now();

        // Setting the initial max flow to zero.
        this.maxFlow = 0;

        // Declaring a 2d array for the residual graph.
        int[][] residualGraph = new int[this.noOfVertices][this.noOfVertices];

        // Initialising the residual graph with the capacities in the original graph.
        for (int node = 0; node < this.noOfVertices; node++)
            residualGraph[node] = graph[node].clone();


        // Finding an augmented path.
        boolean isPathFound = findAugmentedPath(residualGraph);

        // Calculating the max flow.
        while (isPathFound) {

            // Getting the last found path.
            int[] path = this.augmentedPaths.getLast();

            // Initialising the flow of the path to the maximum integer.
            int pathFlow = Integer.MAX_VALUE;
            // Calculating the flow of the path.
            for (int edge = target; edge != source; edge = path[edge]) {
                int node = path[edge];
                pathFlow = Math.min(pathFlow, residualGraph[node][edge]);
            }

            // Update the residual capacities and adding the reverse edges.
            for (int v = target; v != source; v = path[v]) {
                int u = path[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

            // Add path flow to overall flow
            this.maxFlow += pathFlow;

            // Finding another augmented path for the next iteration.
            isPathFound = findAugmentedPath(residualGraph);
        }

        // Getting the process end time.
        Instant end = Instant.now();
        // Storing the time taken to process.
        this.processTime = Duration.between(start,end).toMillis();
    }

    /**
     * Method to return thr graph as a String.
     * @return Returns a graph as a Adjacency matrix String.
     */
    @Override
    public String toString() {
        // StringBuilder to build the output.
        StringBuilder output = new StringBuilder();

        // Finding the maximum capacity in all edges.
        int maxCapacity = 0;
        for (int[] row: this.graph) {
            for (int capacity: row) {
                if (capacity > maxCapacity) {
                    maxCapacity = capacity;
                }
            }
        }

        // Calculating the whiteSpace needed.
        int whiteSpaceBuffer = Integer.max(String.valueOf(maxCapacity).length(), String.valueOf(this.noOfVertices).length());

        // Creating the header row in adjacency matrix.
        output.append(" ".repeat(whiteSpaceBuffer + 1));
        for (int node = 0; node < noOfVertices; node++) {
            output.append(" ".repeat(whiteSpaceBuffer - String.valueOf(node).length() )).append(node).append(" ");
        }
        output.append("\n").append(" ".repeat(whiteSpaceBuffer + 1));

        // Creating the header row underline in adjacency matrix.
        output.append(("-".repeat(whiteSpaceBuffer) + " ").repeat(this.noOfVertices));
        output.append("\n");


        // Creating body of the adjacency matrix.
        for (int rowNum = 0; rowNum < this.noOfVertices; rowNum++) {
            // Creating the column header in adjacency matrix.
            output.append(" ".repeat(whiteSpaceBuffer - String.valueOf(rowNum).length())).append(rowNum).append("|");
            for (int columnNum = 0; columnNum < this.noOfVertices; columnNum++) {
                // Adding the capacities to the adjacency matrix.
                int capacity = this.graph[rowNum][columnNum];
                output.append((" ".repeat(whiteSpaceBuffer - String.valueOf(capacity).length()))).append(capacity).append(" ");
            }
            output.append("\n");
        }

        // Returning the adjacency matrix String.
        return output.toString();
    }

    /**
     * Method to check whether two Graph objects are equal.
     * @param o The Graph object to check.
     * @return Return an boolean, 'true' if equal, 'false' if unequal.
     */
    @Override
    public boolean equals(Object o) {
        // Checking if both are the save object.
        if (this == o) return true;
        // Checking of both objects are Graph objects.
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph1 = (Graph) o;
        // Checking whether the graph, source, target and no of vertices are equal.
        return noOfVertices == graph1.noOfVertices && source == graph1.source && target == graph1.target && Arrays.equals(graph, graph1.graph);
    }

    /**
     * Method to crate a unique hash code for a graph object.
     * @return The unique hashcode.
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(noOfVertices, source, target);
        result = 31 * result + Arrays.hashCode(graph);
        return result;
    }

    /**
     * Getter method for the no of vertices of the Graph object.
     * @return The no of vertices of the Graph object.
     */
    public int getNoOfVertices() {
        return noOfVertices;
    }

    /**
     * Setter method for the no of vertices of the Graph object.
     * @param noOfVertices The no of vertices of the Graph object.
     */
    public void setNoOfVertices(int noOfVertices) {
        this.noOfVertices = noOfVertices;
    }

    /**
     * Getter method for the graph of the Graph object.
     * @return The graph of the Graph object.
     */
    public int[][] getGraph() {
        return graph;
    }

    /**
     * Setter method for the graph of the Graph object.
     * @param graph The graph of the Graph object.
     */
    public void setGraph(int[][] graph) {
        this.graph = graph;
    }

    /**
     * Getter method for the source of the Graph object.
     * @return The source of the Graph object.
     */
    public int getSource() {
        return source;
    }

    /**
     * Setter method for the source of the Graph object.
     * @param source The source of the Graph object.
     */
    public void setSource(int source) {
        this.source = source;
    }

    /**
     * Getter method for the target of the Graph object.
     * @return The target of the Graph object.
     */
    public int getTarget() {
        return target;
    }

    /**
     * Setter method for the target of the Graph object.
     * @param target The target of the Graph object.
     */
    public void setTarget(int target) {
        this.target = target;
    }

    /**
     * Getter method for the max flow of the Graph object.
     * @return The max flow of the Graph object.
     */
    public int getMaxFlow() {
        return maxFlow;
    }

    /**
     * Setter method for the max flow of the Graph object.
     * @param maxFlow The max flow of the Graph object.
     */
    public void setMaxFlow(int maxFlow) {
        this.maxFlow = maxFlow;
    }

    /**
     * Getter method for the augmented paths of the Graph object.
     * @return The augmented paths of the Graph object.
     */
    public LinkedList<int[]> getAugmentedPaths() {
        return augmentedPaths;
    }

    /**
     * Setter method for the augmented paths of the Graph object.
     * @param augmentedPaths The augmented paths of the Graph object.
     */
    public void setAugmentedPaths(LinkedList<int[]> augmentedPaths) {
        this.augmentedPaths = augmentedPaths;
    }

    /**
     * Getter method for the process time taken to calculate max flow of the Graph object.
     * @return The process time taken to calculate max flow of the Graph object.
     */
    public long getProcessTime() {
        return processTime;
    }

    /**
     * Setter method for the process time taken to calculate max flow of the Graph object.
     * @param processTime The process time taken to calculate max flow of the Graph object.
     */
    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }
}
