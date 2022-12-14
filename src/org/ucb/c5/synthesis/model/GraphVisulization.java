package src.org.ucb.c5.synthesis.model;

import com.mxgraph.layout.*;
import com.mxgraph.swing.*;


import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import src.org.ucb.c5.synthesis.EcoliGraph;

import java.awt.*;
import javax.swing.*;

public class GraphVisulization extends JApplet{
    private static final long serialVersionUID = 247;
    private static final Dimension DEFAULT_SIZE = new Dimension(1000,800);
    private JGraphXAdapter<String, DefaultEdge> jgxAdapter;

    public static void main(String[] args) throws Exception {
 //       Graph<String, EcoliGraph.DefaultEdge> graph = new DefaultDirectedGraph<String, EcoliGraph.DefaultEdge>(EcoliGraph.DefaultEdge.class);
        ListenableGraph<String, DefaultEdge> graph =
                new DefaultListenableGraph<String, DefaultEdge>(new DefaultDirectedGraph<>(DefaultEdge.class));
        GraphVisulization applet = new GraphVisulization();
        EcoliGraph ecoliGraph = new EcoliGraph();
        ecoliGraph.initiate();
        ecoliGraph.run(graph, 3);
        applet.init(graph);
        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("Ecoli Metabolic Pathway");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

//        JGraphXAdapterDemo applet = new JGraphXAdapterDemo();
//        applet.init(g);
//
//        JFrame frame = new JFrame();
//        frame.getContentPane().add(applet);
//        frame.setTitle("JGraphT Adapter to JGraphX Demo");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);

    }

    public void init(ListenableGraph<String,DefaultEdge > g) {

        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(g);

        setPreferredSize(DEFAULT_SIZE);
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
        resize(DEFAULT_SIZE);
        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);

        // center the circle
        int radius = 10;
        layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);

        layout.execute(jgxAdapter.getDefaultParent());
        // that's all there is to it!...
//        jgxAdapter = new JGraphXAdapter<>(g);
//
//        setPreferredSize(DEFAULT_SIZE);
//        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
//        component.setConnectable(false);
//        component.getGraph().setAllowDanglingEdges(false);
//        getContentPane().add(component);
//        resize(DEFAULT_SIZE);
//
//        // positioning via jgraphx layouts
//        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
//        // center the circle
//        int radius = 100;
//        layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
//        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
//        layout.setRadius(radius);
//        layout.setMoveCircle(true);
//
//        layout.execute(jgxAdapter.getDefaultParent());
    }
}
