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

import network.aika.debugger.neurons.NeuronConsole;
import network.aika.neuron.activation.*;
import network.aika.utils.Utils;
import network.aika.debugger.AbstractConsole;
import network.aika.neuron.sign.Sign;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class ActivationConsole extends AbstractConsole {

    private NeuronConsole neuronConsole = new NeuronConsole();
    private ElementQueueConsole elementQueueConsole = new ElementQueueConsole();


    public Component getSplitPane() {
        JSplitPane innerSP = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this, neuronConsole);
        innerSP.setResizeWeight(0.5);

        JSplitPane outerSP = new JSplitPane(JSplitPane.VERTICAL_SPLIT, innerSP, elementQueueConsole);
        outerSP.setResizeWeight(0.70);

        return outerSP;
    }

    public void renderActivationConsoleOutput(StyledDocument sDoc, Activation act, String headline) {
        if(headline != null)
            addHeadline(sDoc, headline);

        appendText(sDoc, "Activation " + "\n", "headline");
        appendEntry(sDoc, "Id: ", "" + act.getId());
        appendEntry(sDoc, "Label: ", act.getLabel());
        appendEntry(sDoc, "Value: ", act.getValue() != null ? "" + Utils.round(act.getValue()) : "X");
        appendEntry(sDoc, "net: ", "" + Utils.round(act.getNet()));
        appendEntry(sDoc, "f(net)': ", "" + Utils.round(act.getNeuron().getActivationFunction().outerGrad(act.getNet())));
        appendEntry(sDoc, "Input-Gradient: ", "" + Utils.round(act.getInputGradient()));
        appendEntry(sDoc, "Output-Gradient-Sum: ", "" + Utils.round(act.getOutputGradientSum()));
        appendEntry(sDoc, "Branch-Probability: ", "" + Utils.round(act.getBranchProbability()));
        appendEntry(sDoc, "Fired: ", "" + act.getFired());
        if(!act.getNeuron().isTemplate()) {
            appendEntry(sDoc, "Norm: ", "" + Utils.round(act.getNorm()));
        }
        appendEntry(sDoc, "Reference: ", "" + act.getReference());

        neuronConsole.render(nsDoc ->
                neuronConsole.renderNeuronConsoleOutput(nsDoc, act.getNeuron(), act.getReference())
        );

        elementQueueConsole.render(eqsDoc ->
                elementQueueConsole.renderElementQueueOutput(eqsDoc, act)
        );
    }

    public void renderLinkConsoleOutput(StyledDocument sDoc, Link l, String headline) {
        if(headline != null)
            addHeadline(sDoc, headline);

        appendText(sDoc, "Link\n", "headline");

        Activation oAct = l.getOutput();
        appendEntry(sDoc, "Input: ", l.getInput().toShortString());
        appendEntry(sDoc, "Input-Value: ", "" + Utils.round(l.getInputValue(Sign.POS)));
        appendEntry(sDoc, "Output: ", l.getOutput().toShortString());
        appendEntry(sDoc, "Output-Value: ", oAct.getValue() != null ? "" + Utils.round(oAct.getValue()) : "X");
        appendEntry(sDoc, "Output-net: ", "" + Utils.round(oAct.getNet()));

        appendEntry(sDoc, "f(net)': ", "" + Utils.round(oAct.getNeuron().getActivationFunction().outerGrad(oAct.getNet())));

        appendText(sDoc, "\n", "regular");

        neuronConsole.render(nsDoc ->
                neuronConsole.renderSynapseConsoleOutput(nsDoc, l.getSynapse(), l.getOutput().getReference())
        );

        elementQueueConsole.render(eqsDoc ->
                elementQueueConsole.renderElementQueueOutput(eqsDoc, l)
        );
    }
}
