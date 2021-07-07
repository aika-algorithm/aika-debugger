package network.aika.debugger.scopes;

import network.aika.Model;
import network.aika.debugger.AbstractViewManager;
import network.aika.debugger.neurons.NeuronConsole;
import network.aika.debugger.neurons.NeuronGraphManager;
import network.aika.debugger.neurons.NeuronLayout;
import network.aika.neuron.activation.scopes.Scope;
import network.aika.neuron.activation.scopes.Transition;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;

import javax.swing.*;

public class ScopesViewManager extends AbstractViewManager<ScopesConsole, ScopesGraphManager> {

    private Model model;

    private ScopesConsole console = new ScopesConsole();

    public ScopesViewManager(Model model) {
        super();
        this.model = model;

        graphManager = new ScopesGraphManager(graph);
        mainConsole = new ScopesConsole();
        viewer.enableAutoLayout(new ScopesLayout(this, graphManager));

        splitPane = initSplitPane();
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void showElementContext(GraphicElement ge) {

    }


    private Edge drawTransition(Transition t) {
        if(graphManager.getNode(t.getInput()) == null || graphManager.getNode(t.getOutput()) == null)
            return null;

        return graphManager.lookupEdge(t, e -> {});
    }

    private void drawScope(Scope s, double x, double y) {
        graphManager.lookupNode(s,
                node -> {
                    drawScope(s, x, y, node);
                });
    }

    private void drawScope(Scope s, double x, double y, Node node) {
        node.setAttribute("aika.scopeId", s.getId());

        node.setAttribute("x", x);
        node.setAttribute("y", y);

        node.setAttribute("ui.label", s.getLabel());
    }

    @Override
    public JComponent getConsolePane() {
        return console;
    }

    @Override
    public void click(int x, int y) {

    }

    public void initScopes() {
        getModel()
                .getScopes()
                .getScopes()
                .values()
                .forEach(s -> drawScope(s, s.getXCoord(), s.getYCoord()));

        getModel()
                .getScopes()
                .getTransitions()
                .forEach(t -> drawTransition(t));
    }
}
