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
package network.aika.debugger.neurons;


import network.aika.debugger.AbstractConsole;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Reference;
import network.aika.neuron.excitatory.BindingNeuronSynapse;
import network.aika.neuron.sign.Sign;
import network.aika.utils.Utils;

import javax.swing.text.StyledDocument;

public class NeuronConsole extends AbstractConsole {


    public void renderNeuronConsoleOutput(StyledDocument sDoc, Neuron n, Reference ref) {
        appendText(sDoc, "Neuron\n", "headline");

        appendEntry(sDoc, "Id: ", "" + n.getId());
        appendEntry(sDoc, "Label: ", n.getLabel());
        appendEntry(sDoc, "Type: ", n.getClass().getSimpleName());
        appendEntry(sDoc, "Is Input Neuron: ", "" + n.isInputNeuron());
        appendEntry(sDoc, "Bias: ", "" + Utils.round(n.getBias()));
        appendEntry(sDoc, "Bias (recurrent): ", "" + Utils.round(n.getRecurrentBias()));

        if(!n.isTemplate()) {
            appendEntry(sDoc, "Frequency: ", "" + Utils.round(n.getFrequency()));
            appendEntry(sDoc, "N: ", "" + Utils.round(n.getSampleSpace().getN(ref)));
            appendEntry(sDoc, "LastPos: ", "" + (n.getSampleSpace().getLastPos() != null ? Utils.round(n.getSampleSpace().getLastPos()) : "X"));
            appendEntry(sDoc, "P(POS): ", "" + Utils.round(n.getP(Sign.POS, n.getSampleSpace().getN(ref))));
            appendEntry(sDoc, "P(NEG): ", "" + Utils.round(n.getP(Sign.NEG, n.getSampleSpace().getN(ref))));
            appendEntry(sDoc, "Surprisal(POS): ", "" + Utils.round(n.getSurprisal(Sign.POS, ref)));
            appendEntry(sDoc, "Surprisal(NEG): ", "" + Utils.round(n.getSurprisal(Sign.NEG, ref)));
            appendEntry(sDoc, "Template Neuron: ", templatesToString(n));
        }
    }

    private String templatesToString(Neuron<?> n) {
        StringBuilder sb = new StringBuilder();
        n.getTemplateGroup().forEach(tn -> sb.append(tn.getId() + ":" + tn.getLabel() + ", "));
        return sb.toString();
    }

    public void renderSynapseConsoleOutput(StyledDocument sDoc, Synapse s, Reference ref) {
        appendText(sDoc, "Synapse\n", "headline");

        appendEntry(sDoc, "Type: ", s.getClass().getSimpleName());
        appendEntry(sDoc, "Weight: ", "" + Utils.round(s.getWeight()));
        appendEntry(sDoc, "Input: ", s.getInput().toString());
        appendEntry(sDoc, "Output: ", s.getOutput().toString());
        if(s instanceof BindingNeuronSynapse) {
            BindingNeuronSynapse pps = (BindingNeuronSynapse) s;

            appendEntry(sDoc, "Recurrent: ", "" + pps.isRecurrent());
        }

        if(!s.isTemplate()) {
            appendEntry(sDoc, "Frequency(POS, POS): ", "" + Utils.round(s.getFrequency(Sign.POS, Sign.POS, s.getSampleSpace().getN(ref))));
            appendEntry(sDoc, "Frequency(POS, NEG): ", "" + Utils.round(s.getFrequency(Sign.POS, Sign.NEG, s.getSampleSpace().getN(ref))));
            appendEntry(sDoc, "Frequency(NEG, POS): ", "" + Utils.round(s.getFrequency(Sign.NEG, Sign.POS, s.getSampleSpace().getN(ref))));
            appendEntry(sDoc, "Frequency(NEG, NEG): ", "" + Utils.round(s.getFrequency(Sign.NEG, Sign.NEG, s.getSampleSpace().getN(ref))));
            appendEntry(sDoc, "N: ", "" + Utils.round(s.getSampleSpace().getN(ref)));
            appendEntry(sDoc, "LastPos: ", "" + (s.getSampleSpace().getLastPos() != null ? Utils.round(s.getSampleSpace().getLastPos()) : "X"));
            appendEntry(sDoc, "P(POS, POS) :", "" + Utils.round(s.getP(Sign.POS, Sign.POS, s.getSampleSpace().getN(ref))));
            appendEntry(sDoc, "P(POS, NEG) :", "" + Utils.round(s.getP(Sign.POS, Sign.NEG, s.getSampleSpace().getN(ref))));
            appendEntry(sDoc, "P(NEG, POS) :", "" + Utils.round(s.getP(Sign.NEG, Sign.POS, s.getSampleSpace().getN(ref))));
            appendEntry(sDoc, "P(NEG, NEG) :", "" + Utils.round(s.getP(Sign.NEG, Sign.NEG, s.getSampleSpace().getN(ref))));
            appendEntry(sDoc, "Surprisal(POS, POS): ", "" + Utils.round(s.getSurprisal(Sign.POS, Sign.POS, ref)));
            appendEntry(sDoc, "Surprisal(POS, NEG): ", "" + Utils.round(s.getSurprisal(Sign.POS, Sign.NEG, ref)));
            appendEntry(sDoc, "Surprisal(NEG, POS): ", "" + Utils.round(s.getSurprisal(Sign.NEG, Sign.POS, ref)));
            appendEntry(sDoc, "Surprisal(NEG, NEG): ", "" + Utils.round(s.getSurprisal(Sign.NEG, Sign.NEG, ref)));
            appendEntry(sDoc, "Template: ", s.getTemplate().toString());
        }
    }
}
