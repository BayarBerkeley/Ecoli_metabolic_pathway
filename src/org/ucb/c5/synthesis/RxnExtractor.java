package src.org.ucb.c5.synthesis;

import org.ucb.c5.utils.FileUtils;
import src.org.ucb.c5.synthesis.model.Chemical;
import src.org.ucb.c5.synthesis.model.Reaction;

import java.util.*;

public class RxnExtractor {

    public void initiate() {

    }

    public List<Reaction> run(String rxnpath, List<Chemical> allChemicals) throws Exception {
        Map<Long, Chemical> idToChem = new HashMap<>();
        List<Reaction> allReactions = new ArrayList<Reaction>();
        for (Chemical achem : allChemicals) {
            idToChem.put(achem.getId(), achem);
        }

        //Parse the Reactions from file and add to the arraylist
        String rxndata = FileUtils.readFile(rxnpath);
        rxndata = rxndata.replaceAll("\"", "");
        String[] lines = rxndata.trim().split("\\r|\\r?\\n");
        rxndata = null; //No longer need data, clear out memory

        //Iterate through lines, one line has one reaction after a header line
        for (int i = 1; i < lines.length; i++) {
            String aline = lines[i];
            try {
                //Pull out the data for one reaction
                String[] tabs = aline.trim().split("\t");
                if (tabs.length < 4){
                    continue;
                }
                Long id = Long.parseLong(tabs[0]);
                String substrates = tabs[2];
                String products = tabs[3];
                Set<Chemical> subs = handleChemIdList(substrates, idToChem);
                Set<Chemical> pdts = handleChemIdList(products, idToChem);
                //Instantiate the reaction, then add it
                Reaction rxn = new Reaction(id, subs, pdts);
                allReactions.add(rxn);
            } catch (Exception err) {
                throw err;
            }
        }
        System.out.println("done populating reactions");
        return allReactions;
    }
    // Helper function for string prds and subs to Chemical class.
    private Set<Chemical> handleChemIdList(String chemidstring, Map<Long, Chemical> idToChem) {
        Set<Chemical> out = new HashSet<>();
        String[] stringids = chemidstring.trim().split("\\s");
        for (int i = 0; i < stringids.length; i++) {
            Long chemId = Long.parseLong(stringids[i]);
            Chemical achem = idToChem.get(chemId);
            out.add(achem);
        }
        return out;
    }

}
