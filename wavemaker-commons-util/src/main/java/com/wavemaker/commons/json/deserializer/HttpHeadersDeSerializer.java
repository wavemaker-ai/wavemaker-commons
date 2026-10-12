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
package com.wavemaker.commons.json.deserializer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;

import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.node.ObjectNode;

/**
 * @author Uday Shankar
 */
public class HttpHeadersDeSerializer extends ValueDeserializer<HttpHeaders> {

    @Override
    public HttpHeaders deserialize(JsonParser jp, DeserializationContext ctxt) {
        ObjectNode root = (ObjectNode) ctxt.readTree(jp);
        Map<String, Object> map = ctxt.readTreeAsValue(root, ctxt.getTypeFactory().constructType(new TypeReference<LinkedHashMap<String, Object>>() {
        }));
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                httpHeaders.add(entry.getKey(), (String) entry.getValue());
            } else if (value instanceof List) {
                httpHeaders.put(entry.getKey(), (List<String>) value);
            }
        }
        return httpHeaders;
    }
}
