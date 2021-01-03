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
    Graph graph;

    AikaLayout(ActivationViewerManager avm, Graph g) {
        this.avm = avm;
        this.graph = g;
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
        Node n = graph.getNode(id);
        Activation act = doc.getActivation(n.getAttribute("aika.id", Integer.class));

        Integer originActId = n.getAttribute("aika.originActId", Integer.class);

        ActivationParticle particle;
        if(originActId != null) {
            ActivationParticle originParticle = avm.actIdToParticle.get(originActId);
            Point3 originPos = originParticle.getPosition();

            particle = new ActivationParticle(n, act, this, id, originPos.x, originPos.y + 0.1, originPos.z);
        } else {
            Double x = (Double) n.getAttribute("x");
            Double y = (Double) n.getAttribute("y");

            particle = new ActivationParticle(n, act, this, id, x, y, 0);
        }

        avm.actIdToParticle.put(act.getId(), particle);

        return particle;
    }
}
