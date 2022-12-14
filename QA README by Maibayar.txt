Project 5 Option 1:

What is the minimum goal?
The minimum goal is to build a hypergraph of the Ecoli metabolic pathway.

What is the same for the initial repo (Kaleo's model)?
Generally same for kaleo's model. My work on EcoliGraph is based on the Synthesizer.java, especially,
"ExpandOnce" method abstraction was useful for the adding vertices and edges to a graph.

What library is used and why?
I might be misunderstood instruction about "using existing software or service".
But I hope that at least I am in the boundaries of the idea. I used the JGraphT libray for
my application. Reasons why I chose the library is that first,
1. Instead of building new graph algorithm, we can use a library for limited time project.
2. It is easy to find debug.
3. Using JGraphXAdapter, we can visualize the graph we built.  ListenableGraph interface is a good way
to use a graph to visualize on other software
4. Once a graph is created, you can traverse it using an ordering such as depth-first,
breadth-first, or topological. In our case, we could find the shortest path of chemical
using breadth-first search.
Work Cited:
https://jgrapht.org/guide/UserOverview

What is the structure of the graph?
    From the view point of the pathway:
Graph structure has vertices, edges and a root. Reaction has substrates and products.
If we think the native as a bucket of chemicals, it could be the root.
Since the root is not a substrate, We can separate the substrates from the root.
Therefore, all vertices from the root is the substrates. Next, those substrates turn into the products,
which will be the next nodes. But we have to be careful here because chemicals from that products
could be used for other reactions. Therefore, each chemical product will be separated as edge to the next nodes.
For example, if a reaction has three chemicals in product, vertex will have three edges.
In here, We have to find other chemicals that can react with our chemical product in the chemicalInShell,
which will be substrates to the next reaction. This process run over and over again until pathway is exhausted.
    From the view point of the graph:
I used the DefaultDirectedGraph class from JGraphT library. The structure of this graph is
that edges are directed, vertex is allowed a self-loop, multi edges are not allowed between the same vertex pair,
and no weight is for edges.

What is the structure of the code in EcoliGraph.java?
Generally, the structure is same as the Synthesizer.java, but rest of the code is for adding vertex and
adding the edges. First part of the run method, root become a first vertex, but not be added to
"adjList" instance variable. All the first substrates will be added to "adjList" at index 0 after the root is add.
Each time pathway expands, createVertixAndExpanded method creates a set of newvertex
to the adjList, which will be used for finding substrates of a new reaction in the getMyVertex method.
In the getMyVertex method, a vertex from previous shell in the adjList recruits new vertices
that contain its products. Finally, vertices are added, and edges are created in buildGraph method.

How is the code tested?
GraphVisualation.java is used for visualization purpose to see how the codes works
(and it is also cool to look). To see this, you have to have few reaction, otherwise it will take forever.
what I did is that I took out almost all minimal metabolites and universal metabolites (minimal_metabolites2.txt and
universal_metabolites2.txt) and then run the main.
Graph is kind of messy but readable. If we learn more about JGraphT and JGraphX,
we can build a great visualization.

What can we do in future?
For simplicity, all vertices are name of the substrates as String because we can see the chemicals
on the visualization. If we placed our original vertices ih graph, not just only
we could find the shortest path of the target chemical but also calculate stoichiometry of the reactions.



