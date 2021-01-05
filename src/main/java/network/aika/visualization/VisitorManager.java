package network.aika.visualization;

import network.aika.VisitorEventListener;
import network.aika.neuron.activation.Visitor;


public class VisitorManager implements VisitorEventListener {

    private ActivationViewerManager avm;


    public VisitorManager(ActivationViewerManager avm) {
        this.avm = avm;
    }

    public void setVisitorMode(boolean active) {
        if(active) {
            avm.getDocument().addVisitorEventListener(this);
        } else {
            avm.getDocument().removeVisitorEventListener(this);
        }
    }

    @Override
    public void onVisitorEvent(Visitor v) {
        System.out.println("Visitor event");

        avm.pumpAndWaitForUserAction();
    }
}
