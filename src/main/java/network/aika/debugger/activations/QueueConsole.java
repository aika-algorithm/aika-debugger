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
package network.aika.debugger.activations;

import network.aika.Thought;
import network.aika.neuron.activation.QueueEntry;
import network.aika.debugger.AbstractConsole;

import javax.swing.text.StyledDocument;


public class QueueConsole extends AbstractConsole {

    public void renderQueue(StyledDocument sDoc, Thought t, QueueEntry currentQE) {
        renderQueueEntry(sDoc, currentQE, t.getTimestampOnProcess());
        appendText(sDoc, "---------------------------------------------------------------------------------------------------------------------------------------------------------------------\n", "regular");
        for(QueueEntry qe: t.getQueue()) {
            renderQueueEntry(sDoc, qe, t.getTimestampOnProcess());
        }

        appendText(sDoc, "\n\n\n", "regular");
    }

    public static void renderQueueEntry(StyledDocument sDoc, QueueEntry qe, long currentTimestamp) {
        boolean isGreen = currentTimestamp <= qe.getTimestamp();
        appendEntry(
                sDoc,
                getQueueEntrySortKeyDescription(qe),
                qe.getElement().toShortString(),
                isGreen ? "boldGreen" : "bold",
                isGreen ? "regularGreen" : "regular"
        );
    }

    public static String getQueueEntrySortKeyDescription(QueueEntry qe) {
        return qe.getStep().getPhase().name() + "-" + qe.getFired() + "-" + qe.getTimestamp() + " " + qe.getStep() + " ";
    }
}
