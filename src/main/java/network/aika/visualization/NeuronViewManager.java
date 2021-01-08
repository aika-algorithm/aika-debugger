package network.aika.visualization;

import network.aika.Model;
import network.aika.neuron.Neuron;
import network.aika.visualization.layout.NeuronGraphManager;
import network.aika.visualization.layout.NeuronLayout;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;


public class NeuronViewManager extends AbstractViewManager<NeuronConsole, NeuronGraphManager> {

    private Model model;


    public NeuronViewManager(Model m) {
        super();
        this.model = model;
        console=new NeuronConsole();
        viewer.enableAutoLayout(new NeuronLayout(this, graphManager));
    }

    public Model getModel() {
        return model;
    }

    public void showElementContext(String headlinePrefix, GraphicElement ge) {
        if(ge instanceof Node) {
            Node n = (Node) ge;

            Neuron neuron = graphManager.getActivation(n);
            if(neuron == null)
                return;

            console.setIgnoreRepaint(true);
            console.clear();
            console.addHeadline(headlinePrefix);

            console.renderNeuronConsoleOutput(neuron);
            console.setIgnoreRepaint(false);
            console.repaint();
        }
    }


    public void viewClosed(String id) {
   //     loop = false;
    }

}
