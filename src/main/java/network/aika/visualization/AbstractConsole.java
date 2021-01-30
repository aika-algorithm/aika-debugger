/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network.aika.visualization;

import network.aika.Utils;
import network.aika.neuron.Neuron;
import network.aika.neuron.Sign;
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
        appendEntry(sDoc, "Bias: ", "" + Utils.round(n.getBias(false)));
        appendEntry(sDoc, "Bias (final): ", "" + Utils.round(n.getBias(true)));
        appendEntry(sDoc, "Frequency: ", "" + Utils.round(n.getFrequency()));
        appendEntry(sDoc, "N: ", "" + Utils.round(n.getSampleSpace().getN()));
        appendEntry(sDoc, "LastPos: ", "" + (n.getSampleSpace().getLastPos() != null ? Utils.round(n.getSampleSpace().getLastPos()) : "X"));
        appendEntry(sDoc, "P(POS): ", "" + Utils.round(n.getP(Sign.POS, n.getSampleSpace().getN())));
        appendEntry(sDoc, "P(NEG): ", "" + Utils.round(n.getP(Sign.NEG, n.getSampleSpace().getN())));
        appendEntry(sDoc, "Surprisal(POS): ", "" + Utils.round(n.getSurprisal(Sign.POS)));
        appendEntry(sDoc, "Surprisal(NEG): ", "" + Utils.round(n.getSurprisal(Sign.NEG)));
    }

    public void renderSynapseConsoleOutput(StyledDocument sDoc, Synapse s) {
        appendText(sDoc, "Synapse\n\n", "headline");

        appendEntry(sDoc, "Weight: ", "" + Utils.round(s.getWeight()));
        appendEntry(sDoc, "Frequency(POS, POS): ", "" + Utils.round(s.getFrequency(Sign.POS, Sign.POS, s.getSampleSpace().getN())));
        appendEntry(sDoc, "Frequency(POS, NEG): ", "" + Utils.round(s.getFrequency(Sign.POS, Sign.NEG, s.getSampleSpace().getN())));
        appendEntry(sDoc, "Frequency(NEG, POS): ", "" + Utils.round(s.getFrequency(Sign.NEG, Sign.POS, s.getSampleSpace().getN())));
        appendEntry(sDoc, "Frequency(NEG, NEG): ", "" + Utils.round(s.getFrequency(Sign.NEG, Sign.NEG, s.getSampleSpace().getN())));
        appendEntry(sDoc, "N: ", "" + Utils.round(s.getSampleSpace().getN()));
        appendEntry(sDoc, "LastPos: ", "" + (s.getSampleSpace().getLastPos() != null ? Utils.round(s.getSampleSpace().getLastPos()) : "X"));
        appendEntry(sDoc, "P(POS, POS) :", "" + Utils.round(s.getP(Sign.POS, Sign.POS, s.getSampleSpace().getN())));
        appendEntry(sDoc, "P(POS, NEG) :", "" + Utils.round(s.getP(Sign.POS, Sign.NEG, s.getSampleSpace().getN())));
        appendEntry(sDoc, "P(NEG, POS) :", "" + Utils.round(s.getP(Sign.NEG, Sign.POS, s.getSampleSpace().getN())));
        appendEntry(sDoc, "P(NEG, NEG) :", "" + Utils.round(s.getP(Sign.NEG, Sign.NEG, s.getSampleSpace().getN())));
        appendEntry(sDoc, "Surprisal(POS, POS): ", "" + Utils.round(s.getSurprisal(Sign.POS, Sign.POS)));
        appendEntry(sDoc, "Surprisal(POS, NEG): ", "" + Utils.round(s.getSurprisal(Sign.POS, Sign.NEG)));
        appendEntry(sDoc, "Surprisal(NEG, POS): ", "" + Utils.round(s.getSurprisal(Sign.NEG, Sign.POS)));
        appendEntry(sDoc, "Surprisal(NEG, NEG): ", "" + Utils.round(s.getSurprisal(Sign.NEG, Sign.NEG)));
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
