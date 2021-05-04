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

import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.excitatory.*;
import network.aika.neuron.inhibitory.InhibitoryNeuron;
import network.aika.neuron.inhibitory.InhibitorySynapse;
import network.aika.neuron.inhibitory.PrimaryInhibitorySynapse;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.camera.Camera;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractViewManager<C extends AbstractConsole, G extends AbstractGraphManager> implements ViewerListener {

    protected Map<Class<? extends Neuron>, Consumer<Node>> neuronTypeModifiers = new HashMap<>();
    protected Map<Class<? extends Synapse>, BiConsumer<Edge, Synapse>> synapseTypeModifiers = new HashMap<>();

    protected Graph graph;

    protected G graphManager;

    protected SwingViewer viewer;

    protected ViewerPipe fromViewer;

    protected ViewPanel graphView;

    protected JSplitPane splitPane;

    protected C mainConsole;

    protected C selectedConsole;

    protected Element lastHighlighted;

    public AbstractViewManager(){
        initModifiers();

        graph = initGraph();
        viewer = new SwingViewer(new ThreadProxyPipe(graph));

        graphView = (DefaultView)viewer.addDefaultView(false, new SwingGraphRenderer());
        graphView.enableMouseOptions();

        MouseManager mouseManager = new MouseManager(this);
        graphView.setMouseManager(mouseManager);
        graphView.addMouseWheelListener(mouseManager);

        Camera camera = graphView.getCamera();

        camera.setAutoFitView(false);


        // The default action when closing the view is to quit
        // the program.
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        // We connect back the viewer to the graph,
        // the graph becomes a sink for the viewer.
        // We also install us as a viewer listener to
        // intercept the graphic events.
        fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);
    }

    public G getGraphManager() {
        return graphManager;
    }

    public abstract void showElementContext(GraphicElement ge);

    public Graph getGraph() {
        return graph;
    }

    public Camera getCamera() {
        return graphView.getCamera();
    }

    public JSplitPane getView() {
        return splitPane;
    }

    public C getMainConsole() {
        return mainConsole;
    }

    public C getSelectedConsole() {
        return selectedConsole;
    }

    protected JSplitPane initSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphView, getConsolePane());
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.7);

        return splitPane;
    }

    public abstract JComponent getConsolePane();

    private Graph initGraph() {
        //        System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display");

        Graph graph = new SingleGraph("0");

        graph.setAttribute("ui.stylesheet",
                "node {" +
                        "size: 20px;" +
//                  "fill-color: #777;" +
//                  "text-mode: hidden;" +
                        "z-index: 1;" +
//                  "shadow-mode: gradient-radial; shadow-width: 2px; shadow-color: #999, white; shadow-offset: 3px, -3px;" +
                        "stroke-mode: plain; " +
                        "stroke-width: 2px;" +
                        "text-size: 20px;" +
                        "text-alignment: under;" +
                        "text-color: black;" +
                        "text-style: bold;" +
                        "text-background-mode: rounded-box;" +
                        "text-background-color: rgba(100, 100, 100, 100); " +
                        "text-padding: 2px;" +
                        "text-offset: 0px, 2px;" +
                        "} " +
                        "node:selected {" +
                        "stroke-color: red; " +
                        "stroke-width: 4px;" +
                        "} " +
                        "edge {" +
                        "size: 2px;" +
                        "shape: cubic-curve;" +
                        "z-index: 0;" +
                        "arrow-size: 8px, 5px;" +
                        "} " +
                        "edge:selected {" +
                        "stroke-mode: plain; " +
                        "fill-color: red;" +
                        "stroke-width: 3px;" +
                        "}"
        );

        graph.setAttribute("ui.antialias");
        graph.setAutoCreate(true);

        return graph;
    }

    protected void initModifiers() {
        neuronTypeModifiers.put(PatternNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(0,130,0);"));
        neuronTypeModifiers.put(BindingNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(0,205,0);"));
        neuronTypeModifiers.put(InhibitoryNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(100,100,255);"));

        synapseTypeModifiers.put(InputBNSynapse.class, (e, s) -> e.setAttribute("ui.style", "fill-color: rgb(50,200,50);"));
        synapseTypeModifiers.put(NegativeBNSynapse.class, (e, s) -> e.setAttribute("ui.style", "fill-color: rgb(100,0,0);"));
        synapseTypeModifiers.put(SameBNSynapse.class, (e, s) -> {
            SameBNSynapse pps = (SameBNSynapse) s;
            if(pps.isRecurrent()) {
                e.setAttribute("ui.style", "fill-color: rgb(104,34,139);");
            } else {
                e.setAttribute("ui.style", "fill-color: rgb(20,170,20);");
            }
        });
        synapseTypeModifiers.put(InhibitorySynapse.class, (e, s) -> e.setAttribute("ui.style", "fill-color: rgb(50,50,150);"));
        synapseTypeModifiers.put(PrimaryInhibitorySynapse.class, (e, s) -> e.setAttribute("ui.style", "fill-color: rgb(0,00,100);"));
        synapseTypeModifiers.put(PatternSynapse.class, (e, s) -> e.setAttribute("ui.style", "fill-color: rgb(0,130,0);"));
    }


    public void pump() {
        fromViewer.pump();
        // fromViewer.blockingPump();
    }

    public void unhighlightElement(Element ge) {
        ge.removeAttribute("ui.selected");
        fromViewer.pump();
    }

    public void highlightElement(Element ge) {
        ge.setAttribute("ui.selected");
        fromViewer.pump();
    }

    public void viewClosed(String id) {
        //     loop = false;
    }

    public void buttonPushed(String id) {
        System.out.println("Button pushed on node "+id);
    }

    public void buttonReleased(String id) {
        System.out.println("Button released on node "+id);
    }

    public void mouseOver(String id) {
        System.out.println("Need the Mouse Options to be activated");
    }

    public void mouseLeft(String id) {
        System.out.println("Need the Mouse Options to be activated");
    }

    public abstract void click(int x, int y);

}
