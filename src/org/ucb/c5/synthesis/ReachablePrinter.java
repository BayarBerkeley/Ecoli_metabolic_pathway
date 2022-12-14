package src.org.ucb.c5.synthesis;

import org.ucb.c5.utils.FileUtils;
import src.org.ucb.c5.synthesis.model.Chemical;
import src.org.ucb.c5.synthesis.model.HyperGraph;

import java.util.HashMap;

public class ReachablePrinter {

    public void initiate() {

    }

    public void run(HyperGraph hg, String outpath) {
        HashMap<Chemical, Integer> chemicalToShell = hg.getChemicalToShell();

        StringBuilder sb = new StringBuilder();
        sb.append("id\tname\tinchi\tshell\n");
        for (Chemical achem : chemicalToShell.keySet()) {
            Long chemId = achem.getId();
            String inchi = achem.getInchi();
            String name = achem.getName();
            sb.append(chemId).append("\t").append(name).append("\t").append(inchi).append("\t").append(chemicalToShell.get(achem)).append("\n");
        }
        FileUtils.writeFile(sb.toString(), outpath);
    }

}
