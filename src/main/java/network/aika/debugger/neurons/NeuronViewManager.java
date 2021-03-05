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
import network.aika.neuron.inhibitory.InhibitoryNeuron;
import network.aika.text.Document;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class NeuronViewManager extends AbstractNeuronViewManager {

    private Document document;

    public NeuronViewManager(Model m, Document document) {
        super(m);
        graphManager = new NeuronGraphManager(graph);
        this.document = document;
        console = new NeuronConsole();
        viewer.enableAutoLayout(new NeuronLayout(this, graphManager));

        splitPane = initSplitPane();
    }

    public void showElementContext(String headlinePrefix, GraphicElement ge) {
        if (ge instanceof Node) {
            Node n = (Node) ge;

            Neuron neuron = graphManager.getAikaNode(n);
            if (neuron == null)
                return;

            console.render(headlinePrefix, sDoc ->
                    console.renderNeuronConsoleOutput(sDoc, neuron, null)
            );
        }
    }

    @Override
    public JComponent getConsolePane() {
        return console;
    }

    public void viewClosed(String id) {
        //     loop = false;
    }

    @Override
    public void click(int x, int y) {

    }

    public void initGraphNeurons() {
        TreeSet<Neuron> neurons = new TreeSet<>(
                Comparator.<Neuron, Boolean>comparing(n -> !(n.isInputNeuron() && n.getInputSynapses().count() == 0))
                        .thenComparing(n -> n instanceof InhibitoryNeuron)
                        .thenComparing(n -> n.getId())
        );

        document.getActivations()
                .stream()
                .map(Activation::getNeuron)
                .forEach(n -> neurons.add(n));

        // Inhibitory Neurons don't necessarily know their input and output synapses.
        neurons.stream()
                .forEach(n -> drawNeuron(n));

    }
}
