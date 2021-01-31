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

import network.aika.Utils;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.Visitor;
import network.aika.debugger.AbstractConsole;

import javax.swing.text.StyledDocument;


public class ActivationConsole extends AbstractConsole {


    public void renderActivationConsoleOutput(StyledDocument sDoc, Activation act, ActivationParticle ap) {
        appendText(sDoc, "Activation " + "\n\n", "headline");
        appendEntry(sDoc, "Id: ", "" + act.getId());
        appendEntry(sDoc, "Label: ", act.getLabel());
        appendEntry(sDoc, "Value: ", act.getValue() != null ? "" + Utils.round(act.getValue()) : "X");
        appendEntry(sDoc, "f(net)': ", "" + Utils.round(act.getActFunctionDerivative()));
        appendEntry(sDoc, "net[initial]: ", "" + Utils.round(act.getNet(false)));
        appendEntry(sDoc, "net[final]: ", "" + Utils.round(act.getNet(true)));
        appendEntry(sDoc, "Gradient: ", "" + Utils.round(act.getGradient()));
        appendEntry(sDoc, "Gradient Sum: ", "" + Utils.round(act.getGradientSum()));
        appendEntry(sDoc, "Branch-Probability: ", "" + Utils.round(act.getBranchProbability()));
        appendEntry(sDoc, "Fired: ", "" + act.getFired());
        appendEntry(sDoc, "Norm: ", "" + Utils.round(act.getNorm()));
        appendEntry(sDoc, "Reference: ", "" + act.getReference());
/*
// TODO: Remove Particle!
            if(ap != null) {
                appendText(sDoc, "X: " + ap.getPosition().x + " Y: " + ap.getPosition().y + "\n", "bold");
            }
*/
        appendText(sDoc, "\n\n\n", "regular");

        renderNeuronConsoleOutput(sDoc, act.getNeuron());
    }

    public void renderLinkConsoleOutput(StyledDocument sDoc, Link l) {
        appendText(sDoc, "Link" + "\n\n", "headline");

        Activation iAct = l.getInput();
        Activation oAct = l.getOutput();
        appendEntry(sDoc, "Input-Value: ", "" + Utils.round(l.getInputValue()));
        appendEntry(sDoc, "Output-Value: ", oAct.getValue() != null ? "" + Utils.round(oAct.getValue()) : "X");
        appendEntry(sDoc, "Output-net[initial]: ", "" + Utils.round(oAct.getNet(false)));
        appendEntry(sDoc, "Output-net[final]: ", "" + Utils.round(oAct.getNet(true)));

        appendEntry(sDoc, "IsSelfRef: ", "" + l.isSelfRef());
        appendEntry(sDoc, "Gradient: ", "" + Utils.round(l.getGradient()));
        appendEntry(sDoc, "f(net)': ", "" + Utils.round(oAct.getActFunctionDerivative()));
        appendEntry(sDoc, "f(net - (xi * wi))': ", "" + Utils.round(l.getActFunctionDerivative()));

        appendText(sDoc, "\n\n\n", "regular");

        renderSynapseConsoleOutput(sDoc, l.getSynapse());
    }

    public void renderVisitorConsoleOutput(StyledDocument sDoc, Visitor v, boolean dir) {
        appendText(sDoc, "Visitor " + (dir ? "(up)" : "(down)") + "\n\n", "headline");
        appendText(sDoc, "\n", "regular");
        appendEntry(sDoc, "Origin:", v.origin.act.toShortString());
        appendText(sDoc, "\n", "regular");

        do {
            renderVisitorStep(sDoc, v);

            appendText(sDoc, "\n", "regular");
            v = v.previousStep;
        } while(v != null);
    }

    public void renderVisitorStep(StyledDocument sDoc, Visitor v) {
        appendText(sDoc, v.transition.name() + "\n", "bold");

        if(v.act != null) {
            appendEntry(sDoc, "Current:", v.act.toShortString());
        } else if(v.link != null) {
            appendEntry(sDoc, "Current:", v.link.toString());
        }

        appendEntry(sDoc, "DownUp:", "" + v.downUpDir);
        appendEntry(sDoc, "StartDir:", "" + v.startDir);

        appendEntry(sDoc, "Scopes:", v.getScopes().toString());
        appendEntry(sDoc, "DownSteps:", "" + v.downSteps);
        appendEntry(sDoc, "UpSteps:", "" + v.upSteps);
    }
}
