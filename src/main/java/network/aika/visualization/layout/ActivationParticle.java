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

    public static double K1 = 0.12f;
    public static double K2 = 0.03f;

    Activation act;
    Node node;

    public ActivationParticle(AbstractLayout layout, Node node, Activation act, String id, double x, double y, double z) {
        super(layout, id, x, y, z);

        this.act = act;
        this.node = node;
    }


    @Override
    protected void repulsionN2(Vector3 delta) {
//        super.repulsionN2(delta);
    }

    @Override
    protected void repulsionNLogN(Vector3 delta) {
//        super.repulsionNLogN(delta);
    }

/*
    public void moveTo(double x, double y, double z) {
        super.moveTo(x, y, z);
        System.out.println(act.getLabel() + " x:" + x + " y:" + y + " x:" + z);
    }
*/

    @Override
    protected void attraction(Vector3 delta) {
//        Boolean initNode = node.getAttribute("aika.init-node", Boolean.class);

        SpringBox box = (SpringBox) this.box;
        Energies energies = box.getEnergies();

        System.out.println(act.getLabel());

        for (EdgeSpring edge : neighbours) {
//            edgeAttraction(delta, edge, energies);

            if (!edge.ignored) {
                ActivationParticle other = (ActivationParticle) edge.getOpposite(this);

                Point3 opos = other.getPosition();

                Link link = getLink(other.act, act);

                if(link != null) {
                    Synapse s = link.getSynapse();
                    if (s instanceof PatternPartSynapse) {
                        PatternPartSynapse pps = (PatternPartSynapse) s;
                        boolean isRecurrent = pps.isRecurrent() && !s.getOutput().isInputNeuron();

                        if (isRecurrent) {
                            continue;
                        }
                    }
                }

                if(link == null)
                    continue;

                double dy = 0.0;

                if(act == link.getOutput()) {
                    dy = (opos.y + STANDARD_DISTANCE) - pos.y;
                    dy = Math.max(0.0, dy);
                } else {
                    dy = opos.y - (pos.y + STANDARD_DISTANCE);
                    dy = Math.min(0.0, dy);
                }

                System.out.println("    " + other.act.getLabel() + " dy:" + dy);

                delta.set(0.0, dy, 0.0);

                delta.mult(new Vector2(0.0, K1));

                disp.add(delta);
                attE += K1;
                energies.accumulateEnergy(K1);
            }
        }
    //    System.out.println(act.getLabel() + " : " + disp);
    }

    private Link getLink(Activation actA, Activation actB) {
        Link l = getDirectedLink(actA, actB);
        if(l != null)
            return l;
        return getDirectedLink(actB, actA);
    }

    private Link getDirectedLink(Activation iAct, Activation oAct) {
        return oAct.getInputLinks()
                .filter(l -> l.getInput() == iAct)
                .findFirst()
                .orElse(null);
    }
}
