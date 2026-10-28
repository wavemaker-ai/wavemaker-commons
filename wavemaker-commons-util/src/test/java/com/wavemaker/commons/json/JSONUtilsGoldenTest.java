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
package com.wavemaker.commons.json;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.http.HttpHeaders;

/**
 * The golden files were produced by the Jackson 2 based JSONUtils (a plain ObjectMapper plus the java.sql.Date
 * serializer) and must stay byte for byte identical.
 */
public class JSONUtilsGoldenTest {

    @Test
    public void prettyOutputMatchesJackson2Golden() throws IOException {
        assertEquals(golden("jsonutils-golden-pretty.json"), normalize(JSONUtils.toJSON(fixture())));
    }

    @Test
    public void compactOutputMatchesJackson2Golden() throws IOException {
        assertEquals(golden("jsonutils-golden-compact.json"), JSONUtils.toJSON(fixture(), false));
    }

    @Test
    public void goldenReadsBackIntoTheFixture() throws IOException {
        JSONUtilsFixture fixture = JSONUtils.toObject(golden("jsonutils-golden-compact.json"), JSONUtilsFixture.class);

        assertEquals("https://example.com", fixture.getURL());
        assertEquals("id-1", fixture.getID());
        assertEquals("<a/>", fixture.getXMLData());
        assertEquals(JSONUtilsFixture.Kind.PLAIN, fixture.getPlainKind());
        assertEquals(JSONUtilsFixture.Kind.CUSTOM_TO_STRING, fixture.getCustomKind());
        assertEquals(1700000000000L, fixture.getCreatedOn().getTime());
        assertArrayEquals(new byte[]{1, 2, 3}, fixture.getPayload());
    }

    private JSONUtilsFixture fixture() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-Custom", "v1");
        headers.add("X-Custom", "v2");
        return JSONUtilsFixture.sample(headers);
    }

    private String normalize(String json) {
        return json.replace("\r\n", "\n");
    }

    private String golden(String name) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(name)) {
            assertNotNull(in, name);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
