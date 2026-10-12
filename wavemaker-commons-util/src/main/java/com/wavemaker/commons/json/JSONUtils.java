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
package com.wavemaker.commons.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ext.sql.JavaSqlDateSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.TypeFactory;
import com.wavemaker.commons.MessageResource;
import com.wavemaker.commons.WMRuntimeException;
import com.wavemaker.commons.json.deserializer.HttpHeadersDeSerializer;
import com.wavemaker.commons.json.serializer.HttpHeadersSerializer;
import com.wavemaker.commons.util.WMIOUtils;

/**
 * Created by venuj on 19-05-2014.
 */
public class JSONUtils {

    private static volatile ObjectMapper objectMapper;

    static {
        final SimpleModule module = new SimpleModule();
        module.addSerializer(Date.class,
            new JavaSqlDateSerializer().withFormat(false, new SimpleDateFormat("yyyy-MM-dd"))
        );
        module.addSerializer(HttpHeaders.class, new HttpHeadersSerializer());
        module.addDeserializer(HttpHeaders.class, new HttpHeadersDeSerializer());
        objectMapper = JsonMapper.builderWithJackson2Defaults()
            .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .accessorNaming(new Jackson2AccessorNamingStrategy.Provider())
            .typeFactory(TypeFactory.createDefaultInstance().withClassLoader(JSONUtils.class.getClassLoader()))
            .addModule(module)
            .build();
    }

    private JSONUtils() {
    }

    public static String prettifyJSON(String data) {
        if (StringUtils.isNotBlank(data)) {
            JsonNode tree = objectMapper.readTree(data);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree);
        }
        return data;
    }

    public static void prettifyJSON(String data, File outputFile) throws IOException {
        if (data == null) {
            throw new WMRuntimeException(MessageResource.create("com.wavemaker.commons.null.json.prettify.error"));
        }
        String formattedJson = prettifyJSON(data);
        WMIOUtils.write(outputFile, formattedJson);
    }

    public static String toJSON(Object object) {
        try {
            return toJSON(object, true);
        } catch (JacksonException e) {
            throw new WMRuntimeException(MessageResource.create("com.wavemaker.commons.objToJsonConversionFailed"), e);
        }
    }

    public static String toJSON(Object object, boolean prettify) {
        return prettify ? objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object) : objectMapper
            .writeValueAsString(object);
    }

    public static void toJSON(File outputFile, Object object) {
        toJSON(outputFile, object, true);
    }

    public static void toJSON(File outputFile, Object object, boolean prettify) {
        if (prettify) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, object);
        } else {
            objectMapper.writeValue(outputFile, object);
        }
    }

    public static void toJSON(OutputStream outputStream, Object object) {
        toJSON(outputStream, object, true);
    }

    public static void toJSON(OutputStream outputStream, Object object, boolean prettify) {
        try {
            if (prettify) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, object);
            } else {
                objectMapper.writeValue(outputStream, object);
            }
        } finally {
            WMIOUtils.closeSilently(outputStream);
        }
    }

    public static void toJSON(Writer outputWriter, Object object) {
        toJSON(outputWriter, object, true);
    }

    public static void toJSON(Writer outputWriter, Object object, boolean prettify) {
        try {
            if (prettify) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputWriter, object);
            } else {
                objectMapper.writeValue(outputWriter, object);
            }
        } finally {
            WMIOUtils.closeSilently(outputWriter);
        }
    }

    public static <T> T toObject(String jsonString, Class<T> t) {
        return objectMapper.readValue(jsonString, t);
    }

    public static <T> T toObject(Reader jsonReader, Class<T> t) {
        try {
            return objectMapper.readValue(jsonReader, t);
        } finally {
            WMIOUtils.closeSilently(jsonReader);
        }
    }

    public static <T> T toObject(InputStream jsonStream, Class<T> t) {
        try {
            return objectMapper.readValue(jsonStream, t);
        } finally {
            WMIOUtils.closeSilently(jsonStream);
        }
    }

    public static <T> T toObject(File file, Class<T> targetClass) {
        return objectMapper.readValue(file, targetClass);
    }

    public static <T> T toObject(File file, JavaType javaType) {
        return objectMapper.readValue(file, javaType);
    }

    public static <T> T toObject(String jsonString, TypeReference<T> valueTypeRef) {
        return objectMapper.readValue(jsonString, valueTypeRef);
    }

    public static <T> T toObject(InputStream inputStream, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(inputStream, typeReference);
        } finally {
            WMIOUtils.closeSilently(inputStream);
        }
    }

    public static <T> T toObject(Reader reader, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(reader, typeReference);
        } finally {
            WMIOUtils.closeSilently(reader);
        }
    }

    public static <T> T convert(Object object, Class<T> targetClass) {
        return objectMapper.convertValue(object, targetClass);
    }

    public static synchronized void registerModule(JacksonModule module) {
        boolean registered = objectMapper.registeredModules().stream()
            .anyMatch(registeredModule -> registeredModule.getRegistrationId().equals(module.getRegistrationId()));
        if (!registered) {
            objectMapper = objectMapper.rebuild().addModule(module).build();
        }
    }

    public static JsonNode readTree(InputStream inputStream) {
        try {
            return objectMapper.readTree(inputStream);
        } catch (JacksonException e) {
            throw new WMRuntimeException(MessageResource.create("com.wavemaker.commons.jsonData.parse.failed"), e);
        } finally {
            WMIOUtils.closeSilently(inputStream);
        }
    }

    public static JsonNode readTree(String str) {
        try {
            return objectMapper.readTree(str);
        } catch (JacksonException e) {
            throw new WMRuntimeException(MessageResource.create("com.wavemaker.commons.jsonData.parse.failed"), e);
        }
    }

    public static Object toObject(String value, Type genericType) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return objectMapper.readValue(
                value,
                objectMapper.getTypeFactory().constructType(genericType)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
