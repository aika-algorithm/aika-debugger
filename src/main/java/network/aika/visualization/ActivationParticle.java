package network.aika.visualization;

import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Fired;
import network.aika.neuron.activation.Link;
import network.aika.neuron.excitatory.PatternPartSynapse;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.layout.springbox.implementations.SpringBoxNodeParticle;
import org.miv.pherd.geom.Point3;

import static network.aika.visualization.AikaLayout.*;

public class ActivationParticle extends SpringBoxNodeParticle {

    Activation act;
    Node node;

    public ActivationParticle(Node node, Activation act, SpringBox box, String id, double x, double y, double z) {
        super(box, id, x, y, z);

        this.act = act;
        this.node = node;
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

        if(act.getLabel().equalsIgnoreCase("der Rel Prev. Token"))
            System.out.println(System.identityHashCode(this) + " " + act.getLabel() + "repulsionNLogN:" + delta + " disp:" + disp + " pos:" + pos);

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

    private void edgeAttraction(Vector3 delta, EdgeSpring edge, double strength, Energies energies) {
        int neighbourCount = neighbours.size();

        NodeParticle other = edge.getOpposite(this);
        Point3 opos = other.getPosition();

        delta.set(opos.x - pos.x, opos.y - pos.y, 0);

        double len = delta.normalize();
        double k = INITIAL_DISTANCE * edge.weight;
        double factor = strength * (len - k);

        delta.scalarMult(factor * (1f / (neighbourCount * 0.1f)));

        disp.add(delta);
        attE += factor;
        energies.accumulateEnergy(factor);
    }
}
