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
package com.wavemaker.commons.util.utils;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.wavemaker.commons.util.ClassUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Matt Small
 */
public class ClassUtilsTest {

    private int testField = 10;

    @Test
    public void newInstanceTest() {
        Class<?> clazz = ClassUtilsTest.class;
        Object returned = ClassUtils.newInstance(clazz);
        assertTrue(returned instanceof ClassUtilsTest);
        assertEquals(10, ((ClassUtilsTest) returned).testField);
    }

    static Stream<Arguments> getterNames() {
        return Stream.of(
            Arguments.of("User", "getUser"),
            Arguments.of("U", "getU"),
            Arguments.of("u", "getU"),
            Arguments.of("username", "getUsername"),
            Arguments.of("userName", "getUserName")
        );
    }

    static Stream<Arguments> altGetterNames() {
        return Stream.of(
            Arguments.of("User", "isUser"),
            Arguments.of("U", "isU"),
            Arguments.of("u", "isU"),
            Arguments.of("username", "isUsername"),
            Arguments.of("userName", "isUserName")
        );
    }

    static Stream<Arguments> setterNames() {
        return Stream.of(
            Arguments.of("User", "setUser"),
            Arguments.of("U", "setU"),
            Arguments.of("u", "setU"),
            Arguments.of("username", "setUsername"),
            Arguments.of("userName", "setUserName")
        );
    }

    @ParameterizedTest
    @MethodSource("getterNames")
    public void getPropertyGetterNameTest(String propertyName, String expectedGetterName) {
        assertEquals(expectedGetterName, ClassUtils.getPropertyGetterName(propertyName));
    }

    @ParameterizedTest
    @MethodSource("altGetterNames")
    public void getAltPropertyGetterNameTest(String propertyName, String expectedAltPropertyGetterName) {
        assertEquals(expectedAltPropertyGetterName, ClassUtils.getAltPropertyGetterName(propertyName));
    }

    @ParameterizedTest
    @MethodSource("setterNames")
    public void getPropertySetterNameTest(String propertyName, String expectedSetterName) {
        assertEquals(expectedSetterName, ClassUtils.getPropertySetterName(propertyName));
    }
}
