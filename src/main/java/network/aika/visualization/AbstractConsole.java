package network.aika.visualization;

import network.aika.Utils;
import network.aika.neuron.Neuron;

import javax.swing.*;
import javax.swing.text.*;

public abstract class AbstractConsole extends JTextPane {
    public AbstractConsole() {
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
    public void renderNeuronConsoleOutput(Neuron n) {
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

    public void appendEntry(String fieldName, String fieldValue) {
        appendText(fieldName, "bold");
        appendText(fieldValue + "\n", "regular");
    }

    protected void appendText(String txt, String style) {
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
