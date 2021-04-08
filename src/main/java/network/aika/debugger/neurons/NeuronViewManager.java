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
package network.aika.debugger.neurons;

import network.aika.Model;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.text.Document;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;

import javax.swing.*;

import static network.aika.debugger.AbstractLayout.STANDARD_DISTANCE_X;


public class NeuronViewManager extends AbstractNeuronViewManager {

    private Document document;

    public NeuronViewManager(Model m, Document document) {
        super(m);
        graphManager = new NeuronGraphManager(graph);
        this.document = document;
        mainConsole = new NeuronConsole();
        viewer.enableAutoLayout(new NeuronLayout(this, graphManager));

        splitPane = initSplitPane();
    }

    public void showElementContext(String headlinePrefix, GraphicElement ge) {
        if (ge instanceof Node) {
            Node n = (Node) ge;

            Neuron neuron = graphManager.getAikaNode(n);
            if (neuron == null)
                return;

            mainConsole.render(headlinePrefix, sDoc ->
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
        double[] x = new double[] {0.0};
        document.getActivations()
                .stream()
                .map(Activation::getNeuron)
                .filter(n -> n.isInputNeuron())
                .forEach(n -> {
                    drawNeuron(n, x[0], 0.0);
                    x[0] += STANDARD_DISTANCE_X;
                });
    }
}
