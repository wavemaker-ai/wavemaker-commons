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

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.wavemaker.commons.util.WMUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;


/*
 * Created by Prithvi Medavaram on 9/10/15.
 */

public class WMUtilsTest {

    static Stream<Arguments> fileProvider() throws IOException {
        return Stream.of(
            Arguments.of(File.createTempFile("deleteIfYouSee", ".txt").getName(), "txt"),
            Arguments.of(File.createTempFile("delete.If.You.;See", ".txt").getName(), "txt"),
            Arguments.of(File.createTempFile("deleteIfYouSee", ".xml").getName(), "xml")
        );
    }

    static Stream<Arguments> uriProvider() {
        return Stream.of(
            Arguments.of("https%3A%2F%2FWaveMaker%2FUtilClass%2FtestURL", "https://WaveMaker/UtilClass/testURL"),
            Arguments.of("http%3A%2F%2Fwww.example.com%2F", "http://www.example.com/"),
            Arguments.of("https%3A%2F%2Fwww.google.co.in%2Fwebhp%3Fsourceid%3Dchrome-instant%26ion%3D1%26espv%3D2%26ie%3DUTF-8%26client%3Dubuntu%23q%3Dwavemaker",
                "https://www.google.co.in/webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8&client=ubuntu#q=wavemaker")
        );
    }

    @ParameterizedTest
    @MethodSource("fileProvider")
    public void getFileExtensionsFromFileNameTest(String fileName, String extension) {
        assertEquals(extension, WMUtils.getFileExtensionFromFileName(fileName));
    }

    @ParameterizedTest
    @MethodSource("uriProvider")
    public void decodeRequestURITest(String encoded, String decoded) {
        assertEquals(decoded, WMUtils.decodeRequestURI(encoded));
    }
}
