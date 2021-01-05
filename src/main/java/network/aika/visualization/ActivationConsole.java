package network.aika.visualization;

import network.aika.neuron.activation.Activation;
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

// TODO: Remove Particle!
    public void renderConsoleOutput(String headlinePrefix, Activation act, ActivationParticle ap) {
        StyledDocument sDoc = getStyledDocument();
        try {
            sDoc.remove(0, sDoc.getLength());

            appendText(sDoc, headlinePrefix + " Activation\n\n", "headline");

            appendText(sDoc, "Id: ", "bold");
            appendText(sDoc, "" + act.getId() + "\n","regular" );

            appendText(sDoc, "Label: ", "bold");
            appendText(sDoc, act.getLabel() + "\n", "regular");

            appendText(sDoc, "Phase: ", "bold");
            appendText(sDoc, Phase.toString(act.getPhase()) + "\n", "regular");

            appendText(sDoc, "Value: ", "bold");
            appendText(sDoc, act.getValue() + "\n", "regular");

            appendText(sDoc, "Fired: ", "bold");
            appendText(sDoc, act.getFired() + "\n", "regular");

            appendText(sDoc, "Reference: ", "bold");
            appendText(sDoc, act.getReference() + "\n", "regular");

            if(ap != null) {
                appendText(sDoc, "X: " + ap.getPosition().x + " Y: " + ap.getPosition().y + "\n", "bold");
            }
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
}
