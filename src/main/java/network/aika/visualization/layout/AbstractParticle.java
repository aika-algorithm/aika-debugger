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
import org.graphstream.ui.layout.springbox.GraphCellData;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.layout.springbox.implementations.SpringBoxNodeParticle;
import org.miv.pherd.Particle;
import org.miv.pherd.ParticleBox;
import org.miv.pherd.geom.Point3;
import org.miv.pherd.ntree.Cell;

import java.util.Iterator;

import static network.aika.visualization.layout.AbstractLayout.STANDARD_DISTANCE;


public abstract class AbstractParticle extends SpringBoxNodeParticle {

    public static double K1Attr = 0.0001;

    /**
     * Default repulsion.
     */
    protected double K2 = 0.000005f; // 0.12 ??


    public AbstractParticle(AbstractLayout layout, String id, double x, double y, double z) {
        super(layout, id, x, y, z);
    }


    @Override
    protected void repulsionN2(Vector3 delta) {
        AbstractLayout box = (AbstractLayout) this.box;
        boolean is3D = box.is3D();
        ParticleBox nodes = box.getSpatialIndex();
        Energies energies = box.getEnergies();
        Iterator<Object> i = nodes.getParticleIdIterator();

        while (i.hasNext()) {
            AbstractParticle node = (AbstractParticle) nodes.getParticle(i.next());

            if (node != this) {
                delta.set(node.pos.x - pos.x, 0.0, 0.0);
//                delta.set(node.pos.x - pos.x, node.pos.y - pos.y, is3D ? node.pos.z - pos.z : 0);

                double len = delta.normalize();

                if (len > 0) {
                    if (len < box.k)
                        len = box.k; // XXX NEW To prevent infinite
                    // repulsion.

                    double factor = ((K2 / (len * len)) * node.weight);

                    energies.accumulateEnergy(factor); // TODO check this
                    delta.scalarMult(-factor);
                    disp.add(delta);
                }
            }
        }
    }

    @Override
    protected void repulsionNLogN(Vector3 delta) {
        // Explore the n-tree from the root cell and consider the contents
        // of one cell only if it does intersect an area around the current
        // node. Else take its (weighted) barycenter into account.

        recurseRepulsion(box.getSpatialIndex().getNTree().getRootCell(), delta);
    }

    protected void recurseRepulsion(Cell cell, Vector3 delta) {
        AbstractLayout box = (AbstractLayout) this.box;
        boolean is3D = box.is3D();
        Energies energies = box.getEnergies();

        if (intersection(cell)) {
            if (cell.isLeaf()) {
                Iterator<? extends Particle> i = cell.getParticles();

                while (i.hasNext()) {
                    AbstractParticle node = (AbstractParticle) i.next();

                    if (node != this) {
                        delta.set(node.pos.x - pos.x, 0.0, 0.0);
//                        delta.set(node.pos.x - pos.x, node.pos.y - pos.y, is3D ? node.pos.z - pos.z : 0);

                        double len = delta.normalize();

                        if (len > 0)// && len < ( box.k * box.viewZone ) )
                        {
                            if (len < box.k)
                                len = box.k; // XXX NEW To prevent infinite
                            // repulsion.
                            double factor = ((K2 / (len * len)) * node.weight);
                            energies.accumulateEnergy(factor); // TODO check
                            // this
                            repE += factor;
                            delta.scalarMult(-factor);
                            disp.add(delta);
                        }
                    }
                }
            } else {
                int div = cell.getSpace().getDivisions();

                for (int i = 0; i < div; i++)
                    recurseRepulsion(cell.getSub(i), delta);
            }
        } else {
            if (cell != this.cell) {
                GraphCellData bary = (GraphCellData) cell.getData();

                double dist = bary.distanceFrom(pos);
                double size = cell.getSpace().getSize();

                if ((!cell.isLeaf()) && ((size / dist) > box.getBarnesHutTheta())) {
                    int div = cell.getSpace().getDivisions();

                    for (int i = 0; i < div; i++)
                        recurseRepulsion(cell.getSub(i), delta);
                } else {
                    if (bary.weight != 0) {
                        delta.set(bary.center.x - pos.x, 0.0, 0.0);
//                        delta.set(bary.center.x - pos.x, bary.center.y - pos.y, is3D ? bary.center.z - pos.z : 0);

                        double len = delta.normalize();

                        if (len > 0) {
                            if (len < box.k)
                                len = box.k; // XXX NEW To prevent infinite
                            // repulsion.
                            double factor = ((K2 / (len * len)) * (bary.weight));
                            energies.accumulateEnergy(factor);
                            delta.scalarMult(-factor);
                            repE += factor;

                            disp.add(delta);
                        }
                    }
                }
            }
        }
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
