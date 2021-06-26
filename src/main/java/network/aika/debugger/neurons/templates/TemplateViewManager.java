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
package network.aika.debugger.neurons.templates;

import network.aika.Model;
import network.aika.neuron.Neuron;
import network.aika.debugger.neurons.AbstractNeuronViewManager;
import network.aika.debugger.neurons.NeuronConsole;
import network.aika.debugger.neurons.NeuronGraphManager;
import network.aika.debugger.neurons.NeuronLayout;
import network.aika.neuron.Synapse;
import network.aika.neuron.excitatory.PatternNeuron;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;

import javax.swing.*;
import java.util.function.BiConsumer;


public class TemplateViewManager extends AbstractNeuronViewManager {

    public TemplateViewManager(Model m) {
        super(m);
        graphManager = new NeuronGraphManager(graph);

        mainConsole = new NeuronConsole();
        viewer.enableAutoLayout(new NeuronLayout(this, graphManager));

        splitPane = initSplitPane();
    }



    protected void drawNeuron(Neuron<?> n, double x, double y, Node node) {
        super.drawNeuron(n, x, y, node);

        if(n == n.getModel().getTemplates().SAME_PATTERN_TEMPLATE || n == n.getModel().getTemplates().INHIBITORY_TEMPLATE) {
            node.setAttribute("ui.style", "text-alignment: above;");
            node.setAttribute("ui.style", "text-offset: 0, -10;");
        }

    }


    protected Edge drawSynapse(Synapse s) {
        Edge tse = super.drawSynapse(s);
        tse.setAttribute("ui.label", s.getTemplateInfo().getLabel());

        if(s == s.getModel().getTemplates().NEGATIVE_SYNAPSE_TEMPLATE) {
            tse.setAttribute("ui.style", "text-offset: -30, -30;");
        }
        if(s == s.getModel().getTemplates().INHIBITORY_SYNAPSE_TEMPLATE) {
            tse.setAttribute("ui.style", "text-offset: 30, 30;");
        }

        if(s == s.getModel().getTemplates().RECURRENT_SAME_PATTERN_SYNAPSE_TEMPLATE) {
            tse.setAttribute("ui.style", "text-offset: -30, 30;");
        }
        if(s == s.getModel().getTemplates().PATTERN_SYNAPSE_TEMPLATE) {
            tse.setAttribute("ui.style", "text-offset: 30, -50;");
        }

        if(s == s.getModel().getTemplates().SAME_PATTERN_SYNAPSE_TEMPLATE) {
            tse.setAttribute("ui.style", "text-alignment: above;");
            tse.setAttribute("ui.style", "text-offset: 50, -100;");
        }
        return tse;
    }

    public void showElementContext(GraphicElement ge) {
        if (ge instanceof Node) {
            Node n = (Node) ge;

            Neuron neuron = graphManager.getAikaNode(n);
            if (neuron == null)
                return;

            mainConsole.render(sDoc ->
                    mainConsole.renderNeuronConsoleOutput(sDoc, neuron, null)
            );
        }
    }

    @Override
    public JComponent getConsolePane() {
        return mainConsole;
    }

    public void viewClosed(String id) {
        //     loop = false;
    }

    @Override
    public void click(int x, int y) {

    }

    public void initGraphNeurons() {
        getModel()
                .getTemplates()
                .getAllTemplates()
                .forEach(tn -> drawNeuron(tn, tn.getTemplateInfo().getXCoord(), tn.getTemplateInfo().getYCoord()));

        getModel()
                .getTemplates()
                .getAllTemplates()
                .forEach(tn -> {
                    drawInputSynapses(tn);
                });
    }
}
