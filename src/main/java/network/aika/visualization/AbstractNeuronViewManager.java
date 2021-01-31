package network.aika.visualization;

import network.aika.Model;
import network.aika.visualization.layout.NeuronGraphManager;

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
