package network.aika.visualization;

import network.aika.Utils;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.Visitor;
import network.aika.neuron.phase.Phase;
import network.aika.visualization.layout.ActivationParticle;

public class ActivationConsole extends AbstractConsole {

// TODO: Remove Particle!
    public void renderActivationConsoleOutput(Activation act, ActivationParticle ap) {
        appendText("Activation\n\n", "headline");
        appendEntry("Id: ", "" + act.getId());
        appendEntry("Label: ", act.getLabel());
        appendEntry("Phase: ", Phase.toString(act.getPhase()));
        appendEntry("Value: ", "" + Utils.round(act.getValue()));
        appendEntry("Gradient: ", "" + Utils.round(act.getGradient()));
        appendEntry("Fired: ", "" + act.getFired());
        appendEntry("Reference: ", "" + act.getReference());
/*
            if(ap != null) {
                appendText(sDoc, "X: " + ap.getPosition().x + " Y: " + ap.getPosition().y + "\n", "bold");
            }
 */
        appendText("\n\n\n", "regular");

        renderNeuronConsoleOutput(act.getNeuron());
    }

    public void renderLinkConsoleOutput(Link l) {
        appendText("Link\n\n", "headline");
        appendEntry("IsSelfRef: ", "" + l.isSelfRef());
        appendEntry("InputValue: ", "" + l.getInputValue());
        appendEntry("Gradient: ", "" + l.getGradient());
/*
            if(ap != null) {
                appendText(sDoc, "X: " + ap.getPosition().x + " Y: " + ap.getPosition().y + "\n", "bold");
            }
 */
        appendText("\n\n\n", "regular");

        renderSynapseConsoleOutput(l.getSynapse());
    }

    public void renderVisitorConsoleOutput(Visitor v, boolean dir) {
        appendText("Visitor " + (dir ? "(up)" : "(down)") + "\n\n", "headline");
        appendText("\n", "regular");
        appendEntry("Origin:", v.origin.act.getShortString());
        appendText("\n", "regular");

        do {
            renderVisitorStep(v);

            appendText("\n", "regular");
            v = v.previousStep;
        } while(v != null);
    }

    public void renderVisitorStep(Visitor v) {
        appendText(v.transition.name() + "\n", "bold");

        if(v.act != null) {
            appendEntry("Current:", v.act.getShortString());
        } else if(v.link != null) {
            appendEntry("Current:", v.link.toString());
        }

        appendEntry("DownUp:", "" + v.downUpDir);
        appendEntry("StartDir:", "" + v.startDir);

        appendEntry("Scopes:", v.getScopes().toString());
        appendEntry("DownSteps:", "" + v.downSteps);
        appendEntry("UpSteps:", "" + v.upSteps);
    }

}
