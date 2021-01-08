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
