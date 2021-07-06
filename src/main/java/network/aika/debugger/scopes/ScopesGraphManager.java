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
package network.aika.debugger.scopes;

import network.aika.debugger.AbstractGraphManager;
import network.aika.debugger.neurons.NeuronParticle;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.scopes.Scope;
import network.aika.neuron.activation.scopes.Transition;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.function.Consumer;

public class ScopesGraphManager extends AbstractGraphManager<Scope, Transition, ScopeParticle> {

    public ScopesGraphManager(Graph graph) {
        super(graph);
    }

    @Override
    protected Long getAikaNodeId(Scope s) {
        return (long) s.getId();
    }


    public Edge lookupEdge(Transition t, Consumer<Node> onCreate) {
        return lookupEdge(t.getInput(), t.getOutput(), onCreate);
    }

    public Edge getEdge(Transition t) {
        return getEdge(t.getInput(), t.getOutput());
    }

    @Override
    public Transition getLink(Edge e) {
        return null;
    }
}
