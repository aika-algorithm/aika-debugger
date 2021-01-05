package network.aika.visualization;

import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class GraphManager {

    private Graph graph;
    private Map<String, Activation> nodeIdToActivation = new TreeMap<>();
    private Map<Integer, ActivationParticle> actIdToParticle = new TreeMap<>();

    public GraphManager(Graph graph) {
        this.graph = graph;
    }

    public Activation getActivation(Node n) {
        return nodeIdToActivation.get(n.getId());
    }

    public ActivationParticle getParticle(Activation act) {
        return getParticle(act.getId());
    }

    public ActivationParticle getParticle(Integer actId) {
        return actIdToParticle.get(actId);
    }

    public void setParticle(Activation act, ActivationParticle particle) {
        actIdToParticle.put(act.getId(), particle);
    }

    public static String getNodeId(Activation act) {
        return "" + act.getId();
    }

    public static String getEdgeId(Activation iAct, Activation oAct) {
        return iAct.getId() + "-" + oAct.getId();
    }

    public Node lookupNode(Activation act, Consumer<Node> onCreate) {
        String id = getNodeId(act);
        Node node = graph.getNode(id);

        if (node == null) {
            node = graph.addNode(id);
            onCreate.accept(node);
        }

        nodeIdToActivation.put(node.getId(), act);

        return node;
    }

    public Edge lookupEdge(Link l, Consumer<Node> onCreate) {
        String edgeId = getEdgeId(l.getInput(), l.getOutput());
        Edge edge = graph.getEdge(edgeId);
        if (edge == null) {
            edge = graph.addEdge(edgeId, getNodeId(l.getInput()), getNodeId(l.getOutput()), true);
        }
        return edge;
    }


    public Edge lookupEdge(Activation iAct, Activation oAct, Consumer<Node> onCreate) {
        String edgeId = getEdgeId(iAct, oAct);
        Edge edge = graph.getEdge(edgeId);
        if (edge == null) {
            edge = graph.addEdge(edgeId, getNodeId(iAct), getNodeId(oAct), true);
        }
        return edge;
    }

    public Node getNode(String nodeId) {
        return graph.getNode(nodeId);
    }
}
