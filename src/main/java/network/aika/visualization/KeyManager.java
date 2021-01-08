package network.aika.visualization;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {

    ActivationViewManager actViewManager;
    VisitorManager visitorManager;

    public KeyManager(ActivationViewManager actViewManager) {
        this.actViewManager = actViewManager;
        visitorManager = actViewManager.getVisitorManager();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar() == ' ') {
            if(visitorManager.isRegistered()) {
                visitorManager.setVisitorMode(false);
                visitorManager.click();
            } else {
                actViewManager.click();
            }
        } else if(e.getKeyChar() == 'v') {
            if(!visitorManager.isRegistered()) {
                visitorManager.setVisitorMode(true);
                actViewManager.click();
            } else {
                visitorManager.click();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
