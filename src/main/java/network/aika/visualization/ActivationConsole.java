package network.aika.visualization;

import network.aika.Utils;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Visitor;
import network.aika.neuron.phase.Phase;


import javax.swing.*;
import javax.swing.text.*;

public class ActivationConsole extends JTextPane {

    public ActivationConsole() {
        addStylesToDocument(getStyledDocument());

      //  setFocusable(false);
        setEditable(false);
    }

    private void addStylesToDocument(StyledDocument doc) {
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
        StyleConstants.setFontSize(regular, 20);

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 14);

        s = doc.addStyle("headline", regular);
        StyleConstants.setFontSize(s, 24);
    }

    public void clear() {
        StyledDocument sDoc = getStyledDocument();

        try {
            sDoc.remove(0, sDoc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

// TODO: Remove Particle!
    public void renderActivationConsoleOutput(Activation act, ActivationParticle ap) {
        StyledDocument sDoc = getStyledDocument();

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

    public void renderNeuronConsoleOutput(Neuron n) {
        StyledDocument sDoc = getStyledDocument();

        appendText("Neuron\n\n", "headline");

        appendEntry("Id: ", "" + n.getId());
        appendEntry("Label: ", n.getLabel());
        appendEntry("Is Input Neuron: ", "" + n.isInputNeuron());
        appendEntry("Is Template: ", "" + n.isTemplate());
        appendEntry("Bias: ", "" + Utils.round(n.getBias(false)));
        appendEntry("Bias (final): ", "" + Utils.round(n.getBias(true)));
        appendEntry("Frequency: ", "" + Utils.round(n.getFrequency()));
        appendEntry("N: ", "" + Utils.round(n.getSampleSpace().getN()));
        appendEntry("LastPos: ", "" + (n.getSampleSpace().getLastPos() != null ? Utils.round(n.getSampleSpace().getLastPos()) : "X"));
    }


    public void renderVisitorConsoleOutput(Visitor v, boolean dir) {
        StyledDocument sDoc = getStyledDocument();

        appendText("Visitor " + (dir ? "(up)" : "(down)") + "\n\n", "headline");

        appendEntry("Origin:", v.origin.act.getShortString());

        appendText(v.toString(),"regular");
    }

    public void renderVisitorStep(Visitor v) {
        StyledDocument sDoc = getStyledDocument();

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

    public void appendEntry(String fieldName, String fieldValue) {
        appendText(fieldName, "bold");
        appendText(fieldValue + "\n", "regular");
    }

    private void appendText(String txt, String style) {
        StyledDocument sDoc = getStyledDocument();
        try {
            sDoc.insertString(sDoc.getLength(), txt, sDoc.getStyle(style));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void addHeadline(String headline) {
        appendText(headline + "\n\n", "headline");
    }
}
