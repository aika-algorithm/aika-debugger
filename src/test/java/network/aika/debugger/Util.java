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

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lukas Molzberger
 */
public class Util {

    public static List<String> loadExamplePhrases(String file) throws IOException {
        ArrayList<String> phrases = new ArrayList<>();
        StringWriter writer = new StringWriter();
        try (InputStream is = Util.class.getResourceAsStream(file)) {
            IOUtils.copy(is, writer, "UTF-8");
            String txt = writer.toString();

            for(String phrase: txt.split("\n")) {
                phrase = phrase.replaceAll("\r", "");
                phrase = phrase.replaceAll("\\.", "");
                phrase = phrase.replaceAll(",", "");
                phrase = phrase.replaceAll("!", "");
                phrase = phrase.replaceAll("\\?", "");
                phrase = phrase.replaceAll("-", "");
                phrases.add(phrase);
            }
        }
        return phrases;
    }
}
