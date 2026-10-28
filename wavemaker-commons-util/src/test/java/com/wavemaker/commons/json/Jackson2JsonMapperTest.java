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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.json.JsonMapper;

public class Jackson2JsonMapperTest {

    @Test
    public void writesAcronymPropertiesAsJackson2Did() {
        ObjectMapper mapper = Jackson2JsonMapper.builder().build();

        assertEquals("{\"url\":\"u\",\"id\":1,\"name\":\"n\"}", mapper.writeValueAsString(new Sample()));
    }

    @Test
    public void readsAcronymPropertiesAsJackson2Did() {
        ObjectMapper mapper = Jackson2JsonMapper.builder().build();

        Sample sample = mapper.readValue("{\"url\":\"x\",\"id\":7}", Sample.class);

        assertEquals("x", sample.getURL());
        assertEquals(7, sample.getID());
    }

    @Test
    public void plainJackson2DefaultsDoNotKeepAcronymNames() {
        ObjectMapper mapper = JsonMapper.builderWithJackson2Defaults().build();

        String json = mapper.writeValueAsString(new Sample());

        assertTrue(json.contains("\"URL\":\"u\""));
        assertTrue(json.contains("\"ID\":1"));
    }

    @Test
    public void readsBuilderAcronymPropertiesAsJackson2Did() {
        ObjectMapper mapper = Jackson2JsonMapper.builder().build();

        assertEquals("x", mapper.readValue("{\"url\":\"x\"}", Built.class).url);
    }

    @JsonDeserialize(builder = Built.Builder.class)
    public static class Built {

        private final String url;

        private Built(String url) {
            this.url = url;
        }

        public static class Builder {

            private String url;

            public Builder withURL(String url) {
                this.url = url;
                return this;
            }

            public Built build() {
                return new Built(url);
            }
        }
    }

    public static class Sample {

        private String url = "u";

        private int id = 1;

        private String name = "n";

        public String getURL() {
            return url;
        }

        public void setURL(String url) {
            this.url = url;
        }

        public int getID() {
            return id;
        }

        public void setID(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }
    }
}
