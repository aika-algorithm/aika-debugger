package network.aika.debugger.activations;

import network.aika.debugger.AbstractConsole;
import network.aika.neuron.activation.Visitor;

import javax.swing.text.StyledDocument;

public class VisitorConsole extends AbstractConsole {

    public void renderVisitorConsoleOutput(StyledDocument sDoc, Visitor v, boolean dir) {
        appendText(sDoc, "Visitor " + (dir ? "(up)" : "(down)") + "\n\n", "headline");
        appendText(sDoc, "\n", "regular");
        appendEntry(sDoc, "Origin:", v.getOrigin().getAct().toShortString());
        appendText(sDoc, "\n", "regular");

        do {
            renderVisitorStep(sDoc, v);

            appendText(sDoc, "\n", "regular");
            v = v.getPreviousStep();
        } while(v != null);
    }

    public void renderVisitorStep(StyledDocument sDoc, Visitor v) {
        appendText(sDoc, v.getTransition().name() + "\n", "bold");

        if(v.getAct() != null) {
            appendEntry(sDoc, "Current:", v.getAct().toShortString());
        } else if(v.link != null) {
            appendEntry(sDoc, "Current:", v.link.toString());
        }

        appendEntry(sDoc, "DownUp:", "" + v.downUpDir);
        appendEntry(sDoc, "StartDir:", "" + v.startDir);

        appendEntry(sDoc, "Scopes:", v.getScopes().toString());
        appendEntry(sDoc, "DownSteps:", "" + v.downSteps);
        appendEntry(sDoc, "UpSteps:", "" + v.upSteps);
    }
}
