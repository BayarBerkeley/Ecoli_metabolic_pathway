package src.org.ucb.c5.synthesis;

import src.org.ucb.c5.synthesis.model.Cascade;
import src.org.ucb.c5.synthesis.model.Chemical;
import src.org.ucb.c5.synthesis.model.HyperGraph;
import src.org.ucb.c5.synthesis.model.Reaction;
import org.ucb.c5.utils.FileUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CascadePrinter {

    public void initiate() {

    }

    public void run(HyperGraph hg, Chemical achem, String outPath) {

        StringBuilder sb = new StringBuilder();
        String out = printoutCascadeRelay(achem, sb, 0, new HashSet<>(), hg.getChemicalToCascade()).toString();
        FileUtils.writeFile(out, outPath);
    }

    private StringBuilder printoutCascadeRelay(Chemical achem,
                                               StringBuilder sb,
                                               int indents,
                                               Set<Chemical> visited,
                                               HashMap<Chemical, Cascade> chemicalToCascade) {
        visited.add(achem);

        //Put in a line of id, inchi, name into the stringbuilder for this chem
        Cascade casc = chemicalToCascade.get(achem);
        sb.append(">").append(indents).append("\t");
        sb.append(casc.getProduct().getId()).append("\t");
        sb.append(casc.getProduct().getName()).append("\t");
        sb.append(casc.getProduct().getInchi()).append("\n");

        for (Reaction rxn : casc.getRxnsThatFormPdt()) {
            sb.append(rxn.getId()).append("\t");
            sb.append(handleChems(rxn.getSubstrates())).append("\t-->\t");
            sb.append(handleChems(rxn.getProducts())).append("\n");
        }

        for (Reaction rxn : casc.getRxnsThatFormPdt()) {
            for (Chemical child : rxn.getSubstrates()) {
                //If the chemical has already been visited, ignore
                if (visited.contains(child)) {
                    continue;
                }

                printoutCascadeRelay(child, sb, indents + 1, visited, chemicalToCascade);
            }
        }

        return sb;
    }

    private String handleChems(Set<Chemical> chems) {
        String out = "";
        for (Chemical achem : chems) {
            out += achem.getId();
            out += " ";
        }
        return out.trim();
    }

}
