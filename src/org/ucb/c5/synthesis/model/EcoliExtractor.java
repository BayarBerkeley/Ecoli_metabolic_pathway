package src.org.ucb.c5.synthesis.model;

import chemaxon.marvin.io.formats.name.nameexport.Chem;
import org.ucb.c5.utils.FileUtils;

import java.util.*;

public class EcoliExtractor {

    public void initiate() {

    }

    public List<Reaction> run(String rxnpath) throws Exception {
        List<Reaction> chemRec = new ArrayList<>();

        String rxndata = FileUtils.readFile(rxnpath);
        String[] lines = rxndata.trim().split("\\r|\\r?\\n");

        for (int i = 1; i < lines.length; i++) {
            String aline = lines[i];
            try {
                //Pull out the data for one reaction
                String[] tabs = aline.trim().split("\\t");
                if (tabs.length < 3) {
                    continue;
                }

                String reactions = tabs[0];
                String[] subsProd = new String[2];
                if (reactions.contains("->")) {
                    subsProd = reactions.trim().split("->");
                } else if (reactions.contains("<=>")) {
                    subsProd = reactions.trim().split("<=>");
                } else if (reactions.contains("<-")) {
                    subsProd = reactions.trim().split("<-");
                } else if (reactions.contains("=")) {
                    subsProd = reactions.trim().split("=");
                }

                String[] subsName = null;
                String[] prodName = null;

                int subsSize = 1;
                int prodSize = 1;

                if(subsProd[0].contains(" + ")){
                    subsName = subsProd[0].trim().split(" + ");
                    subsSize = subsName.length;
                } else {
                    subsName = new String[]{subsProd[0]};
                }

                if(subsProd[1].contains(" + ")){
                    prodName = subsProd[1].trim().split(" + ");
                    prodSize = prodName.length;
                } else {
                    prodName = new String[]{subsProd[1]};
                }

                String[] subsId = tabs[1].trim().split("\\s");
                String[] prodId = tabs[2].trim().split("\\s");

                Set<Chemical> subChem = new HashSet<>();
                Set<Chemical> prodChem = new HashSet<>();

                for(int l = 0; l < subsSize; l++ ){
                    Chemical chemSub = new Chemical(Long.parseLong(subsId[l]),null, null, subsName[l]);
                    subChem.add(chemSub);
                }

                for(int m = 0; m < prodSize; m++ ){
                    Chemical chemProd = new Chemical(Long.parseLong(prodId[m]), null, null, prodName[m]);
                    prodChem.add(chemProd);
                }

                Reaction newRec = new Reaction((long) subsSize,subChem,prodChem);
                chemRec.add(newRec);
            } catch (Exception err) {
                throw err;
            }
        }
        System.out.println("done populating Ecoli");
        return chemRec;
    }
    public static void main(String[] args) throws Exception{
        EcoliExtractor data = new EcoliExtractor();
        data.initiate();
        String ecoliW = "src/org/ucb/c5/data/E. coli W.txt";
        data.run(ecoliW);
    }
}
