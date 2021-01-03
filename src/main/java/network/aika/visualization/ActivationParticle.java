package network.aika.visualization;

import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Fired;
import network.aika.neuron.activation.Link;
import network.aika.neuron.excitatory.PatternPartSynapse;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.layout.springbox.implementations.SpringBoxNodeParticle;
import org.miv.pherd.geom.Point3;

public class ActivationParticle extends SpringBoxNodeParticle {
    /**
     * The optimal distance between nodes.
     */
    protected static double k = 1f;

    /**
     * Default attraction.
     */
    protected static double K1Init = 0.06f; // 0.3 ??
    protected static double K1 = 0.01f; // 0.3 ??

    /**
     * Default repulsion.
     */
    protected static double K2 = 0.024f; // 0.12 ??

    Activation act;
    Node node;

    public ActivationParticle(Node node, Activation act, SpringBox box, String id) {
        super(box, id);
        this.act = act;
        this.node = node;
    }

    public ActivationParticle(Node node, Activation act, SpringBox box, String id, double x, double y, double z) {
        super(box, id, x, y, z);
        this.act = act;
        this.node = node;
    }


    @Override
    protected void attraction(Vector3 delta) {
//        super.attraction(delta);

        Boolean initNode = node.getAttribute("aika.init-node", Boolean.class);

        double strength = initNode ? K1Init : K1;

        SpringBox box = (SpringBox) this.box;
        boolean is3D = box.is3D();
        Energies energies = box.getEnergies();

        for (EdgeSpring edge : neighbours) {
            if (!edge.ignored) {
                edgeAttraction(delta, edge, strength, energies);

                ActivationParticle other = (ActivationParticle) edge.getOpposite(this);
                Link link = act.getInputLinks()
                        .filter(l -> l.getInput() == other.act)
                        .findFirst()
                        .orElse(null);
                if(link == null)
                    continue;

                Synapse s = link.getSynapse();
                boolean isRecurrent = false;
                if(s instanceof PatternPartSynapse) {
                    PatternPartSynapse pps = (PatternPartSynapse) s;
                    isRecurrent = pps.isRecurrent();
                }

                Fired fIn = link.getInput().getFired();
                Fired fOut = link.getOutput().getFired();


                Point3 opos = other.getPosition();

                double dx = opos.x - pos.x;

                double dy = 0.0;
                int fDiff = 0;
                if(!isRecurrent) {
                    fDiff = fOut.getFired() - fIn.getFired();
                    dy = Math.max(0.0, opos.y - pos.y);
                }

                delta.set(dx, dy, is3D ? opos.z - pos.z : 0);

//                double len = delta.normalize();
//                double k = this.k * edge.weight;
                double factor = strength;

                delta.scalarMult(factor);

                disp.add(delta);
                attE += factor;
                energies.accumulateEnergy(factor);

//                System.out.println("in:" + other.getId() + " out:" + act.getId() + " fDiff:" + fDiff + " xd:" + dx + " yd:" + dy);
            }
        }

    }

    private void edgeAttraction(Vector3 delta, EdgeSpring edge, double strength, Energies energies) {
        int neighbourCount = neighbours.size();

        NodeParticle other = edge.getOpposite(this);
        Point3 opos = other.getPosition();

        delta.set(opos.x - pos.x, opos.y - pos.y, 0);

        double len = delta.normalize();
        double k = this.k * edge.weight;
        double factor = strength * (len - k);

        delta.scalarMult(factor * (1f / (neighbourCount * 0.1f)));

        disp.add(delta);
        attE += factor;
        energies.accumulateEnergy(factor);
    }
}
