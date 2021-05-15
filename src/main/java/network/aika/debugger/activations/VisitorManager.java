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

import network.aika.callbacks.VisitorEvent;
import network.aika.callbacks.VisitorEventListener;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.visitor.ActVisitor;
import network.aika.neuron.activation.visitor.LinkVisitor;
import network.aika.neuron.activation.visitor.Visitor;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import static network.aika.debugger.StepManager.EventType.VISITOR;
import static network.aika.debugger.StepManager.When.AFTER;
import static network.aika.debugger.StepManager.When.BEFORE;

public class VisitorManager implements VisitorEventListener {

    private ActivationViewManager avm;

    private int visitorHighlightedCounter = 0;

    public VisitorManager(ActivationViewManager avm) {
        this.avm = avm;
        avm.getDocument().addVisitorEventListener(this);
    }

    public boolean isVisitorHighlighted() {
        return visitorHighlightedCounter > 0;
    }

    @Override
    public void onVisitorEvent(Visitor v, VisitorEvent ve) {
        if(!avm.stepManager.stopHere(BEFORE, VISITOR)) {

            if(isVisitorHighlighted()) {
                updateHighlighted(v, ve);
            }
            return;
        }

        avm.getVisitorConsole().render(sDoc ->
                avm.getVisitorConsole().renderVisitorConsoleOutput(sDoc, v, ve, null, false)
        );

        updateHighlighted(v, ve);

        avm.pump();

        avm.stepManager.waitForClick();
    }

    private void updateHighlighted(Visitor v, VisitorEvent ve) {
        ActivationGraphManager gm = avm.getGraphManager();

        if(v instanceof ActVisitor) {
            highlightActivation((ActVisitor) v, ve, gm);
        } else if(v instanceof LinkVisitor) {
            highlightLink((LinkVisitor) v, ve, gm);
        }
    }

    @Override
    public void onVisitorCandidateEvent(Visitor v, Synapse s) {
        if(!avm.stepManager.stopHere(BEFORE, VISITOR))
            return;

        avm.getVisitorConsole().render(sDoc ->
            avm.getVisitorConsole().renderVisitorConsoleOutput(sDoc, v, null, s, true)
        );

        if(!avm.stepManager.stopHere(AFTER, VISITOR))
            return;

        avm.pump();

        avm.stepManager.waitForClick();
    }


    private void highlightActivation(ActVisitor v, VisitorEvent ve, ActivationGraphManager gm) {
        ActVisitor av = v;
        Node n = gm.getNode(av.getActivation());
        if(n != null) {
            if (ve == VisitorEvent.BEFORE) {
                avm.highlightElement(n);
                visitorHighlightedCounter++;
            } else {
                avm.unhighlightElement(n);
                visitorHighlightedCounter--;
            }
        }
    }

    private void highlightLink(LinkVisitor v, VisitorEvent ve, ActivationGraphManager gm) {
        LinkVisitor lv = v;
        Link l = lv.getLink();
        if(l != null) {
            Edge e = gm.getEdge(l);
            if (e != null) {
                if (ve == VisitorEvent.BEFORE) {
                    avm.highlightElement(e);
                    visitorHighlightedCounter++;
                } else {
                    avm.unhighlightElement(e);
                    visitorHighlightedCounter--;
                }
            }
        }
    }

}
