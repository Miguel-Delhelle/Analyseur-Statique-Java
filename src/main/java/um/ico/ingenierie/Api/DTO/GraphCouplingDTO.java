package um.ico.ingenierie.Api.DTO;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.CouplingGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.CallGraph;

import java.util.ArrayList;
import java.util.List;

public record GraphCouplingDTO(List<PaireClassDTO> graphCoupling) {

    public static GraphCouplingDTO from(CallGraph callGraph){

        List<PaireClass> arbreCouplageNoDTO = CouplingGraph.TreeCoupling(callGraph);
        List<PaireClassDTO> graphcouplageDTO = new ArrayList<>();
        arbreCouplageNoDTO.forEach((value) -> {
            graphcouplageDTO.add(PaireClassDTO.from(value));
        });

        return new GraphCouplingDTO(graphcouplageDTO);
    }
}
