import edu.pku.code2graph.gen.jdt.JdtGenerator;
import edu.pku.code2graph.gen.jdt.model.NodeType;
import edu.pku.code2graph.io.GraphVizExporter;
import edu.pku.code2graph.model.*;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class JdtGeneratorTest {
  private static final JdtGenerator generator = new JdtGenerator();

  private Set<Node> filterNodesByType(Graph<Node, Edge> graph, Type nodeType) {
    Set<Node> result =
        graph.vertexSet().stream()
            .filter(node -> node.getType().equals(nodeType))
            .collect(Collectors.toSet());
    return result;
  }

  List<String> filePaths = new ArrayList<>();

  @Test
  public void testMember() throws IOException {
    List<String> filePaths = new ArrayList<>();
    filePaths.add("src/test/resources/TestMember.java");
    Graph<Node, Edge> graph = generator.generateFrom().files(filePaths);
    GraphVizExporter.copyAsDot(graph);
    //    assertThat(filterNodesByType(graph, NodeType.METHOD_INVOCATION).size()).isEqualTo(4);
  }

  @Test
  public void testAnnotation() throws IOException {
    List<String> filePaths = new ArrayList<>();
    filePaths.add("src/test/resources/TestAnnotation.java");
    Graph<Node, Edge> graph = generator.generateFrom().files(filePaths);
    GraphVizExporter.printAsDot(graph);
  }

  @Test
  public void testEnum() throws IOException {
    List<String> filePaths = new ArrayList<>();
    filePaths.add("src/test/resources/TestEnum.java");
    Graph<Node, Edge> graph = generator.generateFrom().files(filePaths);
    GraphVizExporter.printAsDot(graph);
  }

  @Test
  public void testSpring() throws IOException {
    String keyword = "pid";
    traversePath("/Users/tannpopo/Documents/Study/ChangeLint/code2graph-dataset/Spring/zheng");
    filePaths.add("src/test/resources/TestSpring.java");
    Graph<Node, Edge> graph = generator.generateFrom().files(filePaths);
    for (Node node : graph.vertexSet()) {
      if (node.getType() == NodeType.VAR_DECLARATION || node.getType() == NodeType.LITERAL) {
        //        System.out.println(
        //            node instanceof ElementNode
        //                ? ((ElementNode) node).getName()
        //                : ((RelationNode) node).getSymbol());
        if (node instanceof ElementNode && ((ElementNode) node).getName().equals(keyword)
            || node instanceof RelationNode
                && ((RelationNode) node).getSymbol().equals("\"" + keyword + "\"")) {
          System.out.println(node.getSnippet());
          if (node instanceof ElementNode) System.out.println(":" + ((ElementNode) node).getUri());
        }
      }
    }
  }

  private void traversePath(String path) {
    File file = new File(path);
    LinkedList<File> list = new LinkedList<>();
    if (file.exists()) {
      if (null == file.listFiles()) {
        return;
      }
      list.addAll(Arrays.asList(file.listFiles()));
      while (!list.isEmpty()) {
        File[] files = list.removeFirst().listFiles();
        if (null == files) {
          continue;
        }
        for (File f : files) {
          if (f.isDirectory()) {
            list.add(f);
          } else {
            if (f.getName().length() > 5 && f.getName().endsWith(".java")) {
              filePaths.add(f.getAbsolutePath());
            }
          }
        }
      }
    }
  }
}
