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

import network.aika.Model;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.text.Document;
import network.aika.visualization.layout.NeuronGraphManager;
import network.aika.visualization.layout.NeuronLayout;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class NeuronViewManager extends AbstractViewManager<NeuronConsole, NeuronGraphManager> {

    private Model model;
    private Document document;

    public NeuronViewManager(Model m, Document document) {
        super();
        graphManager = new NeuronGraphManager(graph);
        this.model = m;
        this.document = document;
        console = new NeuronConsole();
        viewer.enableAutoLayout(new NeuronLayout(this, graphManager));

        splitPane = initSplitPane();
    }

    public Model getModel() {
        return model;
    }

    public void showElementContext(String headlinePrefix, GraphicElement ge) {
        if (ge instanceof Node) {
            Node n = (Node) ge;

            Neuron neuron = graphManager.getKey(n);
            if (neuron == null)
                return;

            console.render(headlinePrefix, sDoc ->
                    console.renderNeuronConsoleOutput(sDoc, neuron)
            );
        }
    }

    public void viewClosed(String id) {
        //     loop = false;
    }

    @Override
    public void click(int x, int y) {

    }

    public void initGraphNeurons() {
        Set<Neuron> neurons = document.getActivations()
                .stream()
                .map(Activation::getNeuron)
                .collect(Collectors.toSet());
        neurons.stream()
                .forEach(neuron ->
                        graphManager.lookupNode(neuron,
                                node -> {
                                    node.setAttribute("aika.neuronId", neuron.getId());
                                }));


    }
}
