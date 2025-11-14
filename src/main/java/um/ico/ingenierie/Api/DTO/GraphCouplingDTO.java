package um.ico.ingenierie.Api.DTO;

import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.CouplingGraph;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Coupling.PaireClass;
import um.ico.ingenierie.Analysis.Models.Graph.MacroGraph.Dendro.Dendrogramme;
import um.ico.ingenierie.Analysis.Models.Graph.MicroGraph.CallGraph;
import um.ico.ingenierie.Common.utils.IcoUtils;

import java.util.ArrayList;
import java.util.List;

public record GraphCouplingDTO(List<PaireClassDTO> graphCoupling) {

    public static GraphCouplingDTO from(CallGraph callGraph){

        List<PaireClass> arbreCouplageNoDTO = CouplingGraph.CouplingGraph(callGraph);

        //System.out.println(IcoUtils.getAllClass_From(arbreCouplageNoDTO,true));
        List<PaireClassDTO> graphcouplageDTO = new ArrayList<>();
        arbreCouplageNoDTO.forEach((value) -> {
            graphcouplageDTO.add(PaireClassDTO.from(value));
        });
        //System.out.println(graphcouplageDTO);
        System.out.println(Dendrogramme.constructDendo(arbreCouplageNoDTO));


        return new GraphCouplingDTO(graphcouplageDTO);
    }
}
