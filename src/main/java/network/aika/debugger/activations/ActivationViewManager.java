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
import network.aika.neuron.activation.QueueEntry;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.neuron.steps.Step;
import network.aika.text.Document;
import network.aika.debugger.AbstractViewManager;
import network.aika.text.TextModel;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.camera.DefaultCamera2D;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static network.aika.debugger.AbstractLayout.*;
import static network.aika.debugger.StepManager.EventType.ACT;
import static network.aika.debugger.StepManager.EventType.LINK;
import static network.aika.debugger.StepManager.When.*;
import static network.aika.neuron.activation.Fired.NOT_FIRED;


public class ActivationViewManager extends AbstractViewManager<ActivationConsole, ActivationGraphManager> implements EventListener {

    private Document doc;

    private VisitorManager visitorManager;

    private QueueConsole queueConsole;

    private VisitorConsole visitorConsole;

    protected StepManager stepManager;

    private Long numberOfInputTokens;

    public ActivationViewManager(Document doc) {
        super();

        computeNumberOfInputTokens(doc);

        double width = numberOfInputTokens * STANDARD_DISTANCE_X;
        double height = 3 * STANDARD_DISTANCE_Y;

        getCamera().setGraphViewport(-(width / 2), -(height / 2), (width / 2), (height / 2));
        getCamera().setViewCenter(0.20, 0.20, 0.0);

        graphManager = new ActivationGraphManager(graph);

        this.doc = doc;
        doc.addEventListener(this);
        visitorManager = new VisitorManager(this);
        mainConsole = new ActivationConsole();
        selectedConsole = new ActivationConsole();
        queueConsole = new QueueConsole();
        visitorConsole = new VisitorConsole();

        viewer.enableAutoLayout(new ActivationLayout(this, graphManager));

        splitPane = initSplitPane();

        this.stepManager = new StepManager(visitorManager);
    }

    public VisitorConsole getVisitorConsole() {
        return visitorConsole;
    }

    public double scaleCharsToTokens() {
        return (double) numberOfInputTokens / (double) doc.length();
    }

    public StepManager getStepManager() {
        return stepManager;
    }

    public void pumpAndWaitForUserAction() {
        pump();

        stepManager.waitForClick();
    }

    public void showElementContext(GraphicElement ge) {
        if(ge instanceof Node) {
            Node n = (Node) ge;

            Activation act = graphManager.getAikaNode(n);
            if(act == null)
                return;

            selectedConsole.render(sDoc ->
                    selectedConsole.renderActivationConsoleOutput(sDoc, act, null)
            );
        } else if(ge instanceof Edge) {
            Edge e = (Edge) ge;

            Link l = graphManager.getLink(e);
            if(l == null)
                return;

            selectedConsole.render(sDoc ->
                    selectedConsole.renderLinkConsoleOutput(sDoc, l, null)
            );
        }
    }

    @Override
    public JComponent getConsolePane() {
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                getConsoleTabbedPane(),
                getScrollPane(queueConsole)
        );
        sp.setResizeWeight(0.65);

        return sp;
    }

    private JComponent getConsoleTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
//        ImageIcon icon = createImageIcon("images/middle.gif");

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        tabbedPane.setFocusCycleRoot(true);

        tabbedPane.addTab(
                "Main",
                null,
                getScrollPane(mainConsole.getSplitPane()),
                "Shows the currently processed graph element"
        );

        tabbedPane.addTab(
                "Selected",
                null,
                getScrollPane(selectedConsole.getSplitPane()),
                "Shows the selected graph element"
        );

        tabbedPane.addTab(
                "Visitor",
                null,
                getScrollPane(visitorConsole),
                "Shows the path of the visitor"
        );

        return tabbedPane;
    }

    private static JScrollPane getScrollPane(Component c) {
        JScrollPane scrollPane = new JScrollPane(c);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(250, 155));
        scrollPane.setMinimumSize(new Dimension(10, 10));
        return scrollPane;
    }

    @Override
    public void onActivationCreationEvent(Activation act, Activation originAct) {
        Node n = onActivationEvent(act, originAct);

        if(!stepManager.stopHere(NEW, ACT))
            return;

        n.setAttribute("aika.init-node", true);

        mainConsole.render(sDoc ->
                mainConsole.renderActivationConsoleOutput(sDoc,  act, "New")
        );

        pumpAndWaitForUserAction();
    }

    @Override
    public void beforeProcessedEvent(QueueEntry qe) {
        if(qe.getElement() instanceof Activation) {
            beforeActivationProcessedEvent(qe, (Activation) qe.getElement());
        } else if(qe.getElement() instanceof Link) {
            beforeLinkProcessedEvent(qe, (Link) qe.getElement());
        }
    }


    @Override
    public void afterProcessedEvent(QueueEntry qe) {
        if(qe.getElement() instanceof Activation) {
            afterActivationProcessedEvent(qe, (Activation) qe.getElement());
        } else if(qe.getElement() instanceof Link) {
            afterLinkProcessedEvent(qe, (Link) qe.getElement());
        }
    }

    private void beforeActivationProcessedEvent(QueueEntry qe, Activation act) {
        queueConsole.render(sDoc ->
                queueConsole.renderQueue(sDoc, act.getThought(), qe)
        );

        Node n = onActivationEvent(act, null);
        n.setAttribute("aika.init-node", false);

        if (!stepManager.stopHere(BEFORE, ACT))
            return;

        mainConsole.render(sDoc ->
                mainConsole.renderActivationConsoleOutput(sDoc, act, "Before " + Step.toString(qe.getStep()))
        );

        pumpAndWaitForUserAction();
    }


    private void afterActivationProcessedEvent(QueueEntry qe, Activation act) {
        queueConsole.render(sDoc ->
                queueConsole.renderQueue(sDoc, act.getThought(), qe)
        );

        if (!stepManager.stopHere(AFTER, ACT))
            return;

        mainConsole.render(sDoc ->
                mainConsole.renderActivationConsoleOutput(sDoc, act, "After " + Step.toString(qe.getStep()))
        );

        pumpAndWaitForUserAction();
    }

    private void computeNumberOfInputTokens(Document doc) {
        numberOfInputTokens = doc.getActivations()
                .stream()
                .filter(act -> act.getNeuron().isInputNeuron() && act.getNeuron() instanceof PatternNeuron)
                .count();
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
                             n.setAttribute("x", getXPosGU(f));
            }

            if(act.getNeuron().isInputNeuron() && originAct != null && originAct.getFired() != NOT_FIRED) {
                double offset = STANDARD_DISTANCE_X * 0.3;
                Fired f = originAct.getFired();
                if(act.getLabel().endsWith(TextModel.REL_NEXT_TOKEN_LABEL)) {
                    n.setAttribute("x", getXPosGU(f) + offset);
                }

                if(act.getLabel().endsWith(TextModel.REL_PREVIOUS_TOKEN_LABEL)) {
                    n.setAttribute("x", getXPosGU(f) - offset);
                }
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

    private double getXPosGU(Fired f) {
        return f.getInputTimestamp() * scaleCharsToTokens() * STANDARD_DISTANCE_X;
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

        mainConsole.render(sDoc ->
                mainConsole.renderLinkConsoleOutput(sDoc, l, "New")
        );

        pumpAndWaitForUserAction();
    }

    private void beforeLinkProcessedEvent(QueueEntry qe, Link l) {
        queueConsole.render(sDoc ->
                queueConsole.renderQueue(sDoc, l.getThought(), qe)
        );

        Edge e = onLinkEvent(l);

        e.setAttribute("aika.init-node", false);

        if (!stepManager.stopHere(BEFORE, LINK))
            return;

        DefaultStyledDocument sDoc = new DefaultStyledDocument();
        mainConsole.addStylesToDocument(sDoc);
        mainConsole.clear();
        mainConsole.renderLinkConsoleOutput(sDoc, l, "Before " + Step.toString(qe.getStep()));
        mainConsole.setStyledDocument(sDoc);

        pumpAndWaitForUserAction();
    }

    private void afterLinkProcessedEvent(QueueEntry qe, Link l) {
        queueConsole.render(sDoc ->
                queueConsole.renderQueue(sDoc, l.getThought(), qe)
        );

        if (!stepManager.stopHere(AFTER, LINK))
            return;

        DefaultStyledDocument sDoc = new DefaultStyledDocument();
        mainConsole.addStylesToDocument(sDoc);
        mainConsole.clear();
        mainConsole.renderLinkConsoleOutput(sDoc, l, "After " + Step.toString(qe.getStep()));
        mainConsole.setStyledDocument(sDoc);


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
    }

    @Override
    public void click(int x, int y) {
//        DefaultCamera2D camera = (DefaultCamera2D) getCamera();

//        Point3 guPoint = camera.transformPxToGuSwing(x, y);
    }

    public Document getDocument() {
        return doc;
    }

    public VisitorManager getVisitorManager() {
        return visitorManager;
    }
}
