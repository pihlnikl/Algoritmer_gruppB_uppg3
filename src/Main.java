import java.io.*;
import java.util.*;
import javax.swing.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, FileFormatException {

        Scanner scanner = new Scanner(System.in);
        // The code reads the string written by the user, and assigns it
        // to program memory "String message = (string that was given as input)"
        System.out.println("First word: ");
        String wordA = scanner.nextLine();
        System.out.println("Second word: ");
        String wordB = scanner.nextLine();

        File selectedFile = new File("Words.txt");
        Graph g = readGraph(selectedFile);
        shortestPath shortestPath = new shortestPath();
        printPath printPath = new printPath(); // lägger kommentarer så ni vet var min kod är då den säkert behöver ändras

        try {
            shortestPath.shortestPath(g.getNodes().get(wordA));
        }
        catch (CycleFound e){
            System.out.println(e.getMessage());
        }
        printPath.printPath(g.getNodes().get(wordB)); // lägger kommentarer så ni vet var min kod är då den säkert behöver ändras

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
    int dist;
    List<Vertex> adjacentNodes;
    List<Vertex> path;

    public Vertex(String name) {
        this.name = name;
        this.dist = -1;
        this.path = new ArrayList<>();
        this.adjacentNodes = new ArrayList<>();
    }

    public void addAdjacentNode(Vertex node) {
        this.adjacentNodes.add(node);
    }

    public List<Vertex> getPath() {
        return path;
    }

    public void addToPath (Vertex vertex) {
        this.path.add(vertex);
    }

}

class shortestPath {
    public void shortestPath(Vertex s) throws CycleFound {
        Queue q;
        q = new Queue();
        Vertex v;
        q.enqueue(s);
        s.dist = 0;
        while (!q.isEmpty()) {
            v = q.dequeue();
            for (Vertex w : v.adjacentNodes) {
                if (w.dist < 0) {
                    w.dist = v.dist+1;
                    w.addToPath(v);
                    q.enqueue(w);
                }
            }
        }
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

class printPath { // lägger kommentarer så ni vet var min kod är då den säkert behöver ändras
    public void printPath(Vertex e) {
        Stack<Object> path = new Stack<>();
        List<Vertex> next = e.getPath();
        for (Vertex vertex : next) {
            path.add(vertex.name);
        }
        for (Object names : path) {
            System.out.println (names + " -> ");
        }
        System.out.println (e.name);
    }
}
