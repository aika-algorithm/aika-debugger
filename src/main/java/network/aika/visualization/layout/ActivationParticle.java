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
package network.aika.visualization.layout;

import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import network.aika.neuron.excitatory.PatternPartSynapse;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.miv.pherd.geom.Point3;

import static network.aika.visualization.layout.AbstractLayout.*;

public class ActivationParticle extends AbstractParticle {

    Activation act;
    Node node;

    public ActivationParticle(AbstractLayout layout, Node node, Activation act, String id, double x, double y, double z) {
        super(layout, id, x, y, z);

        this.act = act;
        this.node = node;
    }


    @Override
    protected void attraction(Vector3 delta) {
        Boolean initNode = node.getAttribute("aika.init-node", Boolean.class);

        double strength = initNode ? K1Init : K1Final;

        SpringBox box = (SpringBox) this.box;
        boolean is3D = box.is3D();
        Energies energies = box.getEnergies();

        for (EdgeSpring edge : neighbours) {
            if (!edge.ignored) {
                edgeAttraction(delta, edge, strength, energies);

                ActivationParticle other = (ActivationParticle) edge.getOpposite(this);

                Point3 opos = other.getPosition();
                double dx = opos.x - pos.x;
                double dy = (opos.y + INITIAL_DISTANCE) - pos.y;

                Link link = getLink(other.act, act);
                if(link != null) {
                    Synapse s = link.getSynapse();
                    if (s instanceof PatternPartSynapse) {
                        PatternPartSynapse pps = (PatternPartSynapse) s;
                        boolean isRecurrent = pps.isRecurrent() && !s.getOutput().isInputNeuron();

                        if(isRecurrent) {
                            dy = 0.0;
                        }
                    }
                }

                delta.set(dx, dy, is3D ? opos.z - pos.z : 0);

//                double len = delta.normalize();
//                double k = this.k * edge.weight;
                double factor = strength;

                delta.mult(new Vector2(factor * 0.2, factor));

                disp.add(delta);
                attE += factor;
                energies.accumulateEnergy(factor);
            }
        }
    }

    private Link getLink(Activation iAct, Activation oAct) {
        return oAct.getInputLinks()
                .filter(l -> l.getInput() == iAct)
                .findFirst()
                .orElse(null);
    }
}
