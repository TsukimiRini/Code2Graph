package edu.pku.code2graph.util;

import edu.pku.code2graph.model.Edge;
import edu.pku.code2graph.model.Node;
import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;

public class GraphUtil {
  // singleton across a graph building process (but for diff?)
  private static Graph<Node, Edge> graph;
  // FIXME use global counter as a compromise for performance,
  // BUT may skip or jump an id if adding node or edge failed (which means graph not changed)
  private static Integer nodeCount;
  private static Integer edgeCount;

  static {
    graph = initGraph();
    nodeCount = 0;
    edgeCount = 0;
  }

  /**
   * Initialize an empty Graph, return the instance
   *
   * @return
   */
  public static Graph<Node, Edge> initGraph() {
    return GraphTypeBuilder.<Node, Edge>directed()
        .allowingMultipleEdges(true) // allow multiple edges with different types
        .allowingSelfLoops(true) // allow recursion
        .edgeClass(Edge.class)
        .weighted(true)
        .buildGraph();
  }

  /**
   * Generate a unique and incremental id for node
   *
   * @return
   */
  public static Integer nid() {
    // return graph.vertexSet().size() + 1;
    return ++nodeCount;
  }

  /**
   * Generate a unique and incremental id for edge
   *
   * @return
   */
  public static Integer eid() {
    return ++edgeCount;
  }

  public static Graph<Node, Edge> getGraph() {
    return graph;
  }

  public static void clearGraph() {
    graph = initGraph();
    nodeCount = 0;
    edgeCount = 0;
  }
}
