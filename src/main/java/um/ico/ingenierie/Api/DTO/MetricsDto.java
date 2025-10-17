package um.ico.ingenierie.Api.DTO;

import um.ico.ingenierie.Analysis.Models.MetricsData;

public record MetricsDto(
        int numberOfPackage,
        int numberOfClasses,
        int totalNummberOfMethods,
        int totalNumberOfLines,
        double averageMethodsPerClass,
        double averageLinesPerMethods,
        double averageAttributesPerclass
        ){

    public static MetricsDto from(MetricsData metricsData){
        return new MetricsDto(
                metricsData.getNumberOfPackage(),
                metricsData.getNumberOfClass(),
                metricsData.getTotalNumberOfMethods(),
                metricsData.getTotalNumberOfLines(),
                metricsData.getAverageNumberMethodsByClass(),
                metricsData.getAverageNumberOfLineByMethods(),
                metricsData.getAverageNumberOfAttributsByClass()
        ); //TODO Trouvez un moyen pour renvoyez les objets complexes "AbstractSourceClass"
    }
}