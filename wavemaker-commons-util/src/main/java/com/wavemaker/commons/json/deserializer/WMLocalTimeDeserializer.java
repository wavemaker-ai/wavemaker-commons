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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.wavemaker.commons.MessageResource;
import com.wavemaker.commons.WMRuntimeException;

/**
 * Created by saddhamp on 11/7/16.
 */
public class WMLocalTimeDeserializer extends ValueDeserializer<LocalTime> {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        JsonToken currentToken = jsonParser.currentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            String value = jsonParser.getText();
            return getLocalDateTime(value);
        }
        throw new WMRuntimeException(MessageResource.create("com.wavemaker.commons.not.a.string.value"));
    }

    public static LocalTime getLocalDateTime(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return LocalTime.parse(value, dateTimeFormatter);
    }
}

