package network.aika.visualization;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AikaKeyManager implements KeyListener {

    ActivationViewerManager actViewManager;

    public AikaKeyManager(ActivationViewerManager actViewManager) {
        this.actViewManager = actViewManager;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar() == ' ') {
            actViewManager.click();
        } else if(e.getKeyChar() == 'v') {
            System.out.println("Visitor");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
