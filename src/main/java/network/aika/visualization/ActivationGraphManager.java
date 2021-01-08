package network.aika.visualization;

import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;


import java.util.function.Consumer;

public class ActivationGraphManager extends AbstractGraphManager<Activation, ActivationParticle> {

    public ActivationGraphManager(Graph graph) {
        super(graph);
    }

    protected long getKeyId(Activation act) {
        return act.getId();
    }

    public Edge lookupEdge(Link l, Consumer<Node> onCreate) {
        return lookupEdge(l.getInput(), l.getOutput(), onCreate);
    }

    public Edge getEdge(Link l) {
        return getEdge(l.getInput(), l.getOutput());
    }
}
