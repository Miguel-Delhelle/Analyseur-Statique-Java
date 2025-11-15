package um.ico.ingenierie.Api.DTO;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.CouplingGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.CallGraph;

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
