package edu.pku.code2graph.xll;

import edu.pku.code2graph.model.URI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rule extends ArrayList<Map<String, Object>> {
    private URIPattern left;
    private URIPattern right;

    public URIPattern getLeft() {
        if (left != null) return left;
        return left = new URIPattern(get(0));
    }

    public URIPattern getRight() {
        if (right != null) return right;
        return right = new URIPattern(get(1));
    }

    public void link(List<URI> uris) throws CloneNotSupportedException {
        for (URI leftUri: uris) {
            Map<String, String> leftCaps = getLeft().match(leftUri);
            if (leftCaps == null) continue;
            URIPattern pattern = getRight().applyCaptures(leftCaps);
            for (URI rightUri: uris) {
                Map<String, String> rightCaps = pattern.match(rightUri);
                if (rightCaps == null) continue;
                System.out.println(leftUri);
                System.out.println(rightUri);
            }
        }
    }
}
