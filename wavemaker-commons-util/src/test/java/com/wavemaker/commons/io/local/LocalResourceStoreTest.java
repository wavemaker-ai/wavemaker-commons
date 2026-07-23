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
package com.wavemaker.commons.io.local;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.wavemaker.commons.io.File;
import com.wavemaker.commons.io.Folder;
import com.wavemaker.commons.io.JailedResourcePath;
import com.wavemaker.commons.io.Resource;
import com.wavemaker.commons.io.ResourcePath;
import com.wavemaker.commons.io.exception.ResourceTypeMismatchException;
import com.wavemaker.commons.io.local.LocalResourceStore.LocalFileStore;
import com.wavemaker.commons.io.local.LocalResourceStore.LocalFolderStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link com.wavemaker.commons.io.local.LocalResourceStore} and subclasses.
 *
 * @author Phillip Webb
 */
public class LocalResourceStoreTest {

    @TempDir
    java.io.File temp;

    private LocalFolderStore store;

    @BeforeEach
    public void setup() {
        Folder root = new LocalFolder(this.temp);
        root.getFile("/a/b/c.txt").getContent().write("c");
        root.getFile("/d/e/f.txt").getContent().write("d");
        root.getFile("/g.txt").getContent().write("g");
        this.store = new LocalFolderStore(this.temp, new JailedResourcePath());
    }

    @Test
    public void shouldNeedRoot() throws Exception {
        assertThrows(IllegalArgumentException.class,
            () -> new LocalFolderStore(null, new JailedResourcePath()));
    }

    @Test
    public void shouldNeedPath() throws Exception {
        assertThrows(IllegalArgumentException.class,
            () -> new LocalFolderStore(this.temp, null));
    }

    @Test
    public void shouldNeedRootThatIsFolder() throws Exception {
        java.io.File notAFolder = new java.io.File(this.temp, "g.txt");
        assertThrows(IllegalStateException.class,
            () -> new LocalFolderStore(notAFolder, new JailedResourcePath()));
    }

    @Test
    public void shouldGetPath() throws Exception {
        JailedResourcePath path = new JailedResourcePath(new ResourcePath(), new ResourcePath().get("a/b/c"));
        LocalFolderStore store = new LocalFolderStore(this.temp, path);
        assertEquals(path, store.getPath());
    }

    @Test
    public void shouldReturnNullForMissingGetExisting() throws Exception {
        Resource actual = this.store.getExisting(new JailedResourcePath().get("doesnotexist"));
        assertNull(actual);
    }

    @Test
    public void shouldGetExistingFile() throws Exception {
        Resource actual = this.store.getExisting(new JailedResourcePath().get("g.txt"));
        assertInstanceOf(File.class, actual);
        assertEquals("/g.txt", actual.toString());
    }

    @Test
    public void shouldGetExistingFolder() throws Exception {
        Resource actual = this.store.getExisting(new JailedResourcePath().get("a"));
        assertInstanceOf(Folder.class, actual);
        assertEquals("/a/", actual.toString());
    }

    @Test
    public void shouldGetFolder() throws Exception {
        Folder folder = this.store.getFolder(new JailedResourcePath().get("x"));
        assertEquals("/x/", folder.toString());
    }

    @Test
    public void shouldNotGetExistingFileAsFolder() throws Exception {
        assertThrows(ResourceTypeMismatchException.class,
            () -> this.store.getFolder(new JailedResourcePath().get("g.txt")));
    }

    @Test
    public void shouldGetFile() throws Exception {
        File file = this.store.getFile(new JailedResourcePath().get("x"));
        assertEquals("/x", file.toString());
    }

    @Test
    public void shouldNotGetExistingFolderAsFile() throws Exception {
        assertThrows(ResourceTypeMismatchException.class,
            () -> this.store.getFile(new JailedResourcePath().get("a")));
    }

    @Test
    public void shouldRename() throws Exception {
        LocalFileStore store = new LocalFileStore(this.temp, new JailedResourcePath().get("g.txt"));
        store.rename("g.bak");
        assertFalse(new java.io.File(this.temp, "g.txt").exists());
        assertTrue(new java.io.File(this.temp, "g.bak").exists());
    }

    @Test
    public void shouldExist() throws Exception {
        LocalFileStore store = new LocalFileStore(this.temp, new JailedResourcePath().get("g.txt"));
        assertTrue(store.exists());
    }

    @Test
    public void shouldNotExist() throws Exception {
        LocalFileStore store = new LocalFileStore(this.temp, new JailedResourcePath().get("doesnotexist"));
        assertFalse(store.exists());
    }

    @Test
    public void shouldDelete() throws Exception {
        LocalFileStore store = new LocalFileStore(this.temp, new JailedResourcePath().get("g.txt"));
        store.delete();
        assertFalse(new java.io.File(this.temp, "g.txt").exists());
    }

    @Test
    public void shouldCreateFile() throws Exception {
        LocalFileStore store = new LocalFileStore(this.temp, new JailedResourcePath().get("new"));
        store.create();
        assertTrue(new java.io.File(this.temp, "new").exists());
    }

    @Test
    public void shouldGetInputStream() throws Exception {
        LocalFileStore store = new LocalFileStore(this.temp, new JailedResourcePath().get("g.txt"));
        String actual = IOUtils.toString(store.getInputStream());
        assertEquals("g", actual);
    }

    @Test
    public void shouldGetOutputStream() throws Exception {
        LocalFileStore store = new LocalFileStore(this.temp, new JailedResourcePath().get("g.txt"));
        OutputStream stream = store.getOutputStream();
        try {
            stream.write("x".getBytes());
        } finally {
            stream.close();
        }
        String actual = IOUtils.toString((new FileInputStream(new java.io.File(this.temp, "g.txt"))));
        assertEquals("x", actual);

    }

    @Test
    public void shouldGetSize() throws Exception {
        LocalFileStore store = new LocalFileStore(this.temp, new JailedResourcePath().get("g.txt"));
        long actual = store.getSize();
        assertEquals(1L, actual);
    }

    @Test
    public void shouldGetLastModified() throws Exception {
        LocalFileStore store = new LocalFileStore(this.temp, new JailedResourcePath().get("g.txt"));
        long actual = store.getLastModified();
        assertEquals(new java.io.File(this.temp, "g.txt").lastModified(), actual);
    }

    @Test
    public void shouldTouchFile() throws Exception {
        LocalFileStore store = new LocalFileStore(this.temp, new JailedResourcePath().get("g.txt"));
        java.io.File f = new java.io.File(this.temp, "g.txt");
        f.setLastModified(f.lastModified() - TimeUnit.MINUTES.toMillis(10));
        long beforeTouch = f.lastModified();
        store.touch();
        assertTrue(f.lastModified() > beforeTouch);
    }

    @Test
    public void shouldCreateFolder() throws Exception {
        LocalFolderStore store = new LocalFolderStore(this.temp, new JailedResourcePath().get("new"));
        store.create();
        assertTrue(new java.io.File(this.temp, "new").exists());
    }

    @Test
    public void shouldList() throws Exception {
        Set<String> expected = new HashSet<>(Arrays.asList("a", "d", "g.txt"));
        Set<String> actual = new HashSet<>(Arrays.asList("a", "d", "g.txt"));
        for (String name : this.store.list()) {
            actual.add(name);
        }
        assertEquals(expected, actual);
    }
}
