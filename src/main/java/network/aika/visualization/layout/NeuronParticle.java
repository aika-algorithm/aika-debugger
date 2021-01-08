package network.aika.visualization.layout;


import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;


public class NeuronParticle extends AbstractParticle {

    public NeuronParticle(AbstractLayout layout, String id, double x, double y, double z) {
        super(layout, id, x, y, z);
    }

    @Override
    protected void attraction(Vector3 delta) {
        SpringBox box = (SpringBox) this.box;
        Energies energies = box.getEnergies();

        for (EdgeSpring edge : neighbours) {
            if (!edge.ignored) {
                edgeAttraction(delta, edge, 1.0, energies);
            }
        }
    }
}
