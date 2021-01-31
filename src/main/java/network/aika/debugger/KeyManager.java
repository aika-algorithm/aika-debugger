/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network.aika.debugger;

import network.aika.debugger.activations.ActivationViewManager;
import network.aika.debugger.activations.VisitorManager;

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
        if(e.getKeyChar() == 'm') {
            System.out.println("Metric: " + actViewManager.getCamera().getMetrics());
            return;
        }

        if(e.getKeyChar() == 'e') {
            actViewManager.setStopAfterProcessed(true);
        } else if(e.getKeyChar() == 'a' || e.getKeyChar() == 'l') {
            actViewManager.setLinkStepMode(e.getKeyChar() == 'l');
            if(visitorManager.isRegistered()) {
                visitorManager.setVisitorMode(false);
            }
        } else if(e.getKeyChar() == 'v') {
            actViewManager.setLinkStepMode(true);
            if(!visitorManager.isRegistered()) {
                visitorManager.setVisitorMode(true);
            }
        }

        actViewManager.click();
        visitorManager.click();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
