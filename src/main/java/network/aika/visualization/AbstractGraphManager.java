package network.aika.visualization;

import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public abstract class AbstractGraphManager<K, P> {


    private Graph graph;
    private Map<String, K> nodeIdToActivation = new TreeMap<>();
    private Map<Long, P> keyIdToParticle = new TreeMap<>();

    public AbstractGraphManager(Graph graph) {
        this.graph = graph;
    }

    public K getActivation(Node n) {
        return nodeIdToActivation.get(n.getId());
    }

    protected abstract long getKeyId(K key);


    public P getParticle(K key) {
        return getParticle(getKeyId(key));
    }
    public P getParticle(long keyId) {
        return keyIdToParticle.get(keyId);
    }

    public void setParticle(K key, P particle) {
        keyIdToParticle.put(getKeyId(key), particle);
    }


    public String getNodeId(K key) {
        return "" + getKeyId(key);
    }

    public String getEdgeId(K iKey, K oKey) {
        return getKeyId(iKey) + "-" + getKeyId(oKey);
    }

    public Node lookupNode(K key, Consumer<Node> onCreate) {
        String id = getNodeId(key);
        Node node = graph.getNode(id);

        if (node == null) {
            node = graph.addNode(id);
            onCreate.accept(node);
        }

        nodeIdToActivation.put(node.getId(), key);

        return node;
    }


    public Node getNode(K key) {
        String id = getNodeId(key);
        return graph.getNode(id);
    }

    public Edge lookupEdge(K iKey, K oKey, Consumer<Node> onCreate) {
        String edgeId = getEdgeId(iKey, oKey);
        Edge edge = graph.getEdge(edgeId);
        if (edge == null) {
            edge = graph.addEdge(edgeId, getNodeId(iKey), getNodeId(oKey), true);
        }
        return edge;
    }

    public Edge getEdge(K iKey, K oKey) {
        String edgeId = getEdgeId(iKey, oKey);
        return graph.getEdge(edgeId);
    }

    public Node getNode(String nodeId) {
        return graph.getNode(nodeId);
    }

}
