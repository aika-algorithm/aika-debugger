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

import network.aika.neuron.activation.Element;
import network.aika.utils.Utils;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.Visitor;
import network.aika.debugger.AbstractConsole;
import network.aika.neuron.sign.Sign;

import javax.swing.text.StyledDocument;

import static network.aika.neuron.activation.RoundType.*;


public class ActivationConsole extends AbstractConsole {


    public void renderActivationConsoleOutput(StyledDocument sDoc, Activation act, ActivationParticle ap) {
        appendText(sDoc, "Activation " + "\n\n", "headline");
        appendEntry(sDoc, "Id: ", "" + act.getId());
        appendEntry(sDoc, "Label: ", act.getLabel());
        appendEntry(sDoc, "Round(ACT): ", getRoundStr(act.getRound(ACT)));
        appendEntry(sDoc, "Round(GRADIENT): ", getRoundStr(act.getRound(GRADIENT)));
        appendEntry(sDoc, "Round(WEIGHT): ", getRoundStr(act.getRound(WEIGHT)));
        appendEntry(sDoc, "Value: ", act.getValue() != null ? "" + Utils.round(act.getValue()) : "X");
        appendEntry(sDoc, "net[initial]: ", "" + Utils.round(act.getNet(false)));
        appendEntry(sDoc, "net[final]: ", "" + Utils.round(act.getNet(true)));
        appendEntry(sDoc, "f(net)': ", "" + Utils.round(act.getNeuron().getActivationFunction().outerGrad(act.getNet(true))));
        appendEntry(sDoc, "Input-Gradient: ", "" + Utils.round(act.getInputGradient()));
        appendEntry(sDoc, "Output-Gradient-Sum: ", "" + Utils.round(act.getOutputGradientSum()));
        appendEntry(sDoc, "Branch-Probability: ", "" + Utils.round(act.getBranchProbability()));
        appendEntry(sDoc, "Fired: ", "" + act.getFired());
        if(!act.getNeuron().isTemplate()) {
            appendEntry(sDoc, "Norm: ", "" + Utils.round(act.getNorm()));
        }
        appendEntry(sDoc, "Reference: ", "" + act.getReference());
/*
// TODO: Remove Particle!
            if(ap != null) {
                appendText(sDoc, "X: " + ap.getPosition().x + " Y: " + ap.getPosition().y + "\n", "bold");
            }
*/
        appendText(sDoc, "\n\n\n", "regular");

        renderNeuronConsoleOutput(sDoc, act.getNeuron(), act.getReference());
    }

    public void renderLinkConsoleOutput(StyledDocument sDoc, Link l) {
        appendText(sDoc, "Link" + "\n\n", "headline");

        Activation oAct = l.getOutput();
        appendEntry(sDoc, "Input: ", l.getInput().toShortString());
        appendEntry(sDoc, "Input-Value: ", "" + Utils.round(l.getInputValue(Sign.POS)));
        appendEntry(sDoc, "Output: ", l.getOutput().toShortString());
        appendEntry(sDoc, "Output-Value: ", oAct.getValue() != null ? "" + Utils.round(oAct.getValue()) : "X");
        appendEntry(sDoc, "Output-net[initial]: ", "" + Utils.round(oAct.getNet(false)));
        appendEntry(sDoc, "Output-net[final]: ", "" + Utils.round(oAct.getNet(true)));

        appendEntry(sDoc, "Round(ACT): ", getRoundStr(l.getRound(ACT)));
        appendEntry(sDoc, "Round(GRADIENT): ", getRoundStr(l.getRound(GRADIENT)));
        appendEntry(sDoc, "Round(WEIGHT): ", getRoundStr(l.getRound(WEIGHT)));        appendEntry(sDoc, "IsSelfRef: ", "" + l.isSelfRef());
        appendEntry(sDoc, "Gradient: ", "" + Utils.round(l.getGradient()));
        appendEntry(sDoc, "f(net)': ", "" + Utils.round(oAct.getNeuron().getActivationFunction().outerGrad(oAct.getNet(true))));

        appendText(sDoc, "\n\n\n", "regular");

        renderSynapseConsoleOutput(sDoc, l.getSynapse(), l.getInput().getReference());
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
