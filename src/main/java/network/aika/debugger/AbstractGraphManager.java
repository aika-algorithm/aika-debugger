/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network.aika.debugger;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public abstract class AbstractGraphManager<K, P> {


    private Graph graph;
    private Map<String, K> nodeIdToAikaNode = new TreeMap<>();
    private Map<Long, P> keyIdToParticle = new TreeMap<>();

    public AbstractGraphManager(Graph graph) {
        this.graph = graph;
    }

    public K getKey(Node n) {
        return nodeIdToAikaNode.get(n.getId());
    }

    public K getKey(String nodeId) {
        return nodeIdToAikaNode.get(nodeId);
    }

    public K getInputKey(Edge e) {
        return nodeIdToAikaNode.get(e.getId().substring(0, e.getId().indexOf("-")));
    }

    public K getOutputKey(Edge e) {
        return nodeIdToAikaNode.get(e.getId().substring(e.getId().indexOf("-") + 1));
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

        nodeIdToAikaNode.put(node.getId(), key);

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
