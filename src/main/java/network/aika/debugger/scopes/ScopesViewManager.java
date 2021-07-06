package network.aika.debugger.scopes;

import network.aika.Model;
import network.aika.debugger.AbstractViewManager;
import network.aika.neuron.activation.scopes.Scope;
import network.aika.neuron.activation.scopes.Transition;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;

import javax.swing.*;
import java.util.function.Consumer;

public class ScopesViewManager extends AbstractViewManager<ScopesConsole, ScopesGraphManager> {

    private Model model;

    private ScopesConsole console = new ScopesConsole();

    public ScopesViewManager(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void showElementContext(GraphicElement ge) {

    }


    private void drawTransition(Transition t) {

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
                .forEach(s -> drawScope(s, s.getXCoord(), s.getYCoord()));

        getModel()
                .getScopes()
                .getTransitions()
                .forEach(t -> drawTransition(t));
    }
}
