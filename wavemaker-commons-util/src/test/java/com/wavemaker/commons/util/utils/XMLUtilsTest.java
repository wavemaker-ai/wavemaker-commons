/*******************************************************************************
 * Copyright (C) 2022-2023 WaveMaker, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wavemaker.commons.util.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.jupiter.api.Test;

import com.wavemaker.commons.util.XMLUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Saraswathi Rekhala on 19/10/15.
 */
public class XMLUtilsTest {

    @Test
    public void escapeTest() {
        assertEquals("&quot;sample&quot;", XMLUtils.escape("\"sample\""));
        assertEquals("&lt;sample&gt;", XMLUtils.escape("<sample>"));
        assertEquals("&amp;sample", XMLUtils.escape("&sample"));
        assertEquals("&apos;sample&apos;", XMLUtils.escape("\'sample\'"));
    }

    @Test
    public void attributesToMapReaderTest() throws FileNotFoundException, XMLStreamException {
        URL resource = XMLUtilsTest.class.getClassLoader().getResource("Customer.xml");
        String filePath = resource.getFile();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileReader(filePath));
        reader.next();
        Map<String, String> output = XMLUtils.attributesToMap(reader);
        assertEquals(1, output.size());
        Set<Map.Entry<String, String>> entries = output.entrySet();
        Map.Entry<String, String> entry = entries.iterator().next();
        assertEquals("id", entry.getKey());
        assertEquals("100", entry.getValue());
    }

    @Test
    public void attributesToMapScopeTest() throws IOException, XMLStreamException {
        URL resource = XMLUtilsTest.class.getClassLoader().getResource("Customer.xml");
        String filePath = resource.getFile();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileReader(filePath));
        reader.next();
        Map<String, String> output = XMLUtils.attributesToMap("xyz", reader);
        assertEquals(1, output.size());
        Set<Map.Entry<String, String>> entries = output.entrySet();
        Map.Entry<String, String> entry = entries.iterator().next();
        assertEquals("xyz.id", entry.getKey());
        assertEquals("100", entry.getValue());

    }

}
