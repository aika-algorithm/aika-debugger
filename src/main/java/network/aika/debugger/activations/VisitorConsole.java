package network.aika.debugger.activations;

import network.aika.callbacks.VisitorEvent;
import network.aika.debugger.AbstractConsole;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.visitor.ActVisitor;
import network.aika.neuron.activation.visitor.LinkVisitor;
import network.aika.neuron.activation.visitor.Visitor;

import javax.swing.text.StyledDocument;


public class VisitorConsole extends AbstractConsole {

    public void renderVisitorConsoleOutput(StyledDocument sDoc, Visitor v, VisitorEvent ve, Synapse s, boolean isCandidate) {
        if(ve != null)
            appendText(sDoc, (ve == VisitorEvent.AFTER ? "after" : "before") + "\n", "regular");

        appendText(sDoc, "\n", "regular");
        appendEntry(sDoc, "Origin:", v.getOriginAct().toShortString());
        appendText(sDoc, "\n", "regular");

        if(isCandidate) {
            renderLinkCandidateVisitorStep(sDoc, (LinkVisitor) v, s);

            appendText(sDoc, "\n", "regular");
            v = v.getPreviousStep();
        }

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

        appendEntry(sDoc, "Current:", v.getActivation().toShortString());
        appendEntry(sDoc, "Template Label:", v.getActivation().getNeuron().getTemplate().getTemplateInfo().getLabel());
        renderVisitorStep(sDoc, v, "bold", "regular");
    }


    public void renderLinkCandidateVisitorStep(StyledDocument sDoc, LinkVisitor v, Synapse syn) {
        appendText(sDoc, "Candidate Link Visitor Step\n", "boldGreen");

        ActVisitor pv = (ActVisitor) v.getPreviousStep();

        appendEntry(sDoc, "Current Synapse:", syn.toString(), "boldGreen", "regularGreen");
        appendEntry(sDoc, "Template Label:", syn.getTemplate().getTemplateInfo().getLabel(), "boldGreen", "regularGreen");

        renderVisitorStep(sDoc, v, "boldGreen", "regularGreen");
    }

    public void renderLinkVisitorStep(StyledDocument sDoc, LinkVisitor v) {
        appendText(sDoc, "Link Visitor Step\n", "bold");

        appendEntry(sDoc, "Current:", v.getLink().toShortString());

        renderVisitorStep(sDoc, v, "bold", "regular");
    }

    public void renderVisitorStep(StyledDocument sDoc, Visitor v, String titleStyle, String style) {
        appendEntry(sDoc, "CurrentDir:", "" + v.getCurrentDir(), titleStyle, style);

        appendEntry(sDoc, "DownSteps:", "" + v.getDownSteps(), titleStyle, style);
        appendEntry(sDoc, "UpSteps:", "" + v.getUpSteps(), titleStyle, style);
    }
}
