package network.aika.visualization;

import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Fired;
import network.aika.neuron.activation.Link;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.neuron.excitatory.PatternPartSynapse;
import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.layout.springbox.implementations.SpringBoxNodeParticle;
import org.miv.pherd.geom.Point3;

public class AikaParticle extends SpringBoxNodeParticle {
    /**
     * The optimal distance between nodes.
     */
    protected static double k = 1f;

    /**
     * Default attraction.
     */
    protected static double K1 = 0.06f; // 0.3 ??

    /**
     * Default repulsion.
     */
    protected static double K2 = 0.024f; // 0.12 ??

    Activation act;

    public AikaParticle(Activation act, SpringBox box, String id) {
        super(box, id);
        this.act = act;
    }

    public AikaParticle(Activation act, SpringBox box, String id, double x, double y, double z) {
        super(box, id, x, y, z);
        this.act = act;
    }


    @Override
    protected void attraction(Vector3 delta) {
 //       super.attraction(delta);

        SpringBox box = (SpringBox) this.box;
        boolean is3D = box.is3D();
        Energies energies = box.getEnergies();
        int neighbourCount = neighbours.size();

        for (EdgeSpring edge : neighbours) {
            if (!edge.ignored) {
                AikaParticle other = (AikaParticle) edge.getOpposite(this);
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
                double factor = K1;

                delta.scalarMult(factor);

                disp.add(delta);
                attE += factor;
                energies.accumulateEnergy(factor);

                System.out.println("in:" + other.getId() + " out:" + act.getId() + " fDiff:" + fDiff + " xd:" + dx + " yd:" + dy);
            }
        }

    }
}
