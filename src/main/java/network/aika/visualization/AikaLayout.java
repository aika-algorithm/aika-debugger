package network.aika.visualization;

import network.aika.neuron.activation.Activation;
import network.aika.text.Document;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.miv.pherd.geom.Point3;


public abstract class AikaLayout extends SpringBox {

    GraphManager graphManager;

    protected static double k = 1f;

    protected static double K1Init;
    protected static double K1Final;


    public static double INITIAL_DISTANCE = 1f;

    @Override
    public String getLayoutAlgorithmName() {
        return "AikaLayout";
    }

    @Override
    protected void chooseNodePosition(NodeParticle n0, NodeParticle n1) {
  //      super.chooseNodePosition(n0, n1);
    }


}
