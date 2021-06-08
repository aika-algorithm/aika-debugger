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


    private String trimPrefix(String l) {
        return l.substring(l.indexOf("-") + 1);
    }

    @Test
    public void testOpenModel() throws FileNotFoundException {

        FSSuspensionCallback fsCallback = new FSSuspensionCallback();

        TextModel m = new TextModel(fsCallback);
        fsCallback.open(new File("F:/Model"), "AIKA-236-1", false);

        Document doc = new Document("A B ");

        AikaDebugger.createAndShowGUI(doc,m);

        doc.process(m);

    }
}
