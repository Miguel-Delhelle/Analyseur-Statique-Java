package um.ico.ingenierie.Analysis.core;

import um.ico.ingenierie.Analysis.Models.SourceCode.AbstractSourceClass;
import um.ico.ingenierie.Analysis.Models.graph.Edge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SingleFileAnalysisResult {

    AbstractSourceClass foundClass = new AbstractSourceClass();
    Map<String, Set<Edge>> foundEdges = new HashMap<>();

    public SingleFileAnalysisResult() {
    }

    public SingleFileAnalysisResult(AbstractSourceClass foundClass, Map<String, Set<Edge>> foundEdges) {
        this.foundClass = foundClass;
        this.foundEdges = foundEdges;
    }

    public Map<String, Set<Edge>> getFoundEdges() {
        return foundEdges;
    }

    public void setFoundEdges(Map<String, Set<Edge>> foundEdges) {
        this.foundEdges = foundEdges;
    }

    public AbstractSourceClass getFoundClass() {
        return foundClass;
    }

    public void setFoundClass(AbstractSourceClass foundClass) {
        this.foundClass = foundClass;
    }
}
