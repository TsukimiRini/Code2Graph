import edu.pku.code2graph.gen.html.HtmlParser;
import edu.pku.code2graph.gen.html.JsoupGenerator;
import edu.pku.code2graph.gen.html.SpringHandler;
import edu.pku.code2graph.gen.html.StandardDialectParser;
import edu.pku.code2graph.gen.html.model.DialectNode;
import edu.pku.code2graph.gen.html.model.NodeType;
import edu.pku.code2graph.io.GraphVizExporter;
import edu.pku.code2graph.model.Edge;
import edu.pku.code2graph.model.ElementNode;
import edu.pku.code2graph.model.Node;
import edu.pku.code2graph.model.URI;
import edu.pku.code2graph.util.GraphUtil;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.jgrapht.Graph;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringTest {
  private static List<String> filePaths = new ArrayList<>();

  @BeforeAll
  public static void setUpBeforeAll() {
    BasicConfigurator.configure();
    org.apache.log4j.Logger.getRootLogger().setLevel(Level.ERROR);

    filePaths.add("src/test/resources/simple.html");
    filePaths.add("src/test/resources/member.html");
  }

  @Test
  public void testParserWithWholeVariable() {
    StandardDialectParser parser = new StandardDialectParser();
    String code = "#{header.address.city}";
    DialectNode node = parser.parseTree(code);
    assertThat(node.getName()).isEqualTo("header.address.city");
    assertThat(node.getStartIdx()).isEqualTo(0);
    assertThat(node.getEndIdx()).isEqualTo(code.length() - 1);
  }

  @Test
  public void testParserWithPartVariable() {
    StandardDialectParser parser = new StandardDialectParser();
    String code = "book : ${ book }";
    DialectNode node = parser.parseTree(code);
    assertThat(node.getName()).isEqualTo("book");
    assertThat(node.getStartIdx()).isEqualTo(code.indexOf("$"));
    assertThat(node.getEndIdx()).isEqualTo(code.length() - 1);
  }

  @Test
  public void testHadler() {
    HtmlParser parser = new HtmlParser();
    SpringHandler hdl = new SpringHandler();
    String dom =
        "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "    <title>Admin | Projects</title>\n"
            + "</head>\n"
            + "<body>\n"
            + "    <table boder=\"1\">\n"
            + "        <tr data-th-each=\"${projects}\">\n"
            + "            <td><a data-th-href=\"@{${project.id}}\" data-th-text=\"${project.name}\">spring-framework</a></td>\n"
            + "        </tr>\n"
            + "    </table>\n"
            + "</body>\n"
            + "</html>";
    Document doc = parser.parseString(dom);
    hdl.generateFromDoc(doc);
    Graph<Node, Edge> graph = GraphUtil.getGraph();
    GraphVizExporter.printAsDot(graph);

    for (Node node : graph.vertexSet()) {
      if (node instanceof ElementNode) {
        URI uri = ((ElementNode) node).getUri();
        switch (((ElementNode) node).getName()) {
          case "tr":
            assertThat(uri.getIdentifier()).isEqualTo("#root/html/body/table/tbody/tr");
            break;
          case "projects":
            assertThat(uri.getIdentifier())
                .isEqualTo("#root/html/body/table/tbody/tr/data-th-each");
            assertThat(uri.getInline().getIdentifier()).isEqualTo("projects");
            break;
          case "${project.id}":
            assertThat(uri.getIdentifier())
                .isEqualTo("#root/html/body/table/tbody/tr/td/a/data-th-href");
            assertThat(uri.getInline().getIdentifier()).isEqualTo("${project.id}");
            break;
          case "project.id":
            assertThat(uri.getIdentifier())
                .isEqualTo("#root/html/body/table/tbody/tr/td/a/data-th-href");
            assertThat(uri.getInline().getIdentifier()).isEqualTo("${project.id}/project.id");
            break;
        }
      }
    }
  }

  @Test
  public void testGenerator() throws IOException {
    filePaths.clear();
    traversePath("/Users/tannpopo/Documents/Study/ChangeLint/code2graph-dataset/Spring/zheng");
    JsoupGenerator generator = new JsoupGenerator();
    Graph<Node, Edge> graph = generator.generateFrom().files(filePaths);
//    GraphVizExporter.printAsDot(graph);
    File outFile =
        new File(
            "/Users/tannpopo/Documents/Study/ChangeLint/code2graph-dataset/Spring/mall-html-uri.out");
    if (!outFile.exists()) {
      outFile.createNewFile();
    }
    FileWriter fw = new FileWriter(outFile);
    for (Node node : graph.vertexSet()) {
      if (node instanceof ElementNode
          && node.getType().equals(NodeType.INLINE_VAR)
          && !((ElementNode) node).getName().contains("$")) {
        System.out.println(((ElementNode) node).getName());
        fw.write(((ElementNode) node).getUri().toString());
        fw.write("\n");
      }
    }
    fw.close();
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
            if (f.getName().length() > 5 && f.getName().endsWith(".html")) {
              filePaths.add(f.getAbsolutePath());
            } else if (f.getName().length() > 4 && f.getName().endsWith(".ftl")) {
              filePaths.add(f.getAbsolutePath());
            } else if (f.getName().length() > 4 && f.getName().endsWith(".jsp")) {
              filePaths.add(f.getAbsolutePath());
            }
          }
        }
      }
    }
  }
}
