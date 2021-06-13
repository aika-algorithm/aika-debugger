package network.aika.debugger;

import network.aika.Config;
import network.aika.callbacks.FSSuspensionCallback;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.excitatory.BindingNeuron;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.text.Document;
import network.aika.text.TextModel;
import network.aika.text.TextReference;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;


public class TestFsModel {

    @Test
    public void testOpenModel() throws FileNotFoundException {

        FSSuspensionCallback fsCallback = new FSSuspensionCallback();

        TextModel m = new TextModel();
        fsCallback.open(new File("F:/Model"), "AIKA-236-1", false);
        m.setSuspensionHook(fsCallback);

        Document doc = new Document("agile methoden ");

        Config c = new TestConfig()
                .setAlpha(0.99)
                .setLearnRate(-0.1)
                .setEnableTraining(true);
        doc.setConfig(c);

        int i = 0;
        TextReference lastRef = null;
        for(String t: doc.getContent().split(" ")) {
            int j = i + t.length();
            lastRef = doc.processToken(m, lastRef, i, j, "W-" + t).getReference();

            i = j + 1;
        }

        AikaDebugger.createAndShowGUI(doc,m);

        doc.process(m);

        System.out.println();
    }
}
