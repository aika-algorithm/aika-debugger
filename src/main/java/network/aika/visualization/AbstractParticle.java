package network.aika.visualization;

import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.implementations.SpringBoxNodeParticle;

public class AbstractParticle extends SpringBoxNodeParticle {


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
}
