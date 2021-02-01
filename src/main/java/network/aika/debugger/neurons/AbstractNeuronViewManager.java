package network.aika.debugger.neurons;

import network.aika.Model;
import network.aika.debugger.AbstractViewManager;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.inhibitory.InhibitoryNeuron;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractNeuronViewManager extends AbstractViewManager<NeuronConsole, NeuronGraphManager> {

    private Model model;

    public AbstractNeuronViewManager(Model model) {
        super();
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public abstract void initGraphNeurons();

    protected void drawNeuron(Neuron<?> n) {
        graphManager.lookupNode(n,
                node -> {
                    node.setAttribute("aika.neuronId", n.getId());
                    Consumer<Node> neuronTypeModifier = neuronTypeModifiers.get(n.getClass());
                    if (neuronTypeModifier != null) {
                        neuronTypeModifier.accept(node);
                    }

                    if(!(n instanceof InhibitoryNeuron)) {
                        n.getInputSynapses().forEach(s -> drawSynapse(s));
                    }
                    n.getOutputSynapses().forEach(s -> drawSynapse(s));
                });
    }

    protected void drawSynapse(Synapse s) {
        if(graphManager.getNode(s.getInput()) == null || graphManager.getNode(s.getOutput()) == null)
            return;

        Edge edge = graphManager.lookupEdge(s, e -> {});

        BiConsumer<Edge, Synapse> synapseTypeModifier = synapseTypeModifiers.get(s.getClass());
        if(synapseTypeModifier != null) {
            synapseTypeModifier.accept(edge, s);
        }
    }
}
