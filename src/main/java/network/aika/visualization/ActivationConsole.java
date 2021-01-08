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

        appendText(sDoc, "Activation\n\n", "headline");

        appendText(sDoc, "Id: ", "bold");
        appendText(sDoc, "" + act.getId() + "\n", "regular");

        appendText(sDoc, "Label: ", "bold");
        appendText(sDoc, act.getLabel() + "\n", "regular");

        appendText(sDoc, "Phase: ", "bold");
        appendText(sDoc, Phase.toString(act.getPhase()) + "\n", "regular");

        appendText(sDoc, "Value: ", "bold");
        appendText(sDoc, Utils.round(act.getValue()) + "\n", "regular");

        appendText(sDoc, "Gradient: ", "bold");
        appendText(sDoc, Utils.round(act.getGradient()) + "\n", "regular");

        appendText(sDoc, "Fired: ", "bold");
        appendText(sDoc, act.getFired() + "\n", "regular");

        appendText(sDoc, "Reference: ", "bold");
        appendText(sDoc, act.getReference() + "\n", "regular");

/*
            if(ap != null) {
                appendText(sDoc, "X: " + ap.getPosition().x + " Y: " + ap.getPosition().y + "\n", "bold");
            }
 */
        appendText(sDoc, "\n\n\n", "regular");

        renderNeuronConsoleOutput(act.getNeuron());
    }

    public void renderNeuronConsoleOutput(Neuron n) {
        StyledDocument sDoc = getStyledDocument();

        appendText(sDoc, "Neuron\n\n", "headline");

        appendText(sDoc, "Id: ", "bold");
        appendText(sDoc, "" + n.getId() + "\n", "regular");

        appendText(sDoc, "Label: ", "bold");
        appendText(sDoc, n.getLabel() + "\n", "regular");

        appendText(sDoc, "Is Input Neuron: ", "bold");
        appendText(sDoc, n.isInputNeuron() + "\n", "regular");

        appendText(sDoc, "Is Template: ", "bold");
        appendText(sDoc, n.isTemplate() + "\n", "regular");

        appendText(sDoc, "Bias: ", "bold");
        appendText(sDoc, Utils.round(n.getBias(false)) + "\n", "regular");

        appendText(sDoc, "Bias (final): ", "bold");
        appendText(sDoc, Utils.round(n.getBias(true)) + "\n", "regular");

        appendText(sDoc, "Frequency: ", "bold");
        appendText(sDoc, Utils.round(n.getFrequency()) + "\n", "regular");

        appendText(sDoc, "N: ", "bold");
        appendText(sDoc, Utils.round(n.getSampleSpace().getN()) + "\n", "regular");

        appendText(sDoc, "LastPos: ", "bold");
        appendText(sDoc, (n.getSampleSpace().getLastPos() != null ? Utils.round(n.getSampleSpace().getLastPos()) : "X") + "\n", "regular");
    }


    public void renderVisitorConsoleOutput(Visitor v, boolean dir) {
        StyledDocument sDoc = getStyledDocument();
        try {
            sDoc.remove(0, sDoc.getLength());

            appendText(sDoc, "Visitor " + (dir ? "(up)" : "(down)") + "\n\n", "headline");

            appendText(sDoc, v.toString(),"regular");

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    private void appendText(StyledDocument sDoc, String txt, String style) {
        try {
            sDoc.insertString(sDoc.getLength(), txt, sDoc.getStyle(style));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void addHeadline(String headline) {
        appendText(getStyledDocument(), headline + "\n\n", "headline");
    }
}
