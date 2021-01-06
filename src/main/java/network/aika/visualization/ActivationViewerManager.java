package network.aika.visualization;

import network.aika.EventListener;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Fired;
import network.aika.neuron.activation.Link;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.neuron.excitatory.PatternPartNeuron;
import network.aika.neuron.excitatory.PatternPartSynapse;
import network.aika.neuron.excitatory.PatternSynapse;
import network.aika.neuron.inhibitory.InhibitoryNeuron;
import network.aika.neuron.inhibitory.InhibitorySynapse;
import network.aika.neuron.inhibitory.PrimaryInhibitorySynapse;
import network.aika.neuron.phase.activation.ActivationPhase;
import network.aika.text.Document;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.camera.Camera;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static network.aika.neuron.activation.Fired.NOT_FIRED;
import static network.aika.visualization.AikaLayout.INITIAL_DISTANCE;

public class ActivationViewerManager implements EventListener, ViewerListener {

    private Document doc;

    private Graph graph;

    private GraphManager graphManager;

    private VisitorManager visitorManager;

    private SwingViewer viewer;

    private ViewerPipe fromViewer;

    private ViewPanel graphView;

    private JSplitPane splitPane;
    private ActivationConsole console;

    private boolean clicked;

    private Node lastActEventNode;

    private Map<Class<? extends Neuron>, Consumer<Node>> neuronTypeModifiers = new HashMap<>();
    private Map<Class<? extends Synapse>, BiConsumer<Edge, Synapse>> synapseTypeModifiers = new HashMap<>();


    public ActivationViewerManager(Document doc) {
        this.doc = doc;

        initModifiers();
        doc.addEventListener(this);

        graph = initGraph();
        graphManager = new GraphManager(graph);
        visitorManager = new VisitorManager(this);

        viewer = new SwingViewer(new ThreadProxyPipe(graph));
        viewer.enableAutoLayout(new AikaLayout(this, graphManager));

        graphView = (DefaultView)viewer.addDefaultView(false, new SwingGraphRenderer());
        graphView.enableMouseOptions();

        AikaMouseManager mouseManager = new AikaMouseManager(this);
        graphView.setMouseManager(mouseManager);
        graphView.addMouseWheelListener(mouseManager);

        Camera camera = graphView.getCamera();
        camera.setAutoFitView(false);

        // The default action when closing the view is to quit
        // the program.
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        // We connect back the viewer to the graph,
        // the graph becomes a sink for the viewer.
        // We also install us as a viewer listener to
        // intercept the graphic events.
        fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);

        console = new ActivationConsole();
        splitPane = initSplitPane();
    }


    private JSplitPane initSplitPane() {
        JScrollPane paneScrollPane = new JScrollPane(console);
        paneScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        paneScrollPane.setPreferredSize(new Dimension(250, 155));
        paneScrollPane.setMinimumSize(new Dimension(10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphView, paneScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.7);

        return splitPane;
    }

    public void showElementContext(String headlinePrefix, GraphicElement ge) {
        if(ge instanceof Node) {
            Node n = (Node) ge;

            Activation act = graphManager.getActivation(n);
            if(act == null)
                return;

            console.renderActivationConsoleOutput(headlinePrefix, act, graphManager.getParticle(act));
        }
    }

    private Graph initGraph() {
        //        System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display");

        Graph graph = new SingleGraph("0");

        graph.setAttribute("ui.stylesheet",
                "node {" +
                    "size: 20px;" +
//                  "fill-color: #777;" +
//                  "text-mode: hidden;" +
                    "z-index: 1;" +
//                  "shadow-mode: gradient-radial; shadow-width: 2px; shadow-color: #999, white; shadow-offset: 3px, -3px;" +
                    "stroke-mode: plain; " +
                    "stroke-width: 2px;" +
                    "text-size: 20px;" +
                    "text-alignment: under;" +
                    "text-color: black;" +
                    "text-style: bold;" +
                    "text-background-mode: rounded-box;" +
                    "text-background-color: rgba(100, 100, 100, 100); " +
                    "text-padding: 2px;" +
                    "text-offset: 0px, 2px;" +
                "} " +
                "node:selected {" +
                    "stroke-color: red; stroke-width: 4px;" +
                "} " +
                "edge {" +
                    "size: 2px;" +
                    "shape: cubic-curve;" +
                    "z-index: 0;" +
                    "arrow-size: 8px, 5px;" +
                "} " +
                "edge:selected {" +
                    "stroke-color: red;" +
                    "stroke-width: 4px;" +
                "}"
        );

        graph.setAttribute("ui.antialias");
        graph.setAutoCreate(true);

        return graph;
    }


    public Graph getGraph() {
        return graph;
    }

    public JSplitPane getView() {
        return splitPane;
    }

    private void initModifiers() {
        neuronTypeModifiers.put(PatternNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(0,130,0);"));
        neuronTypeModifiers.put(PatternPartNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(0,205,0);"));
        neuronTypeModifiers.put(InhibitoryNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(100,100,255);"));

        synapseTypeModifiers.put(PatternPartSynapse.class, (e, s) -> {
            PatternPartSynapse pps = (PatternPartSynapse) s;
            if(pps.isRecurrent()) {
                e.setAttribute("ui.style", "fill-color: rgb(104,34,139);");
            }
            if(pps.isNegative()) {
                e.setAttribute("ui.style", "fill-color: rgb(100,0,0);");
            }
        });
        synapseTypeModifiers.put(InhibitorySynapse.class, (e, s) -> e.setAttribute("ui.style", "fill-color: rgb(50,50,150);"));
        synapseTypeModifiers.put(PrimaryInhibitorySynapse.class, (e, s) -> e.setAttribute("ui.style", "fill-color: rgb(0,00,100);"));
        synapseTypeModifiers.put(PatternSynapse.class, (e, s) -> e.setAttribute("ui.style", "fill-color: rgb(0,130,0);"));
    }

    public void pumpAndWaitForUserAction() {
        pump();

        waitForClick();
    }

    public void pump() {
        fromViewer.pump();
        // fromViewer.blockingPump();
    }

    public synchronized void click() {
        clicked = true;
        notifyAll();
    }

    private synchronized void waitForClick() {
        try {
            while(!clicked) {
                wait();
            }
            clicked = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivationCreationEvent(Activation act, Activation originAct) {
        Node n = onActivationEvent(act, originAct);

        n.setAttribute("aika.init-node", true);

        console.renderActivationConsoleOutput("New", act, graphManager.getParticle(act));

        pumpAndWaitForUserAction();
    }


    @Override
    public void onActivationProcessedEvent(Activation act) {
        Node n = onActivationEvent(act, null);
        n.setAttribute("aika.init-node", false);

        console.renderActivationConsoleOutput("Processed", act, graphManager.getParticle(act));

        pumpAndWaitForUserAction();
    }

    private Node onActivationEvent(Activation act, Activation originAct) {

        Node node = graphManager.lookupNode(act, n -> {
            if(originAct != null) {
                Edge initialEdge = graphManager.lookupEdge(originAct, act, e -> {});
                initialEdge.setAttribute("ui.style", "fill-color: rgb(200,200,200);");
            }

            if(act.getNeuron().isInputNeuron() && act.getNeuron() instanceof PatternNeuron) {
                n.setAttribute("layout.frozen");
            }
            if(act.getNeuron().isInputNeuron() && act.getFired() != NOT_FIRED) {
                Fired f = act.getFired();
                n.setAttribute("x", f.getInputTimestamp() * INITIAL_DISTANCE);
            }
        });

        node.setAttribute("aika.id", act.getId());
        if(originAct != null) {
            node.setAttribute("aika.originActId", originAct.getId());
        }
        node.setAttribute("ui.label", act.getLabel());

        if(lastActEventNode != null) {
            unhighlightNode(lastActEventNode);
        }

        highlightNode(node);

        ActivationPhase phase = act.getPhase();
        if(phase != null) {
            Consumer<Node> neuronTypeModifier = neuronTypeModifiers.get(act.getNeuron().getClass());
            if(neuronTypeModifier != null) {
                neuronTypeModifier.accept(node);
            }
        }

        lastActEventNode = node;

        return node;
    }

    public void unhighlightNode(Node node) {
        node.removeAttribute("ui.selected");
    }

    public void highlightNode(Node node) {
        node.setAttribute("ui.selected");
    }

    @Override
    public void onLinkProcessedEvent(Link l) {
        Edge edge = graphManager.lookupEdge(l, e -> {});

        BiConsumer<Edge, Synapse> synapseTypeModifier = synapseTypeModifiers.get(l.getSynapse().getClass());
        if(synapseTypeModifier != null) {
            synapseTypeModifier.accept(edge, l.getSynapse());
        }
    }

    public void viewClosed(String id) {
   //     loop = false;
    }

    public void buttonPushed(String id) {
        System.out.println("Button pushed on node "+id);
    }

    public void buttonReleased(String id) {
        System.out.println("Button released on node "+id);
    }

    public void mouseOver(String id) {
        System.out.println("Need the Mouse Options to be activated");
    }

    public void mouseLeft(String id) {
        System.out.println("Need the Mouse Options to be activated");
    }

    public Document getDocument() {
        return doc;
    }

    public VisitorManager getVisitorManager() {
        return visitorManager;
    }

    public ActivationConsole getConsole() {
        return console;
    }
}
