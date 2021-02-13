package network.aika.debugger;

import static network.aika.debugger.StepManager.EventType.*;

public class StepManager {

    boolean stopAfterProcessed;

    EventType mode = ACT;

    protected boolean clicked;

    public enum When {
        NEW,
        BEFORE,
        AFTER
    }

    public enum EventType {
        ACT,
        LINK,
        VISITOR
    }

    public void setStopAfterProcessed(boolean b) {
        stopAfterProcessed = b;
    }

    public void setMode(EventType mode) {
        this.mode = mode;
    }

    public synchronized void click() {
        clicked = true;
        notifyAll();
    }

    public boolean stopHere(When w, EventType et) {
        if(mode == null)
            return false;

        if(w == When.AFTER && stopAfterProcessed)
            return true;

        if(mode == ACT && et == ACT)
            return true;

        if(mode == LINK && (et == ACT || et == LINK))
            return true;

        if(mode == VISITOR && (et == ACT || et == LINK || et == VISITOR))
            return true;

        return false;
    }

    public synchronized void waitForClick() {
        try {
            long waitBegin = System.currentTimeMillis();
            while(!clicked) {
                wait();
            }
            clicked = false;

            if(System.currentTimeMillis() - waitBegin > 1000) {
                if(mode == null) {
                    mode = VISITOR;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
