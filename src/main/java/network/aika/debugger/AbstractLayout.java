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
package network.aika.debugger;

import network.aika.debugger.activations.ActivationGraphManager;
import network.aika.debugger.activations.ActivationParticle;
import network.aika.debugger.activations.ActivationViewManager;
import network.aika.neuron.activation.Activation;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;


public abstract class AbstractLayout<G extends AbstractGraphManager> extends SpringBox {

    protected static double k = 1f;

    protected static double K1Init;
    protected static double K1Final;


    public static double STANDARD_DISTANCE_X = 0.2f;
    public static double STANDARD_DISTANCE_Y = 0.2f;

    protected G graphManager;

    public AbstractLayout(G gm) {
        this.graphManager = gm;
    }

    @Override
    public String getLayoutAlgorithmName() {
        return "AikaLayout";
    }

    @Override
    protected void chooseNodePosition(NodeParticle n0, NodeParticle n1) {
  //      super.chooseNodePosition(n0, n1);

    }

    public void particleMoved(Object id, double x, double y, double z) {
        super.particleMoved(id, x, y, z);

        AbstractParticle ap = graphManager.getParticle(graphManager.getAikaNode((String)id));

        ap.x = x;
        ap.y = y;
    }

}
