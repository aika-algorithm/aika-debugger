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

import static network.aika.debugger.StepManager.EventType.*;

public class KeyManager implements KeyListener {

    ActivationViewManager actViewManager;

    public KeyManager(ActivationViewManager actViewManager) {
        this.actViewManager = actViewManager;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        StepManager sm = actViewManager.getStepManager();

        if(e.getKeyChar() == 'm') {
            System.out.println("Metric: " + actViewManager.getCamera().getMetrics());
            return;
        }

        if(e.getKeyChar() == 'e') {
            sm.setStopAfterProcessed(true);
        } else if(e.getKeyChar() == 'r') {
            sm.setMode(null);
            sm.lastTimestamp = null;
        } else if(e.getKeyChar() == 'a') {
            sm.setMode(ACT);
        } else if(e.getKeyChar() == 'l') {
            sm.setMode(LINK);
        } else if(e.getKeyChar() == 'v') {
            actViewManager.getActivationViewTabbedPane().setSelectedIndex(2);

            sm.setMode(VISITOR);
        }

        sm.click();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
