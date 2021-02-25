package network.aika.debugger;

import network.aika.Config;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.neuron.excitatory.PatternPartNeuron;
import network.aika.neuron.phase.Phase;
import network.aika.neuron.phase.activation.ActivationPhase;
import network.aika.neuron.phase.link.LinkPhase;
import network.aika.text.Document;
import network.aika.text.TextModel;
import network.aika.text.TextReference;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.TreeMap;

import static org.junit.platform.commons.util.StringUtils.isBlank;

public class PhraseTraining {


    private String trimPrefix(String l) {
        return l.substring(l.indexOf("-") + 1);
    }

    @Test
    public void trainPhrases() throws IOException {
        TextModel m = new TextModel();
        m.setConfig(
                new Config() {
                    public String getLabel(Activation act) {
                        Neuron n = act.getNeuron();
                        Activation iAct = act.getInputLinks()
                                .findFirst()
                                .map(l -> l.getInput())
                                .orElse(null);

                        if (n instanceof PatternPartNeuron) {
                            return "PP-" + trimPrefix(iAct.getLabel());
                        } else if (n instanceof PatternNeuron) {
                            return "P-" + ((Document) act.getThought()).getContent();
                        } else {
                            return "I-" + trimPrefix(iAct.getLabel());
                        }
                    }
                }
                        .setAlpha(0.99)
                        .setLearnRate(-0.1)
                        .setEnableTraining(true)
                        .setSurprisalInductionThreshold(0.0)
                        .setGradientInductionThreshold(0.0)
        );

        //    m.setN(912);

        for (int round = 0; round < 2; round++) {
            process(m, round);
        }
    }


    private void process(TextModel m, int round) throws IOException {
        Phase[] countingOnlyFilters = new Phase[]{
                ActivationPhase.TEMPLATE_OUTPUT,
                ActivationPhase.TEMPLATE_INPUT,
                ActivationPhase.ENTROPY_GRADIENT,
                ActivationPhase.PROPAGATE_GRADIENTS_NET,
                ActivationPhase.PROPAGATE_GRADIENTS_SUM,
                ActivationPhase.INDUCTION,
                ActivationPhase.UPDATE_BIAS,
                LinkPhase.TEMPLATE,
                LinkPhase.INDUCTION,
                LinkPhase.INFORMATION_GAIN_GRADIENT
        };
        Util.loadExamplePhrases("phrases.txt")
                .stream()
                .filter(p -> !isBlank(p))
                .forEach(p -> {
                            System.out.println(p);
                            Document doc = new Document(p);
                            if (round == 0) {
                                doc.addFilters(countingOnlyFilters);
                            } else {
                                AikaDebugger.createAndShowGUI(doc, m);
                            }

                            int i = 0;
                            TextReference lastRef = null;
                            for (String t : doc.getContent().split(" ")) {
                                if(!isBlank(t)) {
                                    int j = i + t.length();
                                    lastRef = doc.processToken(m, lastRef, i, j, t).getReference();

                                    i = j + 1;
                                }
                            }

                            doc.process(m);
                        }
                );
    }

}
