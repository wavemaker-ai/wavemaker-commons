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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.wavemaker.commons.io.File;
import com.wavemaker.commons.io.Folder;
import com.wavemaker.commons.io.Resource;
import com.wavemaker.commons.io.ResourceFilter;
import com.wavemaker.commons.io.ResourceFilterContext;
import com.wavemaker.commons.io.ResourceStringFormat;
import com.wavemaker.commons.io.Resources;
import com.wavemaker.commons.io.exception.ResourceDoesNotExistException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link com.wavemaker.commons.io.store.StoredFolder}.
 *
 * @author Phillip Webb
 */
public class StoredFolderTest {

    private MockStoredFolder folder;

    @BeforeEach
    public void setup() {
        this.folder = new MockStoredFolder();
    }

    @Test
    public void shouldCreateWithNoParent() throws Exception {
        assertNull(this.folder.getParent());
    }

    @Test
    public void shouldDelete() throws Exception {
        given(this.folder.getStore().exists()).willReturn(true);
        this.folder.delete();
        verify(this.folder.getStore()).delete();
    }

    @Test
    public void shouldNotDeleteWhenDoesNotExist() throws Exception {
        given(this.folder.getStore().exists()).willReturn(false);
        this.folder.delete();
        verify(this.folder.getStore(), never()).delete();
    }

    @Test
    public void shouldDeleteChildren() throws Exception {
        MockStoredFolder child = this.folder.getFolder("a", true);
        given(this.folder.getStore().exists()).willReturn(true);
        given(this.folder.getStore().list()).willReturn(Collections.singleton("a"));
        this.folder.delete();
        verify(child.getStore()).delete();
        verify(this.folder.getStore()).delete();
    }

    @Test
    public void shouldGetParent() throws Exception {
        Folder parent = this.folder.getFolder("a/b").getParent();
        assertEquals("a", parent.getName());
        assertEquals("/a/", parent.toString());
    }

    @Test
    public void shouldCreateWithNoName() throws Exception {
        assertEquals("", this.folder.getName());
    }

    @Test
    public void shouldCreateWithPathName() throws Exception {
        assertEquals("/", this.folder.toString());
    }

    @Test
    public void shouldDelegateToFileSystemForExists() throws Exception {
        given(this.folder.getStore().exists()).willReturn(true);
        assertTrue(this.folder.exists());
        verify(this.folder.getStore()).exists();
    }

    @Test
    public void shouldGetChildFolder() throws Exception {
        Folder child = this.folder.getFolder("a");
        assertEquals("a", child.getName());
        assertEquals("/a/", child.toString());
    }

    @Test
    public void shouldGetNestedChildFolder() throws Exception {
        Folder child = this.folder.getFolder("a/b");
        assertEquals("b", child.getName());
        assertEquals("/a/b/", child.toString());
    }

    @Test
    public void shouldGetRelativeChildFolder() throws Exception {
        Folder child = this.folder.getFolder("a/b/../c");
        assertEquals("c", child.getName());
        assertEquals("/a/c/", child.toString());
    }

    @Test
    public void shouldGetNestedChildFile() throws Exception {
        File child = this.folder.getFile("a/b");
        assertEquals("b", child.getName());
        assertEquals("/a/b", child.toString());
    }

    @Test
    public void shouldGetRelativeChildFile() throws Exception {
        File child = this.folder.getFile("a/b/../c");
        assertEquals("c", child.getName());
        assertEquals("/a/c", child.toString());
    }

    @Test
    public void shouldGetExistingFile() throws Exception {
        this.folder.getFile("a");
        Resource child = this.folder.getExisting("a");
        assertInstanceOf(File.class, child);
        assertEquals("a", child.getName());
        assertEquals("/a", child.toString());
    }

    @Test
    public void shouldGetExistingFolder() throws Exception {
        this.folder.getFolder("a");
        Resource child = this.folder.getExisting("a");
        assertInstanceOf(Folder.class, child);
        assertEquals("a", child.getName());
        assertEquals("/a/", child.toString());
    }

    @Test
    public void shouldNotGetExistingIfDoesNotExist() throws Exception {
        assertThrows(ResourceDoesNotExistException.class, () -> this.folder.getExisting("a"));
    }

    @Test
    public void shouldNotHaveExistingFileIfDoesNotExist() throws Exception {
        boolean actual = this.folder.hasExisting("a");
        assertFalse(actual);
    }

    @Test
    public void shouldHaveExistingFile() throws Exception {
        this.folder.getFolder("a");
        boolean actual = this.folder.hasExisting("a");
        assertTrue(actual);
    }

    @Test
    public void shouldCreateMissingDirectory() throws Exception {
        MockStoredFolder child = this.folder.getFolder("a");
        child.createIfMissing();
        verify(child.getStore()).create();
    }

    @Test
    public void shouldNotCreateExistingDirectory() throws Exception {
        MockStoredFolder child = this.folder.getFolder("a", true);
        child.createIfMissing();
        verify(child.getStore(), never()).create();
    }

    @Test
    public void shouldCreateParent() throws Exception {
        MockStoredFolder child = this.folder.getFolder("a");
        MockStoredFolder grandChild = child.getFolder("b");
        grandChild.createIfMissing();
        InOrder inOrder = inOrder(grandChild.getStore(), child.getStore());
        inOrder.verify(child.getStore()).create();
        inOrder.verify(grandChild.getStore()).create();
    }

    @Test
    public void shouldListResources() throws Exception {
        this.folder.getFolder("a", true);
        this.folder.getFile("b", true);
        given(this.folder.getStore().exists()).willReturn(true);
        given(this.folder.getStore().list()).willReturn(Arrays.asList("a", "b"));
        Resources<Resource> resources = this.folder.list();
        Iterator<Resource> iterator = resources.iterator();
        Resource resourceA = iterator.next();
        Resource resourceB = iterator.next();
        assertFalse(iterator.hasNext());
        assertInstanceOf(Folder.class, resourceA);
        assertInstanceOf(File.class, resourceB);
        assertEquals("/a/", resourceA.toString());
        assertEquals("/b", resourceB.toString());
    }

    @Test
    public void shouldListFilteredResources() throws Exception {
        this.folder.getFolder("a", true);
        this.folder.getFile("b", true);
        given(this.folder.getStore().exists()).willReturn(true);
        given(this.folder.getStore().list()).willReturn(Arrays.asList("a", "b"));
        Resources<Resource> resources = this.folder.list().include(new ResourceFilter() {

            @Override
            public boolean match(ResourceFilterContext context, Resource resource) {
                return resource instanceof File;
            }
        });
        Iterator<Resource> iterator = resources.iterator();
        Resource file = iterator.next();
        assertFalse(iterator.hasNext());
        assertEquals("/b", file.toString());
    }

    @Test
    public void shouldListFilteredResourcesWithAnonymousClass() throws Exception {
        this.folder.getFolder("a", true);
        this.folder.getFile("b", true);
        given(this.folder.getStore().exists()).willReturn(true);
        given(this.folder.getStore().list()).willReturn(Arrays.asList("a", "b"));
        Resources<File> resources = this.folder.list().files().include(new ResourceFilter() {

            @Override
            public boolean match(ResourceFilterContext context, Resource resource) {
                return true;
            }
        });
        Iterator<File> iterator = resources.iterator();
        Resource file = iterator.next();
        assertFalse(iterator.hasNext());
        assertEquals("/b", file.toString());

    }

    @Test
    public void shouldNeedListResourcesFilters() throws Exception {
        assertThrows(IllegalArgumentException.class,
            () -> this.folder.list().include((ResourceFilter[]) null));
    }

    @Test
    public void shouldIterateUsingList() throws Exception {
        this.folder.getFolder("a", true);
        this.folder.getFile("b", true);
        given(this.folder.getStore().exists()).willReturn(true);
        given(this.folder.getStore().list()).willReturn(Arrays.asList("a", "b"));
        Iterator<Resource> iterator = this.folder.iterator();
        Resource resourceA = iterator.next();
        Resource resourceB = iterator.next();
        assertFalse(iterator.hasNext());
        assertInstanceOf(Folder.class, resourceA);
        assertInstanceOf(File.class, resourceB);
        assertEquals("/a/", resourceA.toString());
        assertEquals("/b", resourceB.toString());
    }

    @Test
    public void shouldMoveWithoutChildren() throws Exception {
        Folder destination = mock(Folder.class);
        Folder destinationChild = mock(Folder.class);
        MockStoredFolder child = this.folder.getFolder("a", true);
        given(this.folder.getStore().exists()).willReturn(true);
        given(destination.getFolder("a")).willReturn(destinationChild);
        child.moveTo(destination);
        verify(destination).getFolder("a");
        verify(destinationChild).createIfMissing();
    }

    @Test
    public void shouldNotMoveIfDoesNotExist() throws Exception {
        Folder destination = mock(Folder.class);
        MockStoredFolder child = this.folder.getFolder("a", false);
        given(this.folder.getStore().exists()).willReturn(true);
        assertThrows(ResourceDoesNotExistException.class, () -> child.moveTo(destination));
    }

    @Test
    public void shouldNotMoveRoot() throws Exception {
        Folder destination = mock(Folder.class);
        given(this.folder.getStore().exists()).willReturn(true);
        assertThrows(IllegalStateException.class, () -> this.folder.moveTo(destination));
    }

    @Test
    public void shouldMoveChildren() throws Exception {
        Folder destination = mock(Folder.class);
        Folder destinationChild = mock(Folder.class);
        Folder destinationGrandchild = mock(Folder.class);
        MockStoredFolder child = this.folder.getFolder("a", true);
        child.getFolder("b", true);
        given(child.getStore().list()).willReturn(Collections.singleton("b"));
        given(destination.getFolder("a")).willReturn(destinationChild);
        given(destinationChild.getFolder("b")).willReturn(destinationGrandchild);
        child.moveTo(destination);
        verify(destinationGrandchild).createIfMissing();
    }

    @Test
    public void shouldCopyWithoutChildren() throws Exception {
        Folder destination = mock(Folder.class);
        Folder destinationChild = mock(Folder.class);
        MockStoredFolder child = this.folder.getFolder("a", true);
        given(this.folder.getStore().exists()).willReturn(true);
        given(destination.getFolder("a")).willReturn(destinationChild);
        child.copyTo(destination);
        verify(destination).getFolder("a");
        verify(destinationChild).createIfMissing();
    }

    @Test
    public void shouldNotCopyIfDoesNotExist() throws Exception {
        Folder destination = mock(Folder.class);
        MockStoredFolder child = this.folder.getFolder("a");
        given(this.folder.getStore().exists()).willReturn(false);
        assertThrows(ResourceDoesNotExistException.class, () -> child.copyTo(destination));
    }

    @Test
    public void shouldNotCopyRoot() throws Exception {
        given(this.folder.getStore().exists()).willReturn(true);
        Folder destination = mock(Folder.class);
        assertThrows(IllegalStateException.class, () -> this.folder.copyTo(destination));
    }

    @Test
    public void shouldCopyChildren() throws Exception {
        MockStoredFolder child = this.folder.getFolder("a", true);
        child.getFolder("b", true);
        given(this.folder.getStore().exists()).willReturn(true);
        given(child.getStore().list()).willReturn(Collections.singleton("b"));
        Folder destination = mock(Folder.class);
        Folder destinationChild = mock(Folder.class);
        Folder destinationGrandchild = mock(Folder.class);
        given(destination.getFolder("a")).willReturn(destinationChild);
        given(destinationChild.getFolder("b")).willReturn(destinationGrandchild);
        child.copyTo(destination);
        verify(destinationGrandchild).createIfMissing();
    }

    @Test
    public void shouldRename() throws Exception {
        MockStoredFolder subFolder = this.folder.getFolder("subfolder");
        given(subFolder.getStore().exists()).willReturn(true);
        subFolder.rename("folder.bak");
        verify(subFolder.getStore()).rename("folder.bak");
    }

    @Test
    public void shouldNotRenameRootFolder() throws Exception {
        given(this.folder.getStore().exists()).willReturn(true);
        assertThrows(IllegalStateException.class, () -> this.folder.rename("folder.bak"));
    }

    @Test
    public void shouldNotRenameIfDoesNotExist() throws Exception {
        MockStoredFolder subFolder = this.folder.getFolder("subfolder");
        given(subFolder.getStore().exists()).willReturn(false);
        assertThrows(ResourceDoesNotExistException.class, () -> subFolder.rename("file.bak"));
    }

    @Test
    public void shouldNotRenameToEmpty() throws Exception {
        MockStoredFolder subFolder = this.folder.getFolder("subfolder");
        given(subFolder.getStore().exists()).willReturn(true);
        assertThrows(IllegalArgumentException.class, () -> subFolder.rename(""));
    }

    @Test
    public void shouldNotRenameWithPathElements() throws Exception {
        given(this.folder.getStore().exists()).willReturn(true);
        assertThrows(IllegalArgumentException.class, () -> this.folder.rename("file/bak"));
    }

    @Test
    public void shouldAppendPathToToString() throws Exception {
        MockStoredFolder child = this.folder.getFolder("a/b/c");
        assertEquals("/a/b/c/", child.toString());
    }

    @Test
    public void shouldFormatToString() throws Exception {
        Folder child = this.folder.getFolder("a", true).jail().getFolder("b/c");
        assertEquals("/b/c/", child.toString());
        assertEquals("/b/c/", child.toString(ResourceStringFormat.FULL));
        assertEquals("/a/b/c/", child.toString(ResourceStringFormat.UNJAILED));
    }

    @Test
    public void shouldNeedNameForGet() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> this.folder.get("", File.class));
    }

    @Test
    public void shouldNeedTypeForGet() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> this.folder.get("name", null));
    }

    @Test
    public void shouldGetFile() throws Exception {
        this.folder = spy(this.folder);
        this.folder.get("name", File.class);
        verify(this.folder).getFile("name");
    }

    @Test
    public void shouldGetFolder() throws Exception {
        this.folder = spy(this.folder);
        this.folder.get("name", Folder.class);
        verify(this.folder).getFolder("name");
    }

    @Test
    public void shouldGetResource() throws Exception {
        this.folder.getFile("name", true);
        this.folder = spy(this.folder);
        this.folder.get("name", Resource.class);
        verify(this.folder).getExisting("name");
    }

    @Test
    public void shouldJail() throws Exception {
        Folder jailed = this.folder.getFolder("a").jail();
        Folder sub = jailed.getFolder("/b");
        assertEquals("/b/", sub.toString());
        assertEquals("/a/b/", sub.toString(ResourceStringFormat.UNJAILED));
    }

    @Test
    public void shouldDoubleJail() throws Exception {
        Folder jailed = this.folder.getFolder("a").jail().jail();
        Folder sub = jailed.getFolder("/b");
        assertEquals("/b/", sub.toString());
        assertEquals("/a/b/", sub.toString(ResourceStringFormat.UNJAILED));
    }

    // FIXME test find

    // FIXME test find and copy?

}
