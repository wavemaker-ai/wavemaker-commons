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
package com.wavemaker.commons.web.filter;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Uday Shankar
 */

public class ServerTimingMetricTest {

    static Stream<Arguments> testDataProvider() {
        return Stream.of(
            Arguments.of("cache;desc=\"Cache Read\";dur=23", "cache", 23L, "Cache Read", null, "cache(Cache Read);dur=23"),
            Arguments.of("cache;desc=Cache Read;dur=23", "cache", 23L, "Cache Read", null, "cache(Cache Read);dur=23"),
            Arguments.of("cache;dur=23", "cache", 23L, null, null, "cache;dur=23"),
            Arguments.of("cache;desc=Cache Read", "cache", null, "Cache Read", null, "cache(Cache Read)"),
            Arguments.of("cache;desc=\"", "cache", null, "Cache Read", IllegalArgumentException.class, null),
            Arguments.of("cache", "cache", null, null, null, "cache"),
            Arguments.of(" cache", "cache", null, null, null, "cache"),
            Arguments.of("cache ", "cache", null, null, null, "cache"),
            Arguments.of(" cache ", "cache", null, null, null, "cache"),
            Arguments.of(null, null, null, null, IllegalArgumentException.class, null),
            Arguments.of("====", null, null, null, IllegalArgumentException.class, null),
            Arguments.of("   cache     ;   desc=     \"Cache Read\"    ;   dur  =    23    ", "cache", 23L, "Cache Read", null, "cache(Cache Read);dur=23"),
            Arguments.of("   cache     ;desc=     \"Cache Read\"    ;   dur  =    23  ; abcd = ndhcsj  ", "cache", 23L, "Cache Read", null, "cache(Cache Read);dur=23")
        );
    }

    @ParameterizedTest
    @MethodSource("testDataProvider")
    public void parseTest(String s, String name, Long processingTime, String description, Class throwableClass, String expectedAsString) {
        if (throwableClass != null) {
            assertThrows(throwableClass, () -> ServerTimingMetric.parse(s));
        } else {
            ServerTimingMetric serverTimingMetric = ServerTimingMetric.parse(s);
            assertEquals(serverTimingMetric.getName(), name);
            assertEquals(serverTimingMetric.getProcessingTime(), processingTime);
            assertEquals(serverTimingMetric.getDescription(), description);
            assertEquals(serverTimingMetric.asString(), expectedAsString);
        }
    }
}
