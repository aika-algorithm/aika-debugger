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
import network.aika.neuron.inhibitory.InhibitoryNeuron;
import network.aika.neuron.phase.activation.ActivationPhase;
import network.aika.neuron.phase.link.LinkPhase;
import network.aika.text.Document;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.util.Display;
import org.graphstream.util.MissingDisplayException;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static network.aika.neuron.activation.Fired.NOT_FIRED;

public class AikaVisualization implements EventListener, ViewerListener {

    private Graph activationGraph;
    private boolean hideActivationGraph = true;
    private Viewer activationGraphViewer;

    private Graph neuronGraph;
    private boolean hideNeuronGraph = true;
    private Viewer neuronGraphViewer;

    private ViewerPipe fromViewer;

    private boolean clicked;

    private Map<ActivationPhase, Consumer<Node>> actPhaseModifiers = new TreeMap<>(Comparator.comparing(p -> p.getRank()));
    private Map<LinkPhase, Consumer<Edge>> linkPhaseModifiers = new TreeMap<>(Comparator.comparing(p -> p.getRank()));
    private Map<Class<? extends Neuron>, Consumer<Node>> neuronTypeModifiers = new HashMap<>();
    private Map<Class<? extends Synapse>, BiConsumer<Edge, Synapse>> synapseTypeModifiers = new HashMap<>();


    public AikaVisualization(Document doc) throws MissingDisplayException {
        initModifiers();
        doc.addEventListener(this);
/*
        viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);

        add((DefaultView)viewer.addDefaultView(false, new SwingGraphRenderer()), BorderLayout.CENTER);
        viewer = new Viewer(graph,Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
*/
        Display display = Display.getDefault();

        activationGraph = initGraph();
        neuronGraph = initGraph();

        activationGraphViewer = display.display(activationGraph, false);
        activationGraphViewer.enableAutoLayout(new AikaLayout(doc, activationGraph));

        ViewPanel activationView = (ViewPanel) activationGraphViewer.getDefaultView();

        neuronGraphViewer = display.display(neuronGraph, false);
        neuronGraphViewer.enableAutoLayout(new AikaLayout(doc, neuronGraph));

        ViewPanel neuronView = (ViewPanel) neuronGraphViewer.getDefaultView();

        activationView.repaint();

        activationView.enableMouseOptions();

        activationView.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == 'a') {
                    hideActivationGraph = !hideActivationGraph;
                    hideGraph(hideActivationGraph, activationGraph);
                }
                if(e.getKeyChar() == 'n') {
                    hideNeuronGraph = !hideNeuronGraph;
                    hideGraph(hideNeuronGraph, neuronGraph);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        activationView.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Click");
                click();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
   //     Camera cam = view.getCamera();

        // The default action when closing the view is to quit
        // the program.
        activationGraphViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        // We connect back the viewer to the graph,
        // the graph becomes a sink for the viewer.
        // We also install us as a viewer listener to
        // intercept the graphic events.
        fromViewer = activationGraphViewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(activationGraph);
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
                    "stroke-mode: plain; stroke-width: 2px;" +
                    "text-size: 20px;" +
                "}" +
                " edge {" +
                    "size: 2px;" +
                    "shape: cubic-curve;" +
                    "z-index: 0;" +
//                  "fill-color: #222;" +
                    "arrow-size: 8px, 5px;" +
                "}");

        graph.setAttribute("ui.antialias");
        graph.setAutoCreate(true);

        /*


      //  viewer = graph.display(false);
//        viewer = new Viewer(graph,Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
//        viewer.disableAutoLayout();
        viewer.enableAutoLayout(new AikaLayout());

        //Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
       //  Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD)

        viewer.getDefaultView().enableMouseOptions();
*/

        return graph;
    }


    public Graph getActivationGraph() {
        return activationGraph;
    }

    public void setActivationGraph(Graph activationGraph) {
        this.activationGraph = activationGraph;
    }

    public Graph getNeuronGraph() {
        return neuronGraph;
    }

    public void setNeuronGraph(Graph neuronGraph) {
        this.neuronGraph = neuronGraph;
    }


    private void initModifiers() {
        actPhaseModifiers.put(ActivationPhase.INITIAL_LINKING, n -> n.setAttribute("ui.style", "stroke-color: red;"));
        actPhaseModifiers.put(ActivationPhase.PREPARE_FINAL_LINKING, n -> n.setAttribute("ui.style", "stroke-color: brown;"));
        actPhaseModifiers.put(ActivationPhase.FINAL_LINKING, n -> n.setAttribute("ui.style", "stroke-color: orange;"));
        actPhaseModifiers.put(ActivationPhase.SOFTMAX, n -> n.setAttribute("ui.style", "stroke-color: violet;"));
        actPhaseModifiers.put(ActivationPhase.COUNTING, n -> n.setAttribute("ui.style", "stroke-color: pink;"));
        actPhaseModifiers.put(ActivationPhase.SELF_GRADIENT, n -> n.setAttribute("ui.style", "stroke-color: light blue;"));
        actPhaseModifiers.put(ActivationPhase.PROPAGATE_GRADIENT, n -> n.setAttribute("ui.style", "stroke-color: blue;"));
        actPhaseModifiers.put(ActivationPhase.UPDATE_SYNAPSE_INPUT_LINKS, n -> n.setAttribute("ui.style", "stroke-color: light green;"));
        actPhaseModifiers.put(ActivationPhase.TEMPLATE_INPUT, n -> n.setAttribute("ui.style", "stroke-color: green;"));
        actPhaseModifiers.put(ActivationPhase.TEMPLATE_OUTPUT, n -> n.setAttribute("ui.style", "stroke-color: green;"));
        actPhaseModifiers.put(ActivationPhase.INDUCTION, n -> n.setAttribute("ui.style", "stroke-color: yellow;"));

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
    }

    private void pump() {
        waitForClick();

        fromViewer.pump();
        // fromViewer.blockingPump();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void click() {
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

        pump();
    }


    @Override
    public void onActivationProcessedEvent(Activation act) {
        Node n = onActivationEvent(act, null);
        n.setAttribute("aika.init-node", false);

        pump();
    }

    private Node onActivationEvent(Activation act, Activation originAct) {
        Graph g = getActivationGraph();
        String id = "" + act.getId();
        Node node = g.getNode(id);

        if (node == null) {
            node = g.addNode(id);
        }

        node.setAttribute("aika.id", act.getId());
        if(originAct != null) {
            node.setAttribute("aika.originActId", originAct.getId());
        }
        node.setAttribute("ui.label", act.getLabel());

        if(act.getNeuron().isInputNeuron() && act.getNeuron() instanceof PatternNeuron) {
            node.setAttribute("layout.frozen");
        }
        if(act.getFired() != NOT_FIRED) {
            Fired f = act.getFired();
            node.setAttribute("x", f.getInputTimestamp());
            node.setAttribute("y", 0);
//            node.setAttribute("y", f.getFired());
        }

        ActivationPhase phase = act.getPhase();
        if(phase != null) {
            Consumer<Node> actPhaseModifier = actPhaseModifiers.get(phase);
            if(actPhaseModifier != null) {
                actPhaseModifier.accept(node);
            }
            Consumer<Node> neuronTypeModifier = neuronTypeModifiers.get(act.getNeuron().getClass());
            if(neuronTypeModifier != null) {
                neuronTypeModifier.accept(node);
            }
        } else {
            node.setAttribute("ui.style", "stroke-color: gray;");
        }

        return node;
    }

    @Override
    public void onLinkProcessedEvent(Link l) {
        String inputId = "" + l.getInput().getId();
        String outputId = "" + l.getOutput().getId();
        String edgeId = inputId + "-" + outputId;
        Edge edge = activationGraph.getEdge(edgeId);
        if (edge == null) {
            edge = activationGraph.addEdge(edgeId, inputId, outputId, true);

            BiConsumer<Edge, Synapse> synapseTypeModifier = synapseTypeModifiers.get(l.getSynapse().getClass());
            if(synapseTypeModifier != null) {
                synapseTypeModifier.accept(edge, l.getSynapse());
            }
        }
        LinkPhase phase = l.getPhase();
        if(phase != null) {
            Consumer<Edge> linkPhaseModifier = linkPhaseModifiers.get(phase);
            if(linkPhaseModifier != null) {
                linkPhaseModifier.accept(edge);
            }
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


    private void hideGraph(boolean hide, Graph g) {
        if(hide) {
            g.nodes().forEach(n -> n.setAttribute("ui.hide"));
            g.edges().forEach(e -> e.setAttribute("ui.hide"));
        } else {
            g.nodes().forEach(n -> n.removeAttribute("ui.hide"));
            g.edges().forEach(e -> e.removeAttribute("ui.hide"));
        }
    }
}
