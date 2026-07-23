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

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TempFilesStorageManagerTest {

    private TempFilesStorageManager tempFilesStorageManager;
    private String fileId;

    @BeforeEach
    public void beforeTest() {
        tempFilesStorageManager = new TempFilesStorageManager();
        fileId = tempFilesStorageManager.registerNewFile("sample.txt");
    }

    @Test
    public void testRegisterNewFile() {
        String tempFieldId = tempFilesStorageManager.registerNewFile(new ByteArrayInputStream("some data".getBytes()), "sample1.txt");
        assertEquals(tempFilesStorageManager.getFileName(tempFieldId), "sample1.txt");
        tempFilesStorageManager.deRegister(tempFieldId);
    }

    @Test
    public void testGetFileName() {
        assertEquals(tempFilesStorageManager.getFileName(fileId), "sample.txt");
    }

    @Test
    public void testGetFilePath() {
        assertTrue(tempFilesStorageManager.getFilePath(fileId).startsWith("/tmp/auto-purge/"));

        System.setProperty("wm.studio.temp.dir", "/tmp/wm_temp");

        TempFilesStorageManager tmpFSM = new TempFilesStorageManager();
        final String newFileId = tmpFSM.registerNewFile("/sample.txt");

        assertFalse(tmpFSM.getFilePath(newFileId).startsWith("/tmp/auto-purge/"));
        assertTrue(tmpFSM.getFilePath(newFileId).startsWith("/tmp/wm_temp/auto-purge/"));

        System.clearProperty("wm.studio.temp.dir");
    }

    @Test
    public void testGetFileInputStream() {
        assertNotNull(tempFilesStorageManager.getFileInputStream(fileId));
    }

    @Test
    public void testGetFileOutputStream() {
        assertNotNull(tempFilesStorageManager.getFileOutputStream(fileId));
    }

    @Test
    public void testGetUniqueDirectory() {
        assertTrue(tempFilesStorageManager.getFilePath(fileId).contains("/sample.txt"));
    }

    @Test
    public void testDeRegister() {
        String tempFieldId = tempFilesStorageManager.registerNewFile(new ByteArrayInputStream("some data".getBytes()), "sample2.txt");
        tempFilesStorageManager.deRegister(tempFieldId);
        assertThrows(IllegalStateException.class,
            () -> tempFilesStorageManager.getFileName(tempFieldId));
    }

    @AfterEach
    public void afterTest() {
        tempFilesStorageManager.deRegister(fileId);
    }
}