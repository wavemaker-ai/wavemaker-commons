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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncodeUtilsTest {

    static Stream<Arguments> testData() {
        return Stream.of(
            Arguments.of("company name", "company+name"),
            Arguments.of("a+b", "a%2Bb"),
            Arguments.of("a%ddf", "a%25ddf"),
            Arguments.of("as%2Csdxfcv", "as%252Csdxfcv"),
            Arguments.of("this is 'random',\\\"text\\\"2Check(encoding&{decoding})",
                "this+is+%27random%27%2C%5C%22text%5C%222Check%28encoding%26%7Bdecoding%7D%29")
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    public void encode(String actualString, String encodedString) {
        assertEquals(encodedString, EncodeUtils.encode(actualString));
    }

    @ParameterizedTest
    @MethodSource("testData")
    public void decode(String actualString, String encodedString) {
        assertEquals(actualString, EncodeUtils.decode(encodedString));
    }
}
