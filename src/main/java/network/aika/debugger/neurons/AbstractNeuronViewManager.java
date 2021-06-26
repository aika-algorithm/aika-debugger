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
import network.aika.debugger.AbstractViewManager;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractNeuronViewManager extends AbstractViewManager<NeuronConsole, NeuronGraphManager> {

    private Model model;

    public AbstractNeuronViewManager(Model model) {
        super();
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public abstract void initGraphNeurons();

    protected void drawNeuron(Neuron<?> n, double x, double y) {
        graphManager.lookupNode(n,
                node -> {
                    drawNeuron(n, x, y, node);
                });
    }

    protected void drawNeuron(Neuron<?> n, double x, double y, Node node) {
        node.setAttribute("aika.neuronId", n.getId());
        Consumer<Node> neuronTypeModifier = neuronTypeModifiers.get(n.getClass());
        if (neuronTypeModifier != null) {
            neuronTypeModifier.accept(node);
        }

        node.setAttribute("x", x);
        node.setAttribute("y", y);

        node.setAttribute("ui.label", n.getLabel());
    }

    protected void drawInputSynapses(Neuron<?> n) {
        n.getInputSynapses().forEach(s -> drawSynapse(s));
    }

    protected void drawOutputSynapses(Neuron<?> n) {
        n.getOutputSynapses().forEach(s -> {
//            if(!s.isRecurrent() || s.getOutput().isInputNeuron())
 //               drawNeuron(s.getOutput(), x, y + STANDARD_DISTANCE_Y);
            drawSynapse(s);
        });
    }

    protected Edge drawSynapse(Synapse s) {
        if(graphManager.getNode(s.getInput()) == null || graphManager.getNode(s.getOutput()) == null)
            return null;

        Edge edge = graphManager.lookupEdge(s, e -> {});

        BiConsumer<Edge, Synapse> synapseTypeModifier = synapseTypeModifiers.get(s.getClass());
        if(synapseTypeModifier != null) {
            synapseTypeModifier.accept(edge, s);
        }
        return edge;
    }
}
