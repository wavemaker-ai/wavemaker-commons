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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.wavemaker.commons.io.local.LocalFolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link ResourceURL}.
 *
 * @author Phillip Webb
 */
public class ResourceURLTest {

    @TempDir
    java.io.File temporaryFolder;

    private Folder root;

    @BeforeEach
    public void setup() throws Exception {
        java.io.File fs = new java.io.File(this.temporaryFolder, "fs");
        fs.mkdirs();
        this.root = new LocalFolder(fs);
        this.root.getFile("/jail/a/b/c.txt").getContent().write("c");
        this.root.getFile("/jail/x/y/z.txt").getContent().write("z");
    }

    @Test
    public void shouldGetFileURL() throws Exception {
        URL url = ResourceURL.get(this.root.getFile("/jail/a/b/c.txt"));
        assertEquals("rfs:/jail/a/b/c.txt", url.toString());
    }

    @Test
    public void shouldGetFolderURL() throws Exception {
        URL url = ResourceURL.get(this.root.getFolder("/jail/a/b"));
        assertEquals("rfs:/jail/a/b/", url.toString());
    }

    @Test
    public void shouldGetJailedURL() throws Exception {
        Folder jail = this.root.getFolder("jail").jail();
        URL url = ResourceURL.get(jail.getFile("/a/b/c.txt"));
        assertEquals("rfs:/a/b/c.txt", url.toString());
    }

    @Test
    public void shouldOpenStream() throws Exception {
        URL url = ResourceURL.get(this.root.getFile("/jail/a/b/c.txt"));
        assertEquals("c", IOUtils.toString(url.openStream()));
    }

    @Test
    public void shouldOpenJailedStream() throws Exception {
        Folder jail = this.root.getFolder("jail").jail();
        URL url = ResourceURL.get(jail.getFile("/a/b/c.txt"));
        assertEquals("c", IOUtils.toString(url.openStream()));
    }

    @Test
    public void shouldCreateRelativeToFolder() throws Exception {
        Folder jail = this.root.getFolder("jail").jail();
        URL ab = ResourceURL.get(jail.getFolder("a/b"));
        URL url = new URL(ab, "c.txt");
        assertEquals("rfs:/a/b/c.txt", url.toString());
        assertEquals("c", IOUtils.toString(url.openStream()));
    }

    @Test
    public void shouldCreateExactToFolder() throws Exception {
        Folder jail = this.root.getFolder("jail").jail();
        URL ab = ResourceURL.get(jail.getFolder("a/b"));
        URL url = new URL(ab, "/x/y/z.txt");
        assertEquals("rfs:/x/y/z.txt", url.toString());
        assertEquals("z", IOUtils.toString(url.openStream()));
    }

    @Test
    public void shouldCreateRelativeToFile() throws Exception {
        Folder jail = this.root.getFolder("jail").jail();
        URL abc = ResourceURL.get(jail.getFile("a/b/c.txt"));
        URL url = new URL(abc, "/x/y/z.txt");
        assertEquals("rfs:/x/y/z.txt", url.toString());
        assertEquals("z", IOUtils.toString(url.openStream()));
    }

    @Test
    public void shouldThrowIOExceptionIfFileDoesNotExist() throws Exception {
        URL url = ResourceURL.get(this.root.getFile("doesnotexist.txt"));
        assertThrows(IOException.class, () -> url.openConnection());
    }

    @Test
    public void shouldThrowIOOpeningFolder() throws Exception {
        URL url = ResourceURL.get(this.root.getFolder("/jail/a/b"));
        assertThrows(IOException.class, () -> url.openConnection());
    }

    @Test
    public void shouldWorkViaClassLoader() throws Exception {
        Folder jail = this.root.getFolder("jail").jail();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{ResourceURL.get(jail)});
        assertEquals("c", IOUtils.toString(classLoader.getResourceAsStream("/a/b/c.txt")));
        assertEquals("rfs:/x/y/z.txt", classLoader.getResource("/x/y/z.txt").toString());
    }

    @Test
    public void shouldGetForCollection() throws Exception {
        List<Folder> folders = new ArrayList<>();
        folders.add(this.root.getFolder("/jail/a"));
        folders.add(this.root.getFolder("/jail/a/b"));
        List<URL> url = ResourceURL.getForResources(folders);
        assertEquals(2, url.size());
        assertEquals("rfs:/jail/a/", url.get(0).toString());
        assertEquals("rfs:/jail/a/b/", url.get(1).toString());
    }
}
