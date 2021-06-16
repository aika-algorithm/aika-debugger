package network.aika.debugger;

import network.aika.Config;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.neuron.excitatory.BindingNeuron;
import network.aika.text.Document;
import network.aika.text.TextModel;
import network.aika.text.TextReference;
import org.junit.jupiter.api.Test;


public class GradientTest {


    @Test
    public void gradientAndInduction() throws InterruptedException {
        TextModel m = new TextModel();
        Config c = new TestConfig()
                        .setAlpha(0.99)
                        .setLearnRate(-0.1)
                        .setEnableTraining(true);

        m.setN(912);

        Document doc = new Document("A B ");
        doc.setConfig(c);

        int i = 0;
        TextReference lastRef = null;
        for(String t: doc.getContent().split(" ")) {
            int j = i + t.length();
            lastRef = doc.processToken(m, lastRef, i, j, t).getReference();

            i = j + 1;
        }

        Neuron nA = m.getNeuron("A");
        nA.setFrequency(53.0);
        nA.getSampleSpace().setN(299);
        nA.getSampleSpace().setLastPos(899l);

        Neuron nB = m.getNeuron("B");
        nB.setFrequency(10.0);
        nB.getSampleSpace().setN(121);
        nB.getSampleSpace().setLastPos(739l);

        AikaDebugger.createAndShowGUI(doc,m);

        doc.process(m);


        System.out.println();
    }
}
