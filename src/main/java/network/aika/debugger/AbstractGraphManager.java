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

public abstract class AbstractGraphManager<N, L, P extends AbstractParticle> {

    private Graph graph;
    private Map<String, N> nodeIdToAikaNode = new TreeMap<>();
    private Map<Long, P> keyIdToParticle = new TreeMap<>();

    public AbstractGraphManager(Graph graph) {
        this.graph = graph;
    }

    public N getAikaNode(Node n) {
        return nodeIdToAikaNode.get(n.getId());
    }

    public N getAikaNode(String nodeId) {
        return nodeIdToAikaNode.get(nodeId);
    }

    public N getInputKey(Edge e) {
        return nodeIdToAikaNode.get(e.getId().substring(0, e.getId().indexOf("-")));
    }

    public N getOutputKey(Edge e) {
        return nodeIdToAikaNode.get(e.getId().substring(e.getId().indexOf("-") + 1));
    }

    protected abstract Long getAikaNodeId(N key);

    public P getParticle(Node n) {
        return getParticle(Long.valueOf(n.getId()));
    }

    public P getParticle(N key) {
        return getParticle(getAikaNodeId(key));
    }

    public P getParticle(long keyId) {
        return keyIdToParticle.get(keyId);
    }

    public void setParticle(N key, P particle) {
        keyIdToParticle.put(getAikaNodeId(key), particle);
    }

    public String getNodeId(N key) {
        return "" + getAikaNodeId(key);
    }

    public String getEdgeId(N iKey, N oKey) {
        return getAikaNodeId(iKey) + "-" + getAikaNodeId(oKey);
    }

    public Node lookupNode(N key, Consumer<Node> onCreate) {
        String id = getNodeId(key);
        Node node = graph.getNode(id);

        if (node == null) {
            node = graph.addNode(id);
            onCreate.accept(node);
        }

        nodeIdToAikaNode.put(node.getId(), key);

        return node;
    }

    public Node getNode(N key) {
        String id = getNodeId(key);
        return graph.getNode(id);
    }

    public Edge lookupEdge(N iKey, N oKey, Consumer<Node> onCreate) {
        String edgeId = getEdgeId(iKey, oKey);
        Edge edge = graph.getEdge(edgeId);
        if (edge == null) {
            edge = graph.addEdge(edgeId, getNodeId(iKey), getNodeId(oKey), true);
        }
        return edge;
    }

    public Edge getEdge(N iKey, N oKey) {
        String edgeId = getEdgeId(iKey, oKey);
        return graph.getEdge(edgeId);
    }

    public Node getNode(String nodeId) {
        return graph.getNode(nodeId);
    }

    public abstract Edge lookupEdge(L l, Consumer<Node> onCreate);

    public abstract Edge getEdge(L l);

    public abstract L getLink(Edge e);
}
