import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class Main {
    public static void main(String[] args) throws IOException, FileFormatException {

        File selectedFile = new File("Words.txt");
        Graph g = readGraph(selectedFile);
        TopSort topSort = new TopSort();

        try {
            topSort.TopSort(g);
        }
        catch (CycleFound e){
            System.out.println(e.getMessage());
        }

    }

    // Read in a graph from a file, print out the adjacency list, returns the graph
    public static Graph readGraph(File selectedFile) throws IOException, FileFormatException {

        Graph g = new Graph();
        BufferedReader r = new BufferedReader(new FileReader(selectedFile));
        String line=null;

        try {
            // Read all lines
            while ((line = r.readLine()) != null) {
                if (line.trim().length() > 0) {  // Skip empty lines
                    try {
                        // Add node to graph
                        g.addNode(line);
                    } catch (Exception e) {   // Something wrong in the words file
                        r.close();
                        throw new FileFormatException("Error reading words");
                    }
                }
            }

        } catch (NullPointerException e1) {  // The input file has wrong format
            throw new FileFormatException("No words found in " + selectedFile.getName());
        }
        // Get map of nodes from Graph
        Map<String, Vertex> nodes = g.getNodes();

        // Iterate each word and add possible edges
        for (String word : nodes.keySet()) {
            g.addEdge(word);
        }

        r.close();  // Close the reader
        return g;
    }
}

@SuppressWarnings("serial")
class FileFormatException extends Exception { //Input file has the wrong format
    public FileFormatException(String message) {
        super(message);
    }
}

class Graph {
    // Save nodes/vertex as a Map, for easier access to Vertex object
    Map<String, Vertex> nodes;

    public Graph() {
        nodes = new HashMap<>();
    }
    // Create Vertex using given word, add to nodes
    public void addNode(String word) {
        nodes.put(word, new Vertex(word));
    }

    // Getter for nodes
    public Map<String, Vertex> getNodes() {
        return nodes;
    }

    // Add edges to the words
    public void addEdge(String w) {
        // Iterate the "nodes" map and compare each word to given word (parameter)
        Vertex source = nodes.get(w);
        for (String word : nodes.keySet()) {
            int diffs = 0;
            if(word != source.name) {
                // Compare each letter and count differences, break if more than 2 differences found
                for (int i = 0; i < word.length(); i++) {
                    if (word.charAt(i) != source.name.charAt(i)) {
                        diffs++;
                        if (diffs > 1) {
                            break;
                        }
                    }
                }
                // Words with exactly 1 difference are added as adjacent (each other's edges)
                if (diffs == 1) {
                    Vertex destination = nodes.get(word);
                    source.addAdjacentNode(destination);
                    destination.addAdjacentNode(source);
                    destination.addDegree();
                }
            }
        }
    }

    // Print, for troubleshooting
    public void printGraph() {
        for (String vertex : nodes.keySet()) {
            for (Vertex edges : nodes.get(vertex).adjacentNodes) {
                System.out.println (vertex + " -> " + edges.name );
            }
        }
    }
}

class Vertex {
    String name;
    int indegree;
    List<Vertex> adjacentNodes;

    public Vertex(String name) {
        this.name = name;
        this.indegree = 0;
        this.adjacentNodes = new ArrayList<>();
    }

    public void addAdjacentNode(Vertex node) {
        this.adjacentNodes.add(node);
    }

    public void addDegree() {
        this.indegree++;
    }

}

class TopSort {

    public void TopSort(Graph g) throws CycleFound {
        Map<Vertex, Integer> indegree = new HashMap<>();
        int counter = 0;
        List<Vertex> topOrder = new ArrayList<>();

        // Add indegree for each node
        for (Vertex v : g.nodes.values()) {
            indegree.put(v, v.indegree);
        }

        Queue q = new Queue();

        // Enqueue zero-indegree vertices
        for (Vertex v : g.nodes.values()) {
            if (indegree.get(v) == 0) {
                q.enqueue(v);
            }
        }

        while (!q.isEmpty()) {

            Vertex v = q.dequeue();
            topOrder.add(v);
            counter++;

            // Add indegree for each adjacent node
            for (Vertex w : v.adjacentNodes) {
                indegree.put(w, indegree.get(w) - 1);
                if (indegree.get(w) == 0) {
                    q.enqueue(w);
                }
            }
        }


        if (counter != g.nodes.size()) {
            throw new CycleFound("Cycle found in graph");
        }

        // If no cycle in graph, print topological order
        //System.out.println("Topological order: ");
        //for (Vertex v : topOrder) {
        //    System.out.println(v.name);
        //}
    }
}

// CycleFound
class CycleFound extends Exception{
    public CycleFound(String message){
        super(message);
    }
}


class Queue {

    private class Node {
        // Used to hold references to nodes for the linked queue implementation
        private Vertex info;
        private Node link;
    }

    private Node first;
    private Node last;

    //Creates an empty queue
    public Queue() {
        first = null;
        last = null;
    }

    public void enqueue(Vertex v) {
        //Adds element at the rear of the queue
        Node newNode = new Node();
        newNode.info = v;
        newNode.link = null;
        if (last == null) {
            //If we are inserting into an empty queue
            first = newNode;
        }
        else {
            last.link = newNode;
        }
        last = newNode;
    }

    public Vertex dequeue() {
        if (!isEmpty()) {
            Vertex toReturn = first.info;
            first = first.link;
            if (first == null) {
                last = null;
            }
            return toReturn;
        }
        else {
            System.out.print("Dequeue attempted on empty queue!");
            return null;
        }
    }
    public boolean isEmpty() {
        // Checks if queue is empty
        return (last == null);
    }
}
