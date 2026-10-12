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
package com.wavemaker.commons.json.module;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import com.wavemaker.tools.apidocs.tools.core.model.Swagger;

/**
 * @author <a href="mailto:dilip.gundu@wavemaker.com">Dilip Kumar</a>
 * @since 3/10/18
 */
public class WMJacksonModuleTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WMJacksonModuleTest.class);

    private ObjectMapper objectMapper;

    private A a;

    @BeforeEach
    public void init() {
        objectMapper = JsonMapper.builderWithJackson2Defaults().build();

        C c = new C("c value");

        B b = new B("b value", c);

        a = new A("a value", b);

        c.setA(a);
    }

    @Test
    public void test1() throws JacksonException {
        assertThrows(JacksonException.class,
            () -> objectMapper.writeValueAsString(a));
    }

    @Test
    public void test2() throws JacksonException {

        objectMapper = objectMapper.rebuild().addModule(new WMJacksonModule(false)).build();

        final String valueAsString = objectMapper.writeValueAsString(a);

        assertEquals("{\"value\":\"a value\",\"b\":{\"value\":\"b value\",\"c\":{\"value\":\"c value\"," +
            "\"a\":null}}}", valueAsString);

    }

    @Test
    public void test3() throws JacksonException {

        objectMapper = objectMapper.rebuild().addModule(new WMJacksonModule(true)).build();

        try {
            objectMapper.writeValueAsString(a);

            fail("Should throw cyclic reference issue.");

        } catch (JacksonException e) {
            assertEquals("Cyclic-reference leading to cycle, Object Reference Stack:A->B->C (through reference" +
                " chain: com.wavemaker.commons.json.module.A[\"b\"]->com.wavemaker.commons.json.module" +
                ".B[\"c\"]->com.wavemaker.commons.json.module.C[\"a\"])", e.getMessage());
        }

    }

    @Test
    public void test4() throws IOException {

        final Swagger swagger = objectMapper
            .readValue(this.getClass().getResourceAsStream("/hrdb_API.json"), Swagger.class);

        assertNotNull(swagger, "Could not get swagger object from json.");

        objectMapper = objectMapper.rebuild().addModule(new WMJacksonModule(true)).build();

        final String string = objectMapper.writeValueAsString(swagger);
        assertNotNull(string, "Could not convert swagger object to json.");

    }

}