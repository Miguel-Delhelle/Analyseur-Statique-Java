package fr.delweb.analyzer.Api.DTO;

import fr.delweb.analyzer.Analysis.Models.Graph.MacroGraph.Coupling.CouplingGraph;
import fr.delweb.analyzer.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;
import fr.delweb.analyzer.Analysis.Models.Graph.MicroGraph.CallGraph;

import java.util.ArrayList;
import java.util.List;

public record CouplingGraphDTO(List<PaireClassDTO> graphCoupling) {

    public static CouplingGraphDTO from(CallGraph callGraph){

        List<PaireClass> arbreCouplageNoDTO = CouplingGraph.CouplingGraph(callGraph);
        List<PaireClassDTO> graphcouplageDTO = new ArrayList<>();
        arbreCouplageNoDTO.forEach((value) -> {
            graphcouplageDTO.add(PaireClassDTO.from(value));
        });
        return new CouplingGraphDTO(graphcouplageDTO);
    }

    public static CouplingGraphDTO from(List<PaireClass> couplingGraph){

        List<PaireClassDTO> graphcouplageDTO = new ArrayList<>();
        couplingGraph.forEach((value) -> {
            graphcouplageDTO.add(PaireClassDTO.from(value));
        });
        return new CouplingGraphDTO(graphcouplageDTO);
    }
}
