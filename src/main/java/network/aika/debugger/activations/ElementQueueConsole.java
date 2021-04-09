package network.aika.debugger.activations;

import network.aika.debugger.AbstractConsole;
import network.aika.neuron.activation.Element;
import network.aika.neuron.activation.QueueEntry;

import javax.swing.text.StyledDocument;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static network.aika.debugger.activations.QueueConsole.renderQueueEntry;

public class ElementQueueConsole extends AbstractConsole {

    public void renderElementQueueOutput(StyledDocument sDoc, Element e) {
        appendText(sDoc, "Queue\n", "headline");

        Stream<QueueEntry> elementQueue = e.getQueuedEntries();

        elementQueue = elementQueue.collect(
                Collectors.toCollection(() ->
                        new TreeSet<>(QueueEntry.COMPARATOR)
                )
        ).stream();

        elementQueue.forEach(qe ->
                renderQueueEntry(sDoc, qe, e.getThought().getTimestampOnProcess())
        );
    }
}
