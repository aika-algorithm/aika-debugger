package network.aika.visualization;

import network.aika.Utils;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;

import javax.swing.*;
import javax.swing.text.*;
import java.util.function.Consumer;

public abstract class AbstractConsole extends JTextPane {
    public AbstractConsole() {
        addStylesToDocument(getStyledDocument());

        //  setFocusable(false);
        setEditable(false);
    }

    public void render(String headline, Consumer<StyledDocument> content) {
        setDoubleBuffered(true);
        setOpaque(false);
        setEnabled(false);
        DefaultStyledDocument sDoc = new DefaultStyledDocument();
        addStylesToDocument(sDoc);
        clear();
        addHeadline(sDoc, headline);

        content.accept(sDoc);
        setStyledDocument(sDoc);
        setEnabled(true);
    }

    public void addStylesToDocument(StyledDocument doc) {
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

    public void renderNeuronConsoleOutput(StyledDocument sDoc, Neuron n) {
        appendText(sDoc, "Neuron\n\n", "headline");

        appendEntry(sDoc, "Id: ", "" + n.getId());
        appendEntry(sDoc, "Label: ", n.getLabel());
        appendEntry(sDoc, "Is Input Neuron: ", "" + n.isInputNeuron());
        appendEntry(sDoc, "Is Template: ", "" + n.isTemplate());
        appendEntry(sDoc, "Bias: ", "" + Utils.round(n.getBias(false)));
        appendEntry(sDoc, "Bias (final): ", "" + Utils.round(n.getBias(true)));
        appendEntry(sDoc, "Frequency: ", "" + Utils.round(n.getFrequency()));
        appendEntry(sDoc, "N: ", "" + Utils.round(n.getSampleSpace().getN()));
        appendEntry(sDoc, "LastPos: ", "" + (n.getSampleSpace().getLastPos() != null ? Utils.round(n.getSampleSpace().getLastPos()) : "X"));
    }

    public void renderSynapseConsoleOutput(StyledDocument sDoc, Synapse s) {
        appendText(sDoc, "Synapse\n\n", "headline");

        appendEntry(sDoc, "Weight: ", "" + s.getWeight());
    }

    public void appendEntry(StyledDocument sDoc, String fieldName, String fieldValue) {
        appendText(sDoc, fieldName, "bold");
        appendText(sDoc, fieldValue + "\n", "regular");
    }

    protected void appendText(StyledDocument sDoc, String txt, String style) {
        try {
            sDoc.insertString(sDoc.getLength(), txt, sDoc.getStyle(style));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void addHeadline(StyledDocument sDoc, String headline) {
        appendText(sDoc, headline + "\n\n", "headline");
    }
}