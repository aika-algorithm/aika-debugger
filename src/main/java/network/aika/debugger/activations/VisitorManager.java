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

import network.aika.callbacks.VisitorEventListener;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.visitor.ActVisitor;
import network.aika.neuron.activation.visitor.LinkVisitor;
import network.aika.neuron.activation.visitor.Visitor;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import static network.aika.debugger.StepManager.EventType.VISITOR;
import static network.aika.debugger.StepManager.When.BEFORE;

public class VisitorManager implements VisitorEventListener {

    private ActivationViewManager avm;

    public VisitorManager(ActivationViewManager avm) {
        this.avm = avm;
        avm.getDocument().addVisitorEventListener(this);
    }

    @Override
    public void onVisitorEvent(Visitor v, boolean dir) {
        if(!avm.stepManager.stopHere(BEFORE, VISITOR))
            return;

        avm.getVisitorConsole().render(sDoc ->
                avm.getVisitorConsole().renderVisitorConsoleOutput(sDoc, v, dir)
        );

        ActivationGraphManager gm = avm.getGraphManager();

        if(v instanceof ActVisitor) {
            ActVisitor av = (ActVisitor) v;
            Node n = gm.getNode(av.getActivation());
            if(n != null) {
                if (!dir)
                    avm.highlightElement(n);
                else
                    avm.unhighlightElement(n);
            }
        } else if(v instanceof LinkVisitor) {
            LinkVisitor lv = (LinkVisitor) v;
            Edge e = gm.getEdge(lv.getLink());
            if(e != null) {
                if (!dir)
                    avm.highlightElement(e);
                else
                    avm.unhighlightElement(e);
            }
        }

        avm.pump();

        avm.stepManager.waitForClick();
    }
}
