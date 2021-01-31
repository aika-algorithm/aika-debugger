package network.aika.debugger.neurons;

import network.aika.Model;
import network.aika.debugger.AbstractViewManager;

public abstract class AbstractNeuronViewManager extends AbstractViewManager<NeuronConsole, NeuronGraphManager> {

    private Model model;

    public AbstractNeuronViewManager(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }


    public abstract void initGraphNeurons();
}
