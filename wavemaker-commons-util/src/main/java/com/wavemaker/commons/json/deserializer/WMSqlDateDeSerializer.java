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

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.wavemaker.commons.MessageResource;
import com.wavemaker.commons.WMRuntimeException;

/**
 * can deserialize java.sql.date objects represented in the format "yyyy-MM-dd".
 *
 * @author Uday Shankar
 */
public class WMSqlDateDeSerializer extends ValueDeserializer<Date> {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        JsonToken currentToken = jsonParser.currentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            String value = jsonParser.getText();
            return getDate(value);
        }
        throw new WMRuntimeException(MessageResource.create("com.wavemaker.commons.failed.to.read.token.as.java.sql.Date"));
    }

    public static Date getDate(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            java.util.Date parsedDate = new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(value);
            return new Date(parsedDate.getTime());
        } catch (ParseException e) {
            throw new WMRuntimeException(MessageResource.create("com.wavemaker.commons.string.to.date.parse.failure"), e, value, "java.sql.Date");
        }
    }
}
