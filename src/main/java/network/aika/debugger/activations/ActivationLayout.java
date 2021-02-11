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
package network.aika.debugger.activations;

import network.aika.neuron.activation.Activation;
import network.aika.text.Document;
import network.aika.debugger.AbstractLayout;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.miv.pherd.geom.Point3;


public class ActivationLayout extends AbstractLayout {
    ActivationViewManager avm;
    ActivationGraphManager graphManager;

    public ActivationLayout(ActivationViewManager avm, ActivationGraphManager gm) {
        this.avm = avm;
        this.graphManager = gm;

        k = STANDARD_DISTANCE;
        K1Init = 0.06f;
        K1Final = 0.01f;
        K2 = 0.005f;
    }

    @Override
    public NodeParticle newNodeParticle(String id) {
        Document doc = avm.getDocument();
        Node n = graphManager.getNode(id);
        Activation act = doc.getActivation(n.getAttribute("aika.id", Integer.class));

        Integer originActId = n.getAttribute("aika.originActId", Integer.class);

        Double x;
        Double y;

        ActivationParticle particle;
        if(originActId != null) {
            ActivationParticle originParticle = graphManager.getParticle(originActId);
            Point3 originPos = originParticle.getPosition();

            x = originPos.x;
            y = originPos.y + STANDARD_DISTANCE;
        } else {
            x = (Double) n.getAttribute("x");
            y = (Double) n.getAttribute("y");

            if(x == null)
                x = 0.0;

            if(y == null)
                y = 0.0;
        }

        if(Math.abs(x) < 0.01 || Math.abs(y) < 0.01) {
            x += (random.nextDouble() - 0.5) * 0.02;
            y += (random.nextDouble() - 0.5) * 0.02;
        }

        particle = new ActivationParticle(this, n, act, id, x, y, 0);

        graphManager.setParticle(act, particle);

        return particle;
    }
/*
    public void particleMoved(Object id, double x, double y, double z) {
        super.particleMoved(id, x, y, z);

        Activation act = graphManager.getKey((String)id);
        ActivationParticle ap = graphManager.getParticle(act);

        System.out.println(act.getLabel() + " x:" + x + " y:" + y);
    }
 */
}
