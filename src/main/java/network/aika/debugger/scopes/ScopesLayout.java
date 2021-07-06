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

import network.aika.Model;
import network.aika.debugger.AbstractLayout;
import network.aika.debugger.neurons.AbstractNeuronViewManager;
import network.aika.debugger.neurons.NeuronGraphManager;
import network.aika.debugger.neurons.NeuronParticle;
import network.aika.neuron.NeuronProvider;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.miv.pherd.geom.Point3;

public class ScopesLayout extends AbstractLayout<ScopesGraphManager> {
    ScopesViewManager svm;

    public ScopesLayout(ScopesViewManager svm, ScopesGraphManager gm) {
        super(gm);
        this.svm = svm;

        k = STANDARD_DISTANCE_X;
        K1Init = 0.06f;
        K1Final = 0.01f;
        K2 = 0.005f;
    }

    @Override
    public NodeParticle newNodeParticle(String id) {
        Model model = svm.getModel();
        Node n = graphManager.getNode(id);

        Integer scopeId = n.getAttribute("aika.scopeId", Integer.class);

        Double x;
        Double y;

        ScopeParticle particle = ;

        x += (random.nextDouble() - 0.5) * 0.1;
        y += (random.nextDouble() - 0.5) * 0.1;

        particle = new ScopeParticle(this, id, np.getNeuron(), x, y, 0);

        graphManager.setParticle(np.getNeuron(), particle);

        return particle;
    }
}
