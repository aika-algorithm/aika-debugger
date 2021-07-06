package network.aika.debugger.scopes;

import network.aika.Model;
import network.aika.debugger.AbstractViewManager;
import org.graphstream.ui.graphicGraph.GraphicElement;

import javax.swing.*;

public class ScopesViewManager extends AbstractViewManager<ScopesConsole, ScopesGraphManager> {

    private Model model;

    public ScopesViewManager(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void showElementContext(GraphicElement ge) {

    }

    @Override
    public JComponent getConsolePane() {
        return null;
    }

    @Override
    public void click(int x, int y) {

    }

    public void initScopes() {
 /*       getModel()
                .getTemplates()
                .getScopes()
                .forEach(tn -> drawNeuron(tn, tn.getTemplateInfo().getXCoord(), tn.getTemplateInfo().getYCoord()));

        getModel()
                .getTemplates()
                .getAllTemplates()
                .forEach(tn -> {
                    drawInputSynapses(tn);
                });

  */
    }
}
