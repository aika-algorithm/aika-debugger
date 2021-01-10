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

import network.aika.text.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class AikaDebugger extends JPanel {

    JTabbedPane tabbedPane;

    Document doc;

    ActivationViewManager actViewManager;
    NeuronViewManager neuronViewManager;

    KeyManager keyManager;

    public AikaDebugger(Document doc) {
        super(new GridLayout(1, 1));

        this.doc = doc;

        tabbedPane = new JTabbedPane();
//        ImageIcon icon = createImageIcon("images/middle.gif");

        //Add the tabbed pane to this panel.
        add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        tabbedPane.setFocusCycleRoot(true);

        tabbedPane.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Click");
           //     click();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        actViewManager = new ActivationViewManager(doc);
        keyManager = new KeyManager(actViewManager);

        tabbedPane.addKeyListener(keyManager);

        addTab(0, "Activations", KeyEvent.VK_A, actViewManager.getView());
    }

    public void addTab(int tabIndex, String label, int ke, JComponent panel) {
        tabbedPane.addTab(label, null, panel,
                "Does nothing");
        tabbedPane.setMnemonicAt(tabIndex, ke);
    }

    public static void createAndShowGUI(Document doc) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        //Create and set up the window.
        JFrame frame = new JFrame("Aika Debugger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new AikaDebugger(doc), BorderLayout.CENTER);

        frame.setSize( 800, 600 );
        frame.setVisible(true);
    }
}
