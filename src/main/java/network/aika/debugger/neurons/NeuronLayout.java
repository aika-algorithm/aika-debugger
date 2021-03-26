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
import network.aika.neuron.NeuronProvider;
import network.aika.debugger.AbstractLayout;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.miv.pherd.geom.Point3;

public class NeuronLayout extends AbstractLayout<NeuronGraphManager> {
    AbstractNeuronViewManager nvm;

    public NeuronLayout(AbstractNeuronViewManager nvm, NeuronGraphManager gm) {
        super(gm);
        this.nvm = nvm;

        k = STANDARD_DISTANCE_X;
        K1Init = 0.06f;
        K1Final = 0.01f;
        K2 = 0.005f;
    }

    @Override
    public NodeParticle newNodeParticle(String id) {
        Model model = nvm.getModel();
        Node n = graphManager.getNode(id);

        NeuronProvider np = model.lookupNeuron(n.getAttribute("aika.neuronId", Long.class));

        Long originNeuronId = n.getAttribute("aika.originNeuronId", Long.class);

        Double x;
        Double y;

        NeuronParticle particle;
        if(originNeuronId != null) {
            NeuronParticle originParticle = graphManager.getParticle(originNeuronId);
            Point3 originPos = originParticle.getPosition();

            x = originPos.x;
            y = originPos.y + STANDARD_DISTANCE_Y;
        } else {
            x = (Double) n.getAttribute("x");
            y = (Double) n.getAttribute("y");

            if(x == null)
                x = 0.0;

            if(y == null)
                y = 0.0;
        }

        x += (random.nextDouble() - 0.5) * 0.1;
        y += (random.nextDouble() - 0.5) * 0.1;

        particle = new NeuronParticle(this, id, np.getNeuron(), x, y, 0);

        graphManager.setParticle(np.getNeuron(), particle);

        return particle;
    }
}
