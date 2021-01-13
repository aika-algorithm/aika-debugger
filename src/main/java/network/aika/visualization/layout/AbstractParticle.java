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

import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBoxNodeParticle;
import org.miv.pherd.geom.Point3;

import static network.aika.visualization.layout.AbstractLayout.STANDARD_DISTANCE;


public abstract class AbstractParticle extends SpringBoxNodeParticle {

    public static double K1Attr = 0.0001;

    public AbstractParticle(AbstractLayout layout, String id, double x, double y, double z) {
        super(layout, id, x, y, z);
    }

    @Override
    protected void repulsionN2(Vector3 delta) {
        super.repulsionN2(delta);
/*
        if(act.getLabel().equalsIgnoreCase("der Rel Prev. Token"))
            System.out.println(System.identityHashCode(this) + " " + act.getLabel() + "repulsionN2:" + delta + " disp:" + disp + " pos:" + pos);
 */
    }

    @Override
    protected void repulsionNLogN(Vector3 delta) {
        super.repulsionNLogN(delta);

//        if(act.getLabel().equalsIgnoreCase("der Rel Prev. Token"))
//            System.out.println(System.identityHashCode(this) + " " + act.getLabel() + "repulsionNLogN:" + delta + " disp:" + disp + " pos:" + pos);
    }

    protected void edgeAttraction(Vector3 delta, EdgeSpring edge, Energies energies) {
        int neighbourCount = neighbours.size();

        NodeParticle other = edge.getOpposite(this);
        Point3 opos = other.getPosition();

        delta.set(opos.x - pos.x, opos.y - pos.y, 0);

        double len = delta.normalize();
        double factor = K1Attr;// * len;

        delta.scalarMult(factor * (1f / (neighbourCount * 0.1f)));

        disp.add(delta);
        attE += factor;
        energies.accumulateEnergy(factor);
    }
}
