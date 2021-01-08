package network.aika.visualization.layout;

import network.aika.neuron.activation.Activation;
import network.aika.text.Document;
import network.aika.visualization.ActivationViewManager;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.miv.pherd.geom.Point3;

public class ActivationLayout extends AbstractLayout {
    ActivationViewManager avm;
    ActivationGraphManager graphManager;

    public ActivationLayout(ActivationViewManager avm, ActivationGraphManager gm) {
        this.avm = avm;
        this.graphManager = gm;

        k = INITIAL_DISTANCE;
        K1Init = 0.06f;
        K1Final = 0.01f;
        K2 = 0.005f;
    }

    @Override
    public NodeParticle newNodeParticle(String id) {
        Document doc = avm.getDocument();
        Node n = graphManager.getNode(id);
        Activation act = doc.getActivation(n.getAttribute("aika.id", Integer.class));

        Integer originActId = n.getAttribute("aika.originActId", Integer.class);

        Double x;
        Double y;

        ActivationParticle particle;
        if(originActId != null) {
            ActivationParticle originParticle = graphManager.getParticle(originActId);
            Point3 originPos = originParticle.getPosition();

            x = originPos.x;
            y = originPos.y + INITIAL_DISTANCE;
        } else {
            x = (Double) n.getAttribute("x");
            y = (Double) n.getAttribute("y");

            if(x == null)
                x = 0.0;

            if(y == null)
                y = 0.0;
        }

        if(Math.abs(x) > 0.01 || Math.abs(y) > 0.01) {
            double randomValue = (random.nextDouble() - 0.5) * 0.02;
            x += randomValue;
            y += randomValue;
        }

        particle = new ActivationParticle(this, n, act, id, x, y, 0);

        graphManager.setParticle(act, particle);

        return particle;
    }
}
