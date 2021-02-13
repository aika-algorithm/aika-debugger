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

import network.aika.debugger.activations.VisitorManager;

import static network.aika.debugger.StepManager.EventType.*;

public class StepManager {

    boolean stopAfterProcessed;

    EventType mode = ACT;

    Long lastTimestamp = null;

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


    private VisitorManager visitorManager;


    public StepManager(VisitorManager visitorManager) {
        this.visitorManager = visitorManager;
    }

    public void setStopAfterProcessed(boolean b) {
        stopAfterProcessed = b;
    }

    public void setMode(EventType mode) {
        this.mode = mode;

        if(mode == ACT || mode == LINK) {
            if(visitorManager.isRegistered()) {
                visitorManager.setVisitorMode(false);
            }
        } else if(mode == VISITOR) {
            if(!visitorManager.isRegistered()) {
                visitorManager.setVisitorMode(true);
            }
        }
    }

    public synchronized void click() {
        clicked = true;
        notifyAll();
    }

    public boolean stopHere(When w, EventType et) {
        if(mode == null) {
            if(lastTimestamp != null && System.currentTimeMillis() - lastTimestamp > 1000) {
                if(mode == null) {
                    setMode(VISITOR);
                }
            } else {
                lastTimestamp = System.currentTimeMillis();
                return false;
            }
        }

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
            while(!clicked) {
                wait();
            }
            clicked = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
