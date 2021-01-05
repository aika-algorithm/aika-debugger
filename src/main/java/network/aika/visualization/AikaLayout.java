package network.aika.visualization;

import network.aika.neuron.activation.Activation;
import network.aika.text.Document;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.miv.pherd.geom.Point3;


public class AikaLayout extends SpringBox {

    ActivationViewerManager avm;
    GraphManager graphManager;

    protected static double k = 1f;

    protected static double K1Init;
    protected static double K1Final;


    public static double INITIAL_DISTANCE = 1f;

    AikaLayout(ActivationViewerManager avm, GraphManager gm) {
        this.avm = avm;
        this.graphManager = gm;

        k = INITIAL_DISTANCE;
        K1Init = 0.06f;
        K1Final = 0.01f;
        K2 = 0.005f;
    }

    @Override
    public String getLayoutAlgorithmName() {
        return "AikaLayout";
    }

    @Override
    protected void chooseNodePosition(NodeParticle n0, NodeParticle n1) {
  //      super.chooseNodePosition(n0, n1);
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

        particle = new ActivationParticle(n, act, this, id, x, y, 0);

        graphManager.setParticle(act, particle);

        return particle;
    }
}
