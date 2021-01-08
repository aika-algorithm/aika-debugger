package network.aika.visualization;

import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBoxNodeParticle;
import org.miv.pherd.geom.Point3;

import static network.aika.visualization.AbstractLayout.INITIAL_DISTANCE;


public abstract class AbstractParticle extends SpringBoxNodeParticle {

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

    protected void edgeAttraction(Vector3 delta, EdgeSpring edge, double strength, Energies energies) {
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
