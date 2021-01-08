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

public class ActivationViewerManager extends AbstractAikaViewManager<ActivationConsole> implements EventListener {

    private Document doc;


    private VisitorManager visitorManager;


    public ActivationViewerManager(Document doc) {
        super();
        this.doc = doc;
        doc.addEventListener(this);
        visitorManager = new VisitorManager(this);
        console=new ActivationConsole();
        viewer.enableAutoLayout(new ActivationLayout(this, graphManager));
    }


    public GraphManager getGraphManager() {
        return graphManager;
    }



    public void showElementContext(String headlinePrefix, GraphicElement ge) {
        if(ge instanceof Node) {
            Node n = (Node) ge;

            Activation act = graphManager.getActivation(n);
            if(act == null)
                return;

            console.setIgnoreRepaint(true);
            console.clear();
            console.addHeadline(headlinePrefix);

            console.renderActivationConsoleOutput(act, graphManager.getParticle(act));
            console.setIgnoreRepaint(false);
            console.repaint();
        }
    }


    public void pumpAndWaitForUserAction() {
        pump();

        System.out.println("Viewport: " + graphView.getCamera().getViewCenter() + " Zoom:" + graphView.getCamera().getViewPercent());

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

        console.clear();
        console.addHeadline("New");
        console.renderActivationConsoleOutput(act, graphManager.getParticle(act));

        pumpAndWaitForUserAction();
    }


    @Override
    public void onActivationProcessedEvent(Activation act) {
        Node n = onActivationEvent(act, null);
        n.setAttribute("aika.init-node", false);

        console.clear();
        console.addHeadline("Processed");
        console.renderActivationConsoleOutput(act, graphManager.getParticle(act));

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

        Consumer<Node> neuronTypeModifier = neuronTypeModifiers.get(act.getNeuron().getClass());
        if (neuronTypeModifier != null) {
            neuronTypeModifier.accept(node);
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

    public void unhighlightEdge(Edge edge) {
        edge.removeAttribute("ui.selected");
    }

    public void highlightEdge(Edge edge) {
        edge.setAttribute("ui.selected");
    }


    @Override
    public void onLinkCreationEvent(Link l) {
        Edge e = onLinkEvent(l);

        e.setAttribute("aika.init-node", true);

//        console.renderActivationConsoleOutput("Processed", act, graphManager.getParticle(act));
    }

    @Override
    public void onLinkProcessedEvent(Link l) {
        Edge e = onLinkEvent(l);

        e.setAttribute("aika.init-node", false);

//        console.renderActivationConsoleOutput("Processed", act, graphManager.getParticle(act));
    }

    private Edge onLinkEvent(Link l) {
        Edge edge = graphManager.lookupEdge(l, e -> {});

        BiConsumer<Edge, Synapse> synapseTypeModifier = synapseTypeModifiers.get(l.getSynapse().getClass());
        if(synapseTypeModifier != null) {
            synapseTypeModifier.accept(edge, l.getSynapse());
        }
        return edge;
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
