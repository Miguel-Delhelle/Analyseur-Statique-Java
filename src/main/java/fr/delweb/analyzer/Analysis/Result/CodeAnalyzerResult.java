package fr.delweb.analyzer.Analysis.Result;

import fr.delweb.analyzer.Analysis.Models.Graph.MicroGraph.CallGraph;
import fr.delweb.analyzer.Analysis.Models.MetricsData;

public record CodeAnalyzerResult(MetricsData metricsData, CallGraph callGraph) {
}
