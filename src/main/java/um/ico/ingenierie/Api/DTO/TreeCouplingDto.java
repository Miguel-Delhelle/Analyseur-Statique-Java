package um.ico.ingenierie.Api.DTO;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.CouplingService;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.PaireClass;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.CallGraph;

import java.util.HashMap;
import java.util.Map;

public record TreeCouplingDto(Map<String, PaireClassDTO> arbreCouplage) {

    public static TreeCouplingDto from(CallGraph callGraph){
        Map<String,PaireClass> arbreCouplageNoDTO = CouplingService.TreeCoupling(callGraph);
        Map<String, PaireClassDTO> arbrecouplageDTO = new HashMap<>();
        arbreCouplageNoDTO.forEach((key, value) -> {
            arbrecouplageDTO.put(key,PaireClassDTO.from(value));
        });
        return new TreeCouplingDto(arbrecouplageDTO);
    }
}
