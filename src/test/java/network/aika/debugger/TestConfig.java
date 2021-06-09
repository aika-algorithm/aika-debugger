package network.aika.debugger;

import network.aika.Config;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.excitatory.BindingNeuron;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.text.Document;

public class TestConfig extends Config {


    private String trimPrefix(String l) {
        return l.substring(l.indexOf("-") + 1);
    }


    public String getLabel(Activation act) {
        Neuron n = act.getNeuron();
        Activation iAct = act.getInputLinks()
                .findFirst()
                .map(l -> l.getInput())
                .orElse(null);

        if(n instanceof BindingNeuron) {
            return "B-" + trimPrefix(iAct.getLabel());
        } else if (n instanceof PatternNeuron) {
            return "P-W-" + ((Document)act.getThought()).getContent();
        } else {
            return "I-" + trimPrefix(iAct.getLabel());
        }
    }
}
