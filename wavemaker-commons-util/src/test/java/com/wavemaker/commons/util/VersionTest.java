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
package com.wavemaker.commons.util;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Uday Shankar
 */
public class VersionTest {

    static Stream<Arguments> compareToDataProvider() {
        return Stream.of(
            Arguments.of("8", "9", true),
            Arguments.of("9", "10", true),
            Arguments.of("9.5.0", "9.4.1", false),
            Arguments.of("8.0", "8.1", true),
            Arguments.of("8.0", "9", true),
            Arguments.of("9", "8.2", false),
            Arguments.of("9.4.0.1", "9.5", true),
            Arguments.of("9.4.0.1", "10.0.0", true),
            Arguments.of("9.4.0.1", "9.4.0.2", true),
            Arguments.of("9.4.0.1", "9.4.0.1", null)
        );
    }

    @ParameterizedTest
    @MethodSource("compareToDataProvider")
    public void testCompareTo(String version1, String version2, Boolean expectedValueLessThanZero) {
        int compareTo = new Version(version1).compareTo(new Version(version2));
        if (expectedValueLessThanZero == null) {
            Assertions.assertTrue(compareTo == 0);
        } else if (expectedValueLessThanZero) {
            Assertions.assertTrue(compareTo < 0);
        } else if (!expectedValueLessThanZero) {
            Assertions.assertTrue(compareTo > 0);
        }
    }
}
