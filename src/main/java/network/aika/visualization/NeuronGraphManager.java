package network.aika.visualization;

import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.function.Consumer;

public class NeuronGraphManager extends AbstractGraphManager<Neuron, NeuronParticle> {

    public NeuronGraphManager(Graph graph) {
        super(graph);
    }

    @Override
    protected long getKeyId(Neuron n) {
        return n.getId();
    }

    public Edge lookupEdge(Synapse s, Consumer<Node> onCreate) {
        return lookupEdge(s.getInput(), s.getOutput(), onCreate);
    }

    public Edge getEdge(Synapse s) {
        return getEdge(s.getInput(), s.getOutput());
    }
}
