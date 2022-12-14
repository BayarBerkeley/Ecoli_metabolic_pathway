package src.org.ucb.c5.synthesis;

import org.ucb.c5.C5;
import src.org.ucb.c5.synthesis.CascadePrinter;
import src.org.ucb.c5.synthesis.MetaboliteExtractor;
import src.org.ucb.c5.synthesis.RxnExtractor;
import src.org.ucb.c5.synthesis.model.Chemical;
import src.org.ucb.c5.synthesis.model.HyperGraph;
import src.org.ucb.c5.synthesis.model.Reaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        ChemExtractor ce = new ChemExtractor();
        ce.initiate();
        RxnExtractor re = new RxnExtractor();
        re.initiate();
        MetaboliteExtractor me = new MetaboliteExtractor();
        me.initiate();
        Synthesizer s = new Synthesizer();
        s.initiate();

        String chempath = "src/org/ucb/c5/data/good_chems.txt";
        String rxnpath = "src/org/ucb/c5/data/good_reactions.txt";
        List<String> metPaths = new ArrayList<String>();
        metPaths.add("src/org/ucb/c5/data/minimal_metabolites.txt");
        metPaths.add("src/org/ucb/c5/data/universal_metabolites.txt");

        List<Chemical> allChems = ce.run(chempath);
        List<Reaction> allRxns = re.run(rxnpath, allChems);
        // natives returns inchis.
        Set<String> natives = me.run(metPaths);

        HyperGraph hg = s.run(allRxns, allChems, natives);

        ReachablePrinter rp = new ReachablePrinter();
        rp.initiate();
        rp.run(hg, "reachables.txt");

        Chemical butanol = allChems.get(5133);

        CascadePrinter cp = new CascadePrinter();
        cp.initiate();
        cp.run(hg, butanol, "butanol_cascade.txt");
    }
}
