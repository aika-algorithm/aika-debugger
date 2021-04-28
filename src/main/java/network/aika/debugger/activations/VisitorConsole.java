package network.aika.debugger.activations;

import network.aika.debugger.AbstractConsole;
import network.aika.neuron.activation.visitor.ActVisitor;
import network.aika.neuron.activation.visitor.LinkVisitor;
import network.aika.neuron.activation.visitor.Visitor;

import javax.swing.text.StyledDocument;

public class VisitorConsole extends AbstractConsole {

    public void renderVisitorConsoleOutput(StyledDocument sDoc, Visitor v, Boolean dir) {
        if(dir != null)
            appendText(sDoc, (dir ? "after" : "before") + "\n", "regular");

        appendText(sDoc, "\n", "regular");
        appendEntry(sDoc, "Origin:", v.getOriginAct().toShortString());
        appendText(sDoc, "\n", "regular");

        do {
            if(v instanceof ActVisitor) {
                renderActVisitorStep(sDoc, (ActVisitor) v);
            } else if(v instanceof LinkVisitor) {
                renderLinkVisitorStep(sDoc, (LinkVisitor) v);
            }

            appendText(sDoc, "\n", "regular");
            v = v.getPreviousStep();
        } while(v != null);
    }


    public void renderActVisitorStep(StyledDocument sDoc, ActVisitor v) {
        appendText(sDoc, "Activation Visitor Step\n", "bold");

        appendEntry(sDoc, "Scopes: ", v.getScopes().toString());
        appendEntry(sDoc, "Current:", v.getActivation().toShortString());

        renderVisitorStep(sDoc, v);
    }


    public void renderLinkVisitorStep(StyledDocument sDoc, LinkVisitor v) {
        appendText(sDoc, "Link Visitor Step\n", "bold");

        appendEntry(sDoc, "Transitions: ", v.getTransitions().toString());
        appendEntry(sDoc, "Current:", v.getLink().toShortString());

        renderVisitorStep(sDoc, v);
    }

    public void renderVisitorStep(StyledDocument sDoc, Visitor v) {
        appendEntry(sDoc, "DownUp:", "" + v.downUpDir);
        appendEntry(sDoc, "StartDir:", "" + v.startDir);

        appendEntry(sDoc, "DownSteps:", "" + v.downSteps);
        appendEntry(sDoc, "UpSteps:", "" + v.upSteps);
    }
}
