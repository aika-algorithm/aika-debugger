package network.aika.visualization;

import com.sun.tools.jconsole.JConsolePlugin;
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
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.util.Display;
import org.graphstream.util.MissingDisplayException;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static network.aika.neuron.activation.Fired.NOT_FIRED;

public class ActivationViewerManager implements EventListener, ViewerListener {


    // https://github.com/graphstream/gs-ui-swing/blob/master/src-test/org/graphstream/ui/viewer/test/DemoTwoGraphsInOneViewer.java

    private Document doc;

    private Graph graph;
    private SwingViewer viewer;

    private ViewerPipe fromViewer;

    private ViewPanel graphView;

    private JSplitPane splitPane;
    private JTextPane consoleTextPane;

    private boolean clicked;

    private Node lastActEventNode;

    Map<String, Activation> nodeIdToActivation = new TreeMap<>();


    private Map<ActivationPhase, Consumer<Node>> actPhaseModifiers = new TreeMap<>(Comparator.comparing(p -> p.getRank()));
    private Map<LinkPhase, Consumer<Edge>> linkPhaseModifiers = new TreeMap<>(Comparator.comparing(p -> p.getRank()));
    private Map<Class<? extends Neuron>, Consumer<Node>> neuronTypeModifiers = new HashMap<>();
    private Map<Class<? extends Synapse>, BiConsumer<Edge, Synapse>> synapseTypeModifiers = new HashMap<>();


    public ActivationViewerManager(Document doc) {
        this.doc = doc;

        initModifiers();
        doc.addEventListener(this);
/*
        viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);

        add((DefaultView)viewer.addDefaultView(false, new SwingGraphRenderer()), BorderLayout.CENTER);
        viewer = new Viewer(graph,Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
*/
        Display display = null;
        try {
            display = Display.getDefault();
        } catch (MissingDisplayException e) {
            e.printStackTrace();
        }

        graph = initGraph();

        //viewer = display.display(graph, false);
        viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);

        viewer.enableAutoLayout(new AikaLayout(doc, graph));

        //view = (ViewPanel) viewer.getDefaultView();
 //       view = (DefaultView)viewer.addDefaultView(false, new AikaRenderer());
        graphView = (DefaultView)viewer.addDefaultView(false, new SwingGraphRenderer());
        graphView.enableMouseOptions();
        graphView.setMouseManager(new AikaMouseManager(this));

        Camera camera = graphView.getCamera();
        camera.setAutoFitView(true);

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

        graphView.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mwe) {
             //   zoomGraphMouseWheelMoved(mwe, view.getCamera());
            }
        });


        graphView.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        splitPane = initSplitPane();
    }

    private JSplitPane initSplitPane() {

        //Create a text pane.
        consoleTextPane = new JTextPane();
        addStylesToDocument(consoleTextPane.getStyledDocument());

        JScrollPane paneScrollPane = new JScrollPane(consoleTextPane);
        paneScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        paneScrollPane.setPreferredSize(new Dimension(250, 155));
        paneScrollPane.setMinimumSize(new Dimension(10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphView, paneScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.7);

        return splitPane;
    }


    protected void addStylesToDocument(StyledDocument doc) {
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("headline", regular);
        StyleConstants.setFontSize(s, 16);
    }

    public void showElementContext(String headlinePrefix, GraphicElement ge) {
        if(ge instanceof Node) {
            Node n = (Node) ge;

            Activation act = nodeIdToActivation.get(n.getId());
            if(act == null)
                return;

            renderConsoleOutput(headlinePrefix, act);
        }
    }

    private void renderConsoleOutput(String headlinePrefix, Activation act) {
        StyledDocument sDoc = consoleTextPane.getStyledDocument();
        try {
            sDoc.remove(0, sDoc.getLength());

            sDoc.insertString(sDoc.getLength(), headlinePrefix + " Activation\n\n", sDoc.getStyle("headline") );

            sDoc.insertString(sDoc.getLength(), "Id: ", sDoc.getStyle("bold") );
            sDoc.insertString(sDoc.getLength(), "" + act.getId() + "\n", sDoc.getStyle("regular") );

            sDoc.insertString(sDoc.getLength(), "Label: ", sDoc.getStyle("bold") );
            sDoc.insertString(sDoc.getLength(), act.getLabel() + "\n", sDoc.getStyle("regular") );

            sDoc.insertString(sDoc.getLength(), "Phase: ", sDoc.getStyle("bold") );
            sDoc.insertString(sDoc.getLength(), act.getPhase() + "\n", sDoc.getStyle("regular") );

            sDoc.insertString(sDoc.getLength(), "Fired: ", sDoc.getStyle("bold") );
            sDoc.insertString(sDoc.getLength(), act.getFired() + "\n", sDoc.getStyle("regular") );

            sDoc.insertString(sDoc.getLength(), "Reference: ", sDoc.getStyle("bold") );
            sDoc.insertString(sDoc.getLength(), act.getReference() + "\n", sDoc.getStyle("regular") );

         //   sDoc.insertString(sDoc.getLength(), act.toString(), sDoc.getStyle("bold") );
        } catch (BadLocationException e) {
            e.printStackTrace();
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


    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public JSplitPane getView() {
        return splitPane;
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

        renderConsoleOutput("Current", act);
        pump();
    }


    @Override
    public void onActivationProcessedEvent(Activation act) {
        Node n = onActivationEvent(act, null);
        n.setAttribute("aika.init-node", false);

        pump();
    }

    private Node onActivationEvent(Activation act, Activation originAct) {
        Graph g = getGraph();
        String id = "" + act.getId();
        Node node = g.getNode(id);

        if (node == null) {
            node = g.addNode(id);
        }

        nodeIdToActivation.put(node.getId(), act);
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


        if(lastActEventNode != null) {
            lastActEventNode.setAttribute("ui.style", "stroke-color: black;");
        }

        node.setAttribute("ui.style", "stroke-color: red;");

        ActivationPhase phase = act.getPhase();
        if(phase != null) {
/*            Consumer<Node> actPhaseModifier = actPhaseModifiers.get(phase);
            if(actPhaseModifier != null) {
                actPhaseModifier.accept(node);
            }
 */
            Consumer<Node> neuronTypeModifier = neuronTypeModifiers.get(act.getNeuron().getClass());
            if(neuronTypeModifier != null) {
                neuronTypeModifier.accept(node);
            }
        }/* else {
            node.setAttribute("ui.style", "stroke-color: gray;");
        }*/

        lastActEventNode = node;

        return node;
    }

    @Override
    public void onLinkProcessedEvent(Link l) {
        String inputId = "" + l.getInput().getId();
        String outputId = "" + l.getOutput().getId();
        String edgeId = inputId + "-" + outputId;
        Edge edge = graph.getEdge(edgeId);
        if (edge == null) {
            edge = graph.addEdge(edgeId, inputId, outputId, true);

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
}
