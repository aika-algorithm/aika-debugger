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

import network.aika.Model;
import network.aika.debugger.scopes.ScopesViewManager;
import network.aika.text.Document;
import network.aika.debugger.activations.ActivationViewManager;
import network.aika.debugger.neurons.NeuronViewManager;
import network.aika.debugger.neurons.templates.TemplateViewManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AikaDebugger extends JPanel {

    JTabbedPane tabbedPane;

    Document doc;
    Model model;

    ActivationViewManager actViewManager;
    NeuronViewManager neuronViewManager;
    TemplateViewManager templateViewManager;
    ScopesViewManager scopesViewManager;

    KeyManager keyManager;

    final static Integer ACTIVATION_TAB_INDEX = 0;
    final static Integer NEURON_TAB_INDEX = 1;
    final static Integer TEMPLATE_TAB_INDEX = 2;
    final static Integer SCOPES_TAB_INDEX = 3;

    public AikaDebugger(Document doc,Model model) {
        super(new GridLayout(1, 1));

        this.doc = doc;
        this.model=model;

        tabbedPane = new JTabbedPane();
//        ImageIcon icon = createImageIcon("images/middle.gif");

        //Add the tabbed pane to this panel.
        add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        tabbedPane.setFocusCycleRoot(true);

        actViewManager = new ActivationViewManager(doc);
        neuronViewManager = new NeuronViewManager(model, doc);
        templateViewManager = new TemplateViewManager(model);
        scopesViewManager = new ScopesViewManager(model);


        keyManager = new KeyManager(actViewManager);

        addTab(ACTIVATION_TAB_INDEX, "Activations", KeyEvent.VK_A, actViewManager.getView());
        addTab(NEURON_TAB_INDEX, "Neurons", KeyEvent.VK_N, neuronViewManager.getView());
        addTab(TEMPLATE_TAB_INDEX, "Templates", KeyEvent.VK_N, templateViewManager.getView());
        addTab(SCOPES_TAB_INDEX, "Scopes", KeyEvent.VK_N, scopesViewManager.getView());

        tabbedPane.addKeyListener(keyManager);
        tabbedPane.addChangeListener(event-> {
            if(tabbedPane.getSelectedIndex()==NEURON_TAB_INDEX){
                neuronViewManager.initGraphNeurons();
            } else if(tabbedPane.getSelectedIndex()==TEMPLATE_TAB_INDEX){
                templateViewManager.initGraphNeurons();
            } else if(tabbedPane.getSelectedIndex()==SCOPES_TAB_INDEX){
                scopesViewManager.initScopes();
            }
        });
    }

    public void addTab(int tabIndex, String label, int ke, JComponent panel) {
        tabbedPane.addTab(label, null, panel,
                "Does nothing");
        tabbedPane.setMnemonicAt(tabIndex, ke);
    }

    public static void createAndShowGUI(Document doc, Model model) {
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
        frame.add(new AikaDebugger(doc,model), BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
}
