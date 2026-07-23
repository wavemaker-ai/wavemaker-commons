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
package com.wavemaker.commons.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.wavemaker.commons.io.File;
import com.wavemaker.commons.io.FilterOn;
import com.wavemaker.commons.io.Folder;
import com.wavemaker.commons.io.Resource;
import com.wavemaker.commons.io.exception.ReadOnlyResourceException;
import com.wavemaker.commons.io.local.LocalFolder;
import com.wavemaker.commons.io.store.MockStoredFolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link com.wavemaker.commons.zip.ZipArchive}.
 *
 * @author Phillip Webb
 */
public class ZipArchiveTest {

    @TempDir
    java.io.File temporaryFolder;

    private final MockStoredFolder folder = new MockStoredFolder();

    private java.io.File zipFile;

    private ZipArchive zip;

    @BeforeEach
    public void setup() throws Exception {
        this.zipFile = new java.io.File(this.temporaryFolder, "zipfile.zip");
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(this.zipFile));
        try {
            zipOutputStream.putNextEntry(new ZipEntry("/a/b/c.txt"));
            zipOutputStream.write("c".getBytes());
            zipOutputStream.putNextEntry(new ZipEntry("/d/"));
            zipOutputStream.putNextEntry(new ZipEntry("/d/e/"));
            zipOutputStream.putNextEntry(new ZipEntry("/d/f/"));
            zipOutputStream.putNextEntry(new ZipEntry("/d/f/g.txt"));
            zipOutputStream.putNextEntry(new ZipEntry("/d/f/h/"));
            zipOutputStream.write("g".getBytes());
        } finally {
            zipOutputStream.close();
        }
        this.zip = new ZipArchive(new LocalFolder(this.zipFile.getParentFile()).getFile(this.zipFile.getName()));
    }

    @Test
    public void shouldNeedZipFile() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> new ZipArchive((File) null));
    }

    @Test
    public void shouldNeedZipFileThatExists() throws Exception {
        File file = mock(File.class);
        assertThrows(IllegalArgumentException.class, () -> new ZipArchive(file));
    }

    @Test
    public void shouldGetFolderNotInZipFile() throws Exception {
        Folder folder = this.zip.getFolder("/a/b/c");
        assertEquals("/a/b/c/", folder.toString());
        assertFalse(folder.exists());
    }

    @Test
    public void shouldGetFileNotInZip() throws Exception {
        File file = this.zip.getFile("/a/b/c");
        assertEquals("/a/b/c", file.toString());
        assertFalse(file.exists());
    }

    @Test
    public void shouldGetFileThatIsInZip() throws Exception {
        File file = this.zip.getFile("/a/b/c.txt");
        assertEquals("/a/b/c.txt", file.toString());
        assertTrue(file.exists());
    }

    @Test
    public void shouldGetFolderThatIsInZip() throws Exception {
        Folder folder = this.zip.getFolder("/d/e");
        assertEquals("/d/e/", folder.toString());
        assertTrue(folder.exists());
    }

    @Test
    public void shouldGetVirtualFolder() throws Exception {
        Folder folder = this.zip.getFolder("/a/b");
        assertEquals("/a/b/", folder.toString());
        assertTrue(folder.exists());
    }

    @Test
    public void shouldNotCreateFile() throws Exception {
        assertThrows(ReadOnlyResourceException.class, () -> this.zip.getFile("/a/b/x.txt").createIfMissing());
    }

    @Test
    public void shouldNotCreateFolder() throws Exception {
        assertThrows(ReadOnlyResourceException.class, () -> this.zip.getFolder("/a/b/c").createIfMissing());
    }

    @Test
    public void shouldList() throws Exception {
        List<String> list = asList(this.zip.getFolder("/d/f").list());
        assertEquals(2, list.size());
        assertTrue(list.contains("g.txt"));
        assertTrue(list.contains("h"));
    }

    @Test
    public void shouldListIncludingVirtualFolder() throws Exception {
        List<String> list = asList(this.zip.getFolder("/a").list());
        assertEquals(1, list.size());
        assertTrue(list.contains("b"));
    }

    @Test
    public void shouldListFromVirtualFolder() throws Exception {
        List<String> list = asList(this.zip.getFolder("/a/b").list());
        assertEquals(1, list.size());
        assertTrue(list.contains("c.txt"));
    }

    @Test
    public void shouldGetSize() throws Exception {
        long actual = this.zip.getFile("/a/b/c.txt").getSize();
        assertEquals(1L, actual);
    }

    @Test
    public void shouldDelegateGetLastModified() throws Exception {
        long actual = this.zip.getFile("/a/b/c.txt").getLastModified();
        assertEquals(this.zipFile.lastModified(), actual);
    }

    @Test
    public void shouldGetInputStream() throws Exception {
        String actual = this.zip.getFile("/a/b/c.txt").getContent().asString();
        assertEquals("c", actual);
    }

    @Test
    public void shouldNotGetOutputStream() throws Exception {
        assertThrows(ReadOnlyResourceException.class, () -> this.zip.getFile("/a/b/c.txt").getContent().asOutputStream());
    }

    @Test
    public void shouldNotDelete() throws Exception {
        assertThrows(ReadOnlyResourceException.class, () -> this.zip.getFile("/a/b/c.txt").delete());
    }

    @Test
    public void shouldNotRename() throws Exception {
        assertThrows(ReadOnlyResourceException.class, () -> this.zip.getFile("/a/b/c.txt").rename("newname"));
    }

    @Test
    public void shouldReloadIfChanged() throws Exception {
        File file = this.zip.getFile("a/b/c.txt");
        assertEquals("c", file.getContent().asString());
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(this.zipFile));
        try {
            zipOutputStream.putNextEntry(new ZipEntry("/a/b/c.txt"));
            zipOutputStream.write("c2".getBytes());
        } finally {
            zipOutputStream.close();
        }
        assertEquals("c2", file.getContent().asString());
    }

    private List<String> asList(Iterable<? extends Resource> iterable) {
        List<String> list = new ArrayList<>();
        for (Resource item : iterable) {
            list.add(item.getName());
        }
        return list;
    }

    @Test
    public void shouldNeedUnzipInputStream() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> ZipArchive.unpack((InputStream) null, this.folder));
    }

    @Test
    @Disabled
    public void shouldUnzip() throws Exception {
        ByteArrayOutputStream outputStreamB = new ByteArrayOutputStream();
        ByteArrayOutputStream outputStreamD = new ByteArrayOutputStream();
        given(this.folder.getStore().exists()).willReturn(true);
        given(this.folder.getFile("a/b.txt").getStore().getOutputStream()).willReturn(outputStreamB);
        given(this.folder.getFile("c/d.txt").getStore().getOutputStream()).willReturn(outputStreamD);
        InputStream zipStream = createSampleZip();
        ZipArchive.unpack(zipStream, this.folder);
        verify(this.folder.getFolder("a").getStore()).create();
        verify(this.folder.getFolder("c").getStore()).create();
        assertEquals("ab", outputStreamB.toString());
        assertEquals("cd", outputStreamD.toString());
    }

    @Test
    public void shouldCreateZipFileWithPrefix() throws Exception {
        Folder rootFolder = createSampleLayout();
        InputStream inputStream = ZipArchive.compress(rootFolder.getFolder("y"), "x");
        List<String> entryNames = getEntryNames(inputStream);
        assertEquals(5, entryNames.size());
        assertTrue(entryNames.contains("x/a/"));
        assertTrue(entryNames.contains("x/a/aa.txt"));
        assertTrue(entryNames.contains("x/a/ab.txt"));
        assertTrue(entryNames.contains("x/b/"));
        assertTrue(entryNames.contains("x/b/ba.txt"));
    }

    @Test
    public void shouldCreateZipFileWithoutPrefix() throws Exception {
        Folder rootFolder = createSampleLayout();
        InputStream inputStream = ZipArchive.compress(rootFolder.getFolder("y"));
        List<String> entryNames = getEntryNames(inputStream);
        assertEquals(5, entryNames.size());
        assertTrue(entryNames.contains("a/aa.txt"));
        assertTrue(entryNames.contains("a/ab.txt"));
        assertTrue(entryNames.contains("b/"));
        assertTrue(entryNames.contains("b/ba.txt"));
    }

    @Test
    public void shouldCreateFilteredZipFile() throws Exception {
        Folder rootFolder = createSampleLayout();
        InputStream inputStream = new ZipResourcesStream(rootFolder.getFolder("y").find().include(FilterOn.names().notStarting("ab")), "x");
        List<String> entryNames = getEntryNames(inputStream);
        assertEquals(4, entryNames.size());
        assertTrue(entryNames.contains("x/a/"));
        assertTrue(entryNames.contains("x/a/aa.txt"));
        assertTrue(entryNames.contains("x/b/"));
        assertTrue(entryNames.contains("x/b/ba.txt"));
    }

    @Test
    public void shouldOnlyHaveExistingFolders() throws Exception {
        // Underlying cause of WM-4263
        assertTrue(this.zip.hasExisting("/a/"));
        assertFalse(this.zip.hasExisting("/missing/"));
    }

    private InputStream createSampleZip() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        zipOutputStream.putNextEntry(new ZipEntry("a/"));
        zipOutputStream.closeEntry();
        zipOutputStream.putNextEntry(new ZipEntry("a/b.txt"));
        IOUtils.write("ab", zipOutputStream);
        zipOutputStream.closeEntry();
        zipOutputStream.putNextEntry(new ZipEntry("c/"));
        zipOutputStream.closeEntry();
        zipOutputStream.putNextEntry(new ZipEntry("c/d.txt"));
        IOUtils.write("cd", zipOutputStream);
        zipOutputStream.closeEntry();
        zipOutputStream.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private Folder createSampleLayout() throws IOException {
        java.io.File ziptest = new java.io.File(this.temporaryFolder, "ziptest");
        ziptest.mkdirs();
        Folder rootFolder = new LocalFolder(ziptest);
        rootFolder.getFolder("y/a").getFile("aa.txt").getContent().write("aa test");
        rootFolder.getFolder("y/a").getFile("ab.txt").getContent().write("ab test");
        rootFolder.getFolder("y/b").getFile("ba.txt").getContent().write("ba test");
        return rootFolder;
    }

    private List<String> getEntryNames(InputStream inputStream) throws IOException {
        ZipInputStream readInputStream = new ZipInputStream(inputStream);
        List<String> entryNames = new ArrayList<>();
        ZipEntry entry = readInputStream.getNextEntry();
        while (entry != null) {
            entryNames.add(entry.getName());
            readInputStream.closeEntry();
            entry = readInputStream.getNextEntry();
        }
        return entryNames;
    }
}
