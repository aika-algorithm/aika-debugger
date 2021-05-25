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
package network.aika.debugger;


import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.function.Consumer;

public abstract class AbstractConsole extends JTextPane {
    public AbstractConsole() {
        addStylesToDocument(getStyledDocument());

        setDoubleBuffered(false);
    }

    public void render(Consumer<StyledDocument> content) {
        setOpaque(false);
        DefaultStyledDocument sDoc = new DefaultStyledDocument();
        addStylesToDocument(sDoc);
        clear();

        content.accept(sDoc);
        setStyledDocument(sDoc);
    }

    public void addStylesToDocument(StyledDocument doc) {
        Color green = new Color(0, 130, 0);

        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontSize(regular, 10);

        Style regularGreen = doc.addStyle("regularGreen", def);
        StyleConstants.setFontSize(regularGreen, 10);
        StyleConstants.setForeground(regularGreen, green);

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("boldGreen", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, green);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("headline", regular);
        StyleConstants.setFontSize(s, 14);
    }

    public void clear() {
        StyledDocument sDoc = getStyledDocument();
        try {
            sDoc.remove(0, sDoc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void appendEntry(StyledDocument sDoc, String fieldName, String fieldValue) {
        appendEntry(sDoc, fieldName, fieldValue, "bold", "regular");
    }

    public static void appendEntry(StyledDocument sDoc, String fieldName, String fieldValue, String titleStyle, String style) {
        appendText(sDoc, fieldName, titleStyle);
        appendText(sDoc, fieldValue + "\n", style);
    }

    protected static void appendText(StyledDocument sDoc, String txt, String style) {
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
