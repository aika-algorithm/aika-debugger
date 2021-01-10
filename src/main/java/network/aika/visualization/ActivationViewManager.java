package network.aika.visualization;

import network.aika.EventListener;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Fired;
import network.aika.neuron.activation.Link;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.text.Document;
import network.aika.visualization.layout.ActivationGraphManager;
import network.aika.visualization.layout.ActivationLayout;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;

import javax.swing.text.DefaultStyledDocument;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static network.aika.neuron.activation.Fired.NOT_FIRED;
import static network.aika.visualization.layout.AbstractLayout.INITIAL_DISTANCE;


public class ActivationViewManager extends AbstractViewManager<ActivationConsole, ActivationGraphManager> implements EventListener {

    private Document doc;

    private VisitorManager visitorManager;

    boolean linkStepMode;

    public ActivationViewManager(Document doc) {
        super();
        graphManager = new ActivationGraphManager(graph);

        this.doc = doc;
        doc.addEventListener(this);
        visitorManager = new VisitorManager(this);
        console = new ActivationConsole();
        viewer.enableAutoLayout(new ActivationLayout(this, graphManager));

        splitPane = initSplitPane();
    }

    public void showElementContext(String headlinePrefix, GraphicElement ge) {
        if(ge instanceof Node) {
            Node n = (Node) ge;

            Activation act = graphManager.getKey(n);
            if(act == null)
                return;

            console.render(headlinePrefix, sDoc ->
                    console.renderActivationConsoleOutput(sDoc, act, graphManager.getParticle(act))
            );
        } else if(ge instanceof Edge) {
            Edge e = (Edge) ge;

        }
    }

    @Override
    public void onActivationCreationEvent(Activation act, Activation originAct) {
        Node n = onActivationEvent(act, originAct);

        n.setAttribute("aika.init-node", true);

        console.render("New", sDoc ->
                console.renderActivationConsoleOutput(sDoc, act, graphManager.getParticle(act))
        );

        pumpAndWaitForUserAction();
    }

    @Override
    public void onActivationProcessedEvent(Activation act) {
        Node n = onActivationEvent(act, null);
        n.setAttribute("aika.init-node", false);

        console.render("Processed", sDoc ->
                console.renderActivationConsoleOutput(sDoc, act, graphManager.getParticle(act))
        );

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

        highlightCurrentOnly(node);

        Consumer<Node> neuronTypeModifier = neuronTypeModifiers.get(act.getNeuron().getClass());
        if (neuronTypeModifier != null) {
            neuronTypeModifier.accept(node);
        }

        return node;
    }

    public void setLinkStepMode(boolean linkStepMode) {
        this.linkStepMode = linkStepMode;
    }

    private void highlightCurrentOnly(Element e) {
        if(lastHighlighted != e) {
            if(lastHighlighted != null) {
                unhighlightElement(lastHighlighted);
            }
            lastHighlighted = e;
            highlightElement(e);
        }
    }

    @Override
    public void onLinkCreationEvent(Link l) {
        Edge e = onLinkEvent(l);

        e.setAttribute("aika.init-node", true);

        console.render("New", sDoc ->
                console.renderLinkConsoleOutput(sDoc, l)
        );

        if(linkStepMode) {
            pumpAndWaitForUserAction();
        }
    }

    @Override
    public void onLinkProcessedEvent(Link l) {
        Edge e = onLinkEvent(l);

        e.setAttribute("aika.init-node", false);

        DefaultStyledDocument sDoc = new DefaultStyledDocument();
        console.addStylesToDocument(sDoc);
        console.clear();
        console.addHeadline(sDoc, "Processed");
        console.renderLinkConsoleOutput(sDoc, l);
        console.setStyledDocument(sDoc);

        if(linkStepMode) {
            pumpAndWaitForUserAction();
        }
    }

    private Edge onLinkEvent(Link l) {
        Edge edge = graphManager.lookupEdge(l, e -> {});

        highlightCurrentOnly(edge);

        BiConsumer<Edge, Synapse> synapseTypeModifier = synapseTypeModifiers.get(l.getSynapse().getClass());
        if(synapseTypeModifier != null) {
            synapseTypeModifier.accept(edge, l.getSynapse());
        }

        return edge;
    }

    public void viewClosed(String id) {
   //     loop = false;
    }

    public Document getDocument() {
        return doc;
    }

    public VisitorManager getVisitorManager() {
        return visitorManager;
    }
}
