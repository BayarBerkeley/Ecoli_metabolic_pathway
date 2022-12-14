package src.org.ucb.c5.synthesis;


import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.ucb.c5.utils.FileUtils;
import src.org.ucb.c5.synthesis.model.Chemical;
import src.org.ucb.c5.synthesis.model.EcoliExtractor;
import src.org.ucb.c5.synthesis.model.Reaction;
import java.util.*;
import java.util.List;

/**
 *
 * @author Maibayar Batulzii
 */
public class EcoliGraph {

    private List<Chemical> allChems;
    private List<Reaction> allRxns;

    private HashMap<Chemical, Integer> chemicalToShell;
    private HashMap<Reaction, Integer> reactionToShell;
    private ArrayList<Set<Vertix>> adjList;
    private Vertix root;

    public void initiate() throws Exception {
        // initiate every chemicals and E coli W reaction
        // initiate all metabolites.
        ChemExtractor ce = new ChemExtractor();
        ce.initiate();
        MetaboliteExtractor me = new MetaboliteExtractor();
        me.initiate();
        Synthesizer s = new Synthesizer();
        s.initiate();
        EcoliExtractor ecoli = new EcoliExtractor();
        ecoli.initiate();
        String ecoliW = "src/org/ucb/c5/data/E. coli W.txt";

        String chempath = "src/org/ucb/c5/data/good_chems.txt";
        List<String> metPaths = new ArrayList<String>();
        metPaths.add("src/org/ucb/c5/data/minimal_metabolites.txt");
        metPaths.add("src/org/ucb/c5/data/universal_metabolites.txt");

        //Iterate through lines, one line has one reaction after a header line

        allChems = ce.run(chempath);
        allRxns = ecoli.run(ecoliW);

        // natives returns inchis.

        Set<String> natives = me.run(metPaths);
        chemicalToShell = new HashMap<>();
        reactionToShell = new HashMap<>();
        adjList = new ArrayList<>();

        for (Chemical chem : allChems) {
            if (natives.contains(chem.getInchi())) {
                chemicalToShell.put(chem, 0);
            }
        }
    }

    public ListenableGraph<String, DefaultEdge> run(ListenableGraph<String, DefaultEdge> g, int StopShell){
        // find edge and create the vertix.
        Set<Chemical> chemicalSet = chemicalToShell.keySet();
        Reaction falseRxn = new Reaction(0L, chemicalSet,new HashSet<>());
        root = new Vertix(falseRxn, 0);
        String chemsRoot = "Native";
        Set<Vertix> subsList = new HashSet<>();
        outer:
        g.addVertex(chemsRoot); // it is used for GraphVisulization
 //       g.addVertex(root);
        outer:
        for(Reaction rxn: allRxns){
            for (Chemical achem : rxn.getSubstrates()) {
                if (!chemicalToShell.containsKey(achem)) {
                    continue outer;
                }
            }
            Vertix newvrt = new Vertix(rxn, 0);
            newvrt.setChemFrom("From Native");
            // it is substrates from the root(native);
            subsList.add(newvrt);
            String newSubs = newvrt.toStringChemicals();
            System.out.println(newSubs);
            // adding the Vertex to the graph;
            g.addVertex(newSubs);
            // creating the edge;
            g.addEdge(chemsRoot, newSubs);
        }
        // add the the adjList
        adjList.add(subsList);
        int currshell = 1;

        while(createVertixAndExpanded(currshell)){
            buildGraph(g, currshell);
            if(currshell >= StopShell){
                break;
            }
            currshell++;
        }
        return g;
    }

    /**
     * size of the adjList is curree=nt shell (cuurshell)
     * and it extends to set of Vetices at each shell
     * @param graph
     * @param currshell
     */
    public void buildGraph(ListenableGraph<String, DefaultEdge> graph, int currshell){
        // iterates the each vertex at previous shell.
        for(Vertix vert: adjList.get(currshell - 1)){
            String vertStr = vert.toStringChemicals();
            // return the chemicals name as string and add as vertex
            graph.addVertex(vertStr);
            // vertices of each vertex
            Set<Vertix> verts = getMyVertix(vert);
            for (Vertix vertix : verts) {
                String vertixStr = vertix.toStringChemicals();
                graph.addVertex(vertixStr);
                DefaultEdge newedge = new DefaultEdge(); // DefaultEdge(vert, vertix, vert.getShell())
                graph.addEdge(vertStr, vertixStr);
            }
        }
    }

    /**
     * Since each vertex is has own edges and then each edge has own vertex.
     * this method returns that vertices.
     * @param vertix
     * @return
     */
    public Set<Vertix> getMyVertix(Vertix vertix){
        Set<Vertix> itsvertix = new HashSet<>();
        Set<Chemical> prds = vertix.getRxn().getProducts();

        // iterates vertices in current shell.
        // vertex is reaction.
        for(Vertix vrt: adjList.get(vertix.getShell() + 1)){
            if(prds.size() == 0){
                break;
            }
            // checks the reaction's substrate contains the chemical
            // from the current vertex's product and add to set of vertices.
            for(Chemical chem: vrt.getRxn().getSubstrates()){
                if(prds.contains(chem)){
                    itsvertix.add(vrt);
                    vrt.setChemFrom(chem.getName());
                    break;
                }
            }
        }
        return itsvertix;
    }

    /**
     * creates a vertex and exands the shell
     * same with expandOnce except creating vertex and add to adjList
     * @param currshell
     * @return
     */

    public boolean createVertixAndExpanded(int currshell){
        boolean isExpanded = false;
        Set<Vertix> vs = new HashSet<>();
        outer:
        for (Reaction rxn: allRxns){
            //If the reaction has already been put in the expansion, skip this reaction
            if (reactionToShell.containsKey(rxn)) {
                continue;
            }
            //If any of the substates are not enabled, skip this reaction
            for (Chemical achem : rxn.getSubstrates()) {
                if (!chemicalToShell.containsKey(achem)) {
                    continue outer;
                }
            }
            isExpanded = true;
            reactionToShell.put(rxn, currshell);
            // creates vertex
            vs.add(new Vertix(rxn, currshell));
            //For each product, enable it with current shell (if it isn't already)
            for (Chemical chemid : rxn.getProducts()) {
                if (!chemicalToShell.containsKey(chemid)) {
                    chemicalToShell.put(chemid, currshell);
                }
            }
        }
        // adds the vertex to adjList
        adjList.add(vs);
        System.out.println("Expanded shell: " + currshell + " expanded " + isExpanded + " with " + chemicalToShell.size() + " reachables");
        return isExpanded;
    }

    /**
     * this class is same as Cascade (I should have used cascade)
     * but added toStringChemicals method that is used for graph.
     */


    public class Vertix {
        private final Reaction rxn;
        private String chemFrom;
        private final int shell;
        public Vertix(Reaction rxn, int shell){
            this.rxn = rxn;
            this.shell = shell;
        }
        public String toStringChemicals(){
            StringBuilder sb = new StringBuilder();
            if (rxn.getSubstrates().size() > 3){
                int countRow = 0;
                for (Chemical chem : rxn.getSubstrates()) {
                    if(countRow == 3){
                        sb.append("\n").append(chem).append(" ");
                        countRow = 0;
                    }
                    sb.append(chem.getName()).append(" ");
                    countRow++;
                }
            } else {
                for (Chemical chem : rxn.getSubstrates()) {
                    sb.append(chem.getName()).append("\n");
                }
            }
            return sb.toString();
        }

        public Reaction getRxn(){
            return rxn;
        }
        public int getShell(){
            return shell;
        }
        public void setChemFrom(String chemFrom){
            this.chemFrom = chemFrom;
        }
        public String getChemFrom(){
            return chemFrom;
        }

    }

    public static void main(String[] args) throws Exception{
        EcoliGraph ecoliPathway = new EcoliGraph();
        ecoliPathway.initiate();
        ListenableGraph<String, DefaultEdge> graph =
                new DefaultListenableGraph<String, DefaultEdge>(new DefaultDirectedGraph<>(DefaultEdge.class));;
        ecoliPathway.run(graph, 100);
 //       String start = graph.vertexSet().stream().filter(str -> str.equals("1-butanol")).findAny().get();
//        URI start = hrefGraph
//                .vertexSet().stream().filter(uri -> uri.getHost().equals("www.jgrapht.org")).findAny()
//                .get();
 //       Iterator<String> iterator = new BreadthFirstIterator<>(graph,start);
//        while(iterator.hasNext()){
//            String str = iterator.next();
//            System.out.println(str);
//        }
    }
}

