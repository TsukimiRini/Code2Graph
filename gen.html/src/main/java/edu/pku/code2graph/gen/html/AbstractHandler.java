package edu.pku.code2graph.gen.html;

import edu.pku.code2graph.model.*;
import edu.pku.code2graph.util.GraphUtil;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class AbstractHandler {
  protected Logger logger = LoggerFactory.getLogger(DocumentHandler.class);

  protected Graph<Node, Edge> graph = GraphUtil.getGraph();
  protected Map<URI, ElementNode> uriMap = new HashMap<>();

  // temporarily save the current file path here
  protected String filePath;

  protected Stack<ElementNode> stack = new Stack<>();

  public Graph<Node, Edge> getGraph() {
    return graph;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getIdentifier(String self) {
    StringBuilder idtf = new StringBuilder();
    for (ElementNode node : stack) {
      if(!node.getName().isEmpty())
        idtf.append(URI.checkInvalidCh(node.getName())).append("/");
    }
    idtf.append(URI.checkInvalidCh(self));
    return idtf.toString();
  }

  public Map<URI, ElementNode> getUriMap() {
    return uriMap;
  }
}
