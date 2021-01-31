package network.aika.debugger.neurons;

import network.aika.Model;
import network.aika.debugger.AbstractViewManager;
import network.aika.neuron.Neuron;
import org.graphstream.graph.Node;

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
                });
    }
}
