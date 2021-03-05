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


import network.aika.debugger.AbstractLayout;
import network.aika.debugger.AbstractParticle;
import network.aika.neuron.Neuron;
import network.aika.neuron.NeuronProvider;
import network.aika.neuron.Synapse;
import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;


public class NeuronParticle extends AbstractParticle {

    Neuron neuron;

    public NeuronParticle(AbstractLayout layout, String id, Neuron n, double x, double y, double z) {
        super(layout, id, x, y, z);

        neuron = n;
    }

    @Override
    protected void attraction(Vector3 delta) {
        SpringBox box = (SpringBox) this.box;
        Energies energies = box.getEnergies();

        for (EdgeSpring edge : neighbours) {
            if (!edge.ignored) {
                Synapse s = lookupSynapse(edge);

//                System.out.println("Layout synapse " + s.toString());

               // edgeAttraction(delta, edge, energies);
            }
        }
    }

    private Synapse lookupSynapse(EdgeSpring edge) {
        NeuronParticle linkedNP = (NeuronParticle) edge.getOpposite(this);

        Synapse os = neuron.getOutputSynapse(linkedNP.neuron.getProvider());
        if(os != null)
            return os;

        return neuron.getInputSynapse(linkedNP.neuron.getProvider());
    }
}
