package network.aika.visualization;

import network.aika.neuron.activation.Activation;
import network.aika.text.Document;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

public class AikaLayout extends SpringBox {

    Document doc;
    Graph graph;

    AikaLayout(Document doc, Graph g) {
        this.doc = doc;
        this.graph = g;
    }

    @Override
    public String getLayoutAlgorithmName() {
        return "AikaLayout";
    }

    @Override
    protected void chooseNodePosition(NodeParticle n0, NodeParticle n1) {
        super.chooseNodePosition(n0, n1);
    }

    @Override
    public NodeParticle newNodeParticle(String id) {
        Node n = graph.getNode(id);
        Activation act = doc.getActivation(n.getAttribute("aika.id", Integer.class));

        Integer originActId = n.getAttribute("aika.originActId", Integer.class);

        if(originActId != null) {
//            Activation originAct = doc.getActivation(originActId);
//            Node originNode = graph.getNode("" + originAct.getId());

            return new ActivationParticle(n, act, this, id);
        } else {
            return new ActivationParticle(n, act, this, id);
        }
    }
}
