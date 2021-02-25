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
package network.aika.debugger.activations;

import network.aika.callbacks.EventListener;
import network.aika.debugger.StepManager;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Fired;
import network.aika.neuron.activation.Link;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.neuron.phase.Phase;
import network.aika.text.Document;
import network.aika.debugger.AbstractViewManager;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static network.aika.debugger.StepManager.EventType.ACT;
import static network.aika.debugger.StepManager.EventType.LINK;
import static network.aika.debugger.StepManager.When.*;
import static network.aika.neuron.activation.Fired.NOT_FIRED;
import static network.aika.debugger.AbstractLayout.STANDARD_DISTANCE;


public class ActivationViewManager extends AbstractViewManager<ActivationConsole, ActivationGraphManager> implements EventListener {

    private Document doc;

    private VisitorManager visitorManager;

    private QueueConsole queueConsole;

    protected StepManager stepManager;

    public ActivationViewManager(Document doc) {
        super();
        graphManager = new ActivationGraphManager(graph);

        this.doc = doc;
        doc.addEventListener(this);
        visitorManager = new VisitorManager(this);
        console = new ActivationConsole();
        queueConsole = new QueueConsole();
        viewer.enableAutoLayout(new ActivationLayout(this, graphManager));

        splitPane = initSplitPane();

        this.stepManager = new StepManager(visitorManager);
    }


    public StepManager getStepManager() {
        return stepManager;
    }

    public void pumpAndWaitForUserAction() {
        pump();

        System.out.println("Viewport: " + graphView.getCamera().getViewCenter() + " Zoom:" + graphView.getCamera().getViewPercent());

        stepManager.waitForClick();
    }

    public void click(int x, int y) {
        graph.edges()
                .filter(e ->
                        checkBoundingBox(e, x, y)
                )
                .forEach(e ->
                        System.out.println(e.getId())
                );
    }

    private boolean checkBoundingBox(Edge e, int x, int y) {
        Point3 mousePos = getCamera().transformPxToGu(x, y);

        Object[] xyz0 = (Object[]) e.getNode0().getAttribute("xyz");
        Object[] xyz1 = (Object[]) e.getNode1().getAttribute("xyz");
        if(xyz0 == null || xyz1 == null) {
            return false;
        }

        Double x0 = (Double) xyz0[0];
        Double y0 = (Double) xyz0[1];
        Double x1 = (Double) xyz1[0];
        Double y1 = (Double) xyz1[1];

        DefaultCamera2D cam = (DefaultCamera2D) getCamera();

        Graphics2D g = null;
        try {
            Field f = cam.getClass().getDeclaredField("bck");
            f.setAccessible(true);
            g = ((Backend) f.get(cam)).graphics2D();
        } catch (NoSuchFieldException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }

        g.draw(new Ellipse2D.Double(x0, y0, 0.01, 0.01));
        g.draw(new Ellipse2D.Double(x1, y1, 0.01, 0.01));
        g.draw(new Ellipse2D.Double(x, y, 0.01, 0.01));
        g.draw(new Ellipse2D.Double(mousePos.x, mousePos.y, 0.01, 0.01));


        return Math.min(x0, x1) <= mousePos.x &&
                Math.min(y0, y1) <= mousePos.y &&
                Math.max(x0, x1) >= mousePos.x &&
                Math.max(y0, y1) >= mousePos.y;
    }

    public void showElementContext(String headlinePrefix, GraphicElement ge) {
        if(ge instanceof Node) {
            Node n = (Node) ge;

            Activation act = graphManager.getAikaNode(n);
            if(act == null)
                return;

            console.render(headlinePrefix, sDoc ->
                    console.renderActivationConsoleOutput(sDoc, act, graphManager.getParticle(act))
            );
        } else if(ge instanceof Edge) {
            Edge e = (Edge) ge;

        }
    }

    @Override
    public JComponent getConsolePane() {
        JScrollPane paneScrollPane = new JScrollPane(console);
        paneScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        paneScrollPane.setPreferredSize(new Dimension(250, 155));
        paneScrollPane.setMinimumSize(new Dimension(10, 10));

        JScrollPane queuePaneScrollPane = new JScrollPane(queueConsole);
        queuePaneScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        queuePaneScrollPane.setPreferredSize(new Dimension(250, 155));
        queuePaneScrollPane.setMinimumSize(new Dimension(10, 10));

        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, paneScrollPane, queuePaneScrollPane);
        sp.setResizeWeight(0.52);
        return sp;
    }

    @Override
    public void onActivationCreationEvent(Activation act, Activation originAct) {
        Node n = onActivationEvent(act, originAct);

        if(!stepManager.stopHere(NEW, ACT))
            return;

        n.setAttribute("aika.init-node", true);

        console.render("New", sDoc ->
                console.renderActivationConsoleOutput(sDoc,  act, graphManager.getParticle(act))
        );

        pumpAndWaitForUserAction();
    }

    @Override
    public void onActivationProcessedEvent(Phase p, Activation act) {
        queueConsole.render("Queue", sDoc ->
                queueConsole.renderQueue(sDoc, act.getThought())
        );

        Node n = onActivationEvent(act, null);
        n.setAttribute("aika.init-node", false);

        if (!stepManager.stopHere(BEFORE, ACT))
            return;

        console.render("Before " + Phase.toString(p), sDoc ->
                console.renderActivationConsoleOutput(sDoc, act, graphManager.getParticle(act))
        );

        pumpAndWaitForUserAction();
    }

    @Override
    public void afterActivationProcessedEvent(Phase p, Activation act) {
        if (!stepManager.stopHere(AFTER, ACT))
            return;

        console.render("After " + Phase.toString(p), sDoc ->
                console.renderActivationConsoleOutput(sDoc, act, graphManager.getParticle(act))
        );

        pumpAndWaitForUserAction();
    }

    private Node onActivationEvent(Activation act, Activation originAct) {
        Node node = graphManager.lookupNode(act, n -> {
            if(originAct != null) {
                Edge initialEdge = graphManager.lookupEdge(originAct, act, e -> {});
                initialEdge.setAttribute("ui.style", "fill-color: rgb(200,200,200);");
            }

            if(act.getNeuron().isInputNeuron() && act.getNeuron() instanceof PatternNeuron) {
                n.setAttribute("layout.frozen");
            }
            if(act.getNeuron().isInputNeuron() && act.getFired() != NOT_FIRED) {
                Fired f = act.getFired();
                n.setAttribute("x", f.getInputTimestamp() * STANDARD_DISTANCE);
            }
        });

        node.setAttribute("aika.id", act.getId());
        if(originAct != null) {
            node.setAttribute("aika.originActId", originAct.getId());
        }
        node.setAttribute("ui.label", act.getLabel());

        highlightCurrentOnly(node);

        Consumer<Node> neuronTypeModifier = neuronTypeModifiers.get(act.getNeuron().getClass());
        if (neuronTypeModifier != null) {
            neuronTypeModifier.accept(node);
        }

        return node;
    }


    private void highlightCurrentOnly(Element e) {
        if(lastHighlighted != e) {
            if(lastHighlighted != null) {
                unhighlightElement(lastHighlighted);
            }
            lastHighlighted = e;
            highlightElement(e);
        }
    }

    @Override
    public void onLinkCreationEvent(Link l) {
        Edge e = onLinkEvent(l);

        e.setAttribute("aika.init-node", true);

        if (!stepManager.stopHere(NEW, LINK))
            return;

        console.render("New", sDoc ->
                console.renderLinkConsoleOutput(sDoc, l)
        );

        pumpAndWaitForUserAction();
    }

    @Override
    public void onLinkProcessedEvent(Phase p, Link l) {
        queueConsole.render("Queue", sDoc ->
                queueConsole.renderQueue(sDoc, l.getThought())
        );

        Edge e = onLinkEvent(l);

        e.setAttribute("aika.init-node", false);

        if (!stepManager.stopHere(BEFORE, LINK))
            return;

        DefaultStyledDocument sDoc = new DefaultStyledDocument();
        console.addStylesToDocument(sDoc);
        console.clear();
        console.addHeadline(sDoc, "Before " + Phase.toString(p));
        console.renderLinkConsoleOutput(sDoc, l);
        console.setStyledDocument(sDoc);

        pumpAndWaitForUserAction();
    }

    @Override
    public void afterLinkProcessedEvent(Phase p, Link l) {
        if (!stepManager.stopHere(AFTER, LINK))
            return;

        DefaultStyledDocument sDoc = new DefaultStyledDocument();
        console.addStylesToDocument(sDoc);
        console.clear();
        console.addHeadline(sDoc, "After " + Phase.toString(p));
        console.renderLinkConsoleOutput(sDoc, l);
        console.setStyledDocument(sDoc);


        pumpAndWaitForUserAction();
    }

    private Edge onLinkEvent(Link l) {
        Edge edge = graphManager.lookupEdge(l, e -> {});

        highlightCurrentOnly(edge);

        BiConsumer<Edge, Synapse> synapseTypeModifier = synapseTypeModifiers.get(l.getSynapse().getClass());
        if(synapseTypeModifier != null) {
            synapseTypeModifier.accept(edge, l.getSynapse());
        }

        return edge;
    }

    public void viewClosed(String id) {
   //     loop = false;
    }

    public Document getDocument() {
        return doc;
    }

    public VisitorManager getVisitorManager() {
        return visitorManager;
    }
}
