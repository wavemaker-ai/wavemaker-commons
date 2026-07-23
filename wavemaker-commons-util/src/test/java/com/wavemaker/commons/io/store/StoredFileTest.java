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
package com.wavemaker.commons.io.store;

import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.wavemaker.commons.io.AbstractFileContent;
import com.wavemaker.commons.io.File;
import com.wavemaker.commons.io.FileContent;
import com.wavemaker.commons.io.Folder;
import com.wavemaker.commons.io.exception.ResourceDoesNotExistException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link com.wavemaker.commons.io.store.StoredFile}.
 *
 * @author Phillip Webb
 */
public class StoredFileTest {

    private StoredFile file;

    @BeforeEach
    public void setup() {
        this.file = new MockStoredFolder().getFile("file.txt");
    }

    @Test
    public void shouldGetSizeFromStore() throws Exception {
        Long size = 100L;
        given(this.file.getStore().getSize()).willReturn(size);
        assertEquals(size, this.file.getSize());
        verify(this.file.getStore()).getSize();
    }

    @Test
    public void shouldGetLastModifiedFromStore() throws Exception {
        Long lastModified = 100L;
        given(this.file.getStore().getLastModified()).willReturn(lastModified);
        assertEquals(lastModified, this.file.getLastModified());
        verify(this.file.getStore()).getLastModified();
    }

    @Test
    public void shouldThrowOnTouchNonExistingFile() throws Exception {
        given(this.file.exists()).willReturn(false);
        assertThrows(ResourceDoesNotExistException.class, () -> this.file.touch());
    }

    @Test
    public void shouldTouchExistingFile() throws Exception {
        given(this.file.exists()).willReturn(true);
        this.file.touch();
        verify(this.file.getStore()).touch();
    }

    @Test
    public void shouldGetContentInputStreamFromFileSystem() throws Exception {
        InputStream inputStream = mock(InputStream.class);
        given(this.file.getStore().getInputStream()).willReturn(inputStream);
        assertEquals(inputStream, this.file.getContent().asInputStream());
    }

    @Test
    public void shouldGetContentOutputStreamFromFileSystem() throws Exception {
        OutputStream outputStream = mock(OutputStream.class);
        given(this.file.getStore().getOutputStream(false)).willReturn(outputStream);
        assertEquals(outputStream, this.file.getContent().asOutputStream());
    }

    @Test
    public void shouldGetContentAsAbstractContent() throws Exception {
        assertInstanceOf(AbstractFileContent.class, this.file.getContent());
    }

    @Test
    public void shouldDelete() throws Exception {
        given(this.file.getStore().exists()).willReturn(true);
        this.file.delete();
        verify(this.file.getStore()).delete();
    }

    @Test
    public void shouldNotDeleteWhenDoesNotExist() throws Exception {
        given(this.file.getStore().exists()).willReturn(false);
        this.file.delete();
        verify(this.file.getStore(), never()).delete();
    }

    @Test
    public void shouldCreateIfMissingNewFile() throws Exception {
        given(this.file.getStore().exists()).willReturn(false);
        this.file.createIfMissing();
        verify(this.file.getStore()).create();
    }

    @Test
    public void shouldNotCreateIfMissingExistingFile() throws Exception {
        given(this.file.getStore().exists()).willReturn(true);
        this.file.createIfMissing();
        verify(this.file.getStore(), never()).create();
    }

    @Test
    public void shouldNotMoveIfDoesNotExist() throws Exception {
        Folder destination = mock(Folder.class);
        given(this.file.getStore().exists()).willReturn(false);
        assertThrows(ResourceDoesNotExistException.class, () -> this.file.moveTo(destination));
    }

    @Test
    @Disabled
    public void shouldMove() throws Exception {
        Folder destination = mock(Folder.class);
        File destinationFile = mock(File.class);
        FileContent destinationContent = mock(FileContent.class);
        InputStream inputStream = mock(InputStream.class);
        given(destination.getFile("file.txt")).willReturn(destinationFile);
        given(destinationFile.getContent()).willReturn(destinationContent);
        given(this.file.getStore().exists()).willReturn(true);
        given(this.file.getStore().getInputStream()).willReturn(inputStream);
        this.file.moveTo(destination);
        verify(destinationContent).write(inputStream);
        verify(this.file.getStore()).delete();
    }

    @Test
    public void shouldNotCopyIfDoesNotExist() throws Exception {
        Folder destination = mock(Folder.class);
        given(this.file.getStore().exists()).willReturn(false);
        assertThrows(ResourceDoesNotExistException.class, () -> this.file.copyTo(destination));
    }

    @Test
    public void shouldCopy() throws Exception {
        Folder destination = mock(Folder.class);
        File destinationFile = mock(File.class);
        FileContent destinationContent = mock(FileContent.class);
        given(destination.getFile("file.txt")).willReturn(destinationFile);
        given(destinationFile.getContent()).willReturn(destinationContent);
        given(this.file.getStore().exists()).willReturn(true);
        this.file.copyTo(destination);
        verify(destinationContent).write(this.file);
        verify(this.file.getStore(), never()).delete();
    }

    @Test
    public void shouldRename() throws Exception {
        given(this.file.getStore().exists()).willReturn(true);
        this.file.rename("file.bak");
        verify(this.file.getStore()).rename("file.bak");
    }

    @Test
    public void shouldNotRenameIfDoesNotExist() throws Exception {
        given(this.file.getStore().exists()).willReturn(false);
        assertThrows(ResourceDoesNotExistException.class, () -> this.file.rename("file.bak"));
    }

    @Test
    public void shouldNotRenameToEmpty() throws Exception {
        given(this.file.getStore().exists()).willReturn(true);
        assertThrows(IllegalArgumentException.class, () -> this.file.rename(""));
    }

    @Test
    public void shouldNotRenameWithPathElements() throws Exception {
        given(this.file.getStore().exists()).willReturn(true);
        assertThrows(IllegalArgumentException.class, () -> this.file.rename("file/bak"));
    }

    @Test
    public void shouldHaveToString() throws Exception {
        assertEquals("/file.txt", this.file.toString());
    }

}
