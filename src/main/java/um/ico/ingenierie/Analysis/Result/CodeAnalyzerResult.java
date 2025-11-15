package um.ico.ingenierie.Analysis.Result;

import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.CallGraph;
import um.ico.ingenierie.Analysis.Models.MetricsData;

public record CodeAnalyzerResult(MetricsData metricsData, CallGraph callGraph) {
}
