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
package com.wavemaker.commons.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wavemaker.commons.util.WMIOUtils;

public class DeleteTempFileOnCloseInputStreamTest {

    private File tempFile;
    private DeleteTempFileOnCloseInputStream deleteTempFileOnCloseInputStream;

    @BeforeEach
    public void beforeTest() throws FileNotFoundException {
        tempFile = WMIOUtils.createTempFile("test", ".txt");
        deleteTempFileOnCloseInputStream = new DeleteTempFileOnCloseInputStream(tempFile);
    }

    @Test
    public void testClose() throws IOException {
        deleteTempFileOnCloseInputStream.close();
        Assertions.assertFalse(deleteTempFileOnCloseInputStream.getTempFile().exists());
    }

    @Test
    public void testGetTempFile() {
        Assertions.assertNotNull(deleteTempFileOnCloseInputStream.getTempFile());
        Assertions.assertTrue(deleteTempFileOnCloseInputStream.getTempFile().getName().startsWith("test"));
    }

    @AfterEach
    public void afterTest() {
        tempFile.delete();
    }
}