package network.aika.visualization;

import network.aika.VisitorEventListener;
import network.aika.neuron.activation.Visitor;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;


public class VisitorManager implements VisitorEventListener {

    private boolean isRegistered = false;

    private ActivationViewerManager avm;

    private boolean clicked;


    public VisitorManager(ActivationViewerManager avm) {
        this.avm = avm;
    }


    public synchronized void click() {
        clicked = true;
        notifyAll();
    }

    private synchronized void waitForClick() {
        try {
            while(!clicked) {
                wait();
            }
            clicked = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setVisitorMode(boolean active) {
        if(active) {
            if(!isRegistered) {
                avm.getDocument().addVisitorEventListener(this);
                isRegistered = true;
            }
        } else {
            if(isRegistered) {
                avm.getDocument().removeVisitorEventListener(this);
                isRegistered = false;
            }
        }
    }

    @Override
    public void onVisitorEvent(Visitor v, boolean dir) {
        avm.getConsole().renderVisitorConsoleOutput(v, dir);
        GraphManager gm = avm.getGraphManager();

        switch(v.transition) {
            case ACT:
                System.out.println("Visitor ACT event: " + v.act.getLabel() + " " + dir);
                Node n = gm.getNode(v.act);
                if(n != null) {
                    if (!dir)
                        avm.highlightNode(n);
                    else
                        avm.unhighlightNode(n);
                }
                break;
            case LINK:
                System.out.println("Visitor LINK event: " + v.link.toString() + " " + dir);

                Edge e = gm.getEdge(v.link);
                if(e != null) {
                    if (!dir)
                        avm.highlightEdge(e);
                    else
                        avm.unhighlightEdge(e);
                }
                break;
        }

        avm.pump();

        waitForClick();
    }
}