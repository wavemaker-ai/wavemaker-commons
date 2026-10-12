/*******************************************************************************
 * Copyright (C) 2026 WaveMaker, Inc.
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
package com.wavemaker.commons.json.serializer;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class HttpHeadersSerializer extends ValueSerializer<HttpHeaders> {

    @Override
    public void serialize(HttpHeaders value, JsonGenerator jgen, SerializationContext provider) {
        jgen.writeStartObject(value);
        for (Map.Entry<String, List<String>> header : value.headerSet()) {
            jgen.writeName(header.getKey());
            List<String> headerValues = header.getValue();
            if (headerValues == null) {
                jgen.writeNull();
                continue;
            }
            jgen.writeStartArray();
            for (String headerValue : headerValues) {
                jgen.writeString(headerValue);
            }
            jgen.writeEndArray();
        }
        jgen.writeEndObject();
    }

    @Override
    public boolean isEmpty(SerializationContext provider, HttpHeaders value) {
        return value.isEmpty();
    }
}
