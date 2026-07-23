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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link ResourcesCollection}.
 *
 * @author Phillip Webb
 */
@ExtendWith(MockitoExtension.class)
public class ResourcesCollectionTest {

    @Mock
    private Folder source;

    @Mock
    private Folder folder;

    @Mock
    private File file;

    @Mock
    private ResourceOperation<Resource> resourceOperation;

    @Test
    public void shouldNeedResources() throws Exception {
        assertThrows(IllegalArgumentException.class,
            () -> new ResourcesCollection<>(this.source, (Collection<Resource>) null));
    }

    @Test
    public void shouldNeedResourcesArray() throws Exception {
        assertThrows(IllegalArgumentException.class,
            () -> new ResourcesCollection<>(this.source, (Resource[]) null));
    }

    @Test
    public void shouldIterateCollection() throws Exception {
        ResourcesCollection<Resource> collection = new ResourcesCollection<>(this.source, Arrays.asList(this.folder, this.file));
        Iterator<Resource> iterator = collection.iterator();
        assertSame(this.folder, iterator.next());
        assertSame(this.file, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldIterateArray() throws Exception {
        ResourcesCollection<Resource> collection = new ResourcesCollection<>(this.source, this.folder, this.file);
        Iterator<Resource> iterator = collection.iterator();
        assertSame(this.folder, iterator.next());
        assertSame(this.file, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldDeleteAgainstItems() throws Exception {
        ResourcesCollection<Resource> collection = new ResourcesCollection<>(this.source, this.folder, this.file);
        collection.delete();
        InOrder ordered = Mockito.inOrder(this.folder, this.file);
        ordered.verify(this.folder).delete();
        ordered.verify(this.file).delete();
    }

    @Test
    public void shouldMoveToAgainstItems() throws Exception {
        ResourcesCollection<Resource> collection = new ResourcesCollection<>(this.source, this.folder, this.file);
        Folder destination = Mockito.mock(Folder.class);
        collection.moveTo(destination);
        InOrder ordered = Mockito.inOrder(this.folder, this.file);
        ordered.verify((Resource) this.folder).moveTo(destination);
        ordered.verify((Resource) this.file).moveTo(destination);
    }

    @Test
    public void shouldCopyToAgainstItems() throws Exception {
        ResourcesCollection<Resource> collection = new ResourcesCollection<>(this.source, this.folder, this.file);
        Folder destination = Mockito.mock(Folder.class);
        collection.copyTo(destination);
        InOrder ordered = Mockito.inOrder(this.folder, this.file);
        ordered.verify((Resource) this.folder).copyTo(destination);
        ordered.verify((Resource) this.file).copyTo(destination);
    }

    @Test
    public void shouldPerformOperationAgainstResourceItems() throws Exception {
        ResourcesCollection<Resource> collection = new ResourcesCollection<>(this.source, this.folder, this.file);
        collection.performOperation(this.resourceOperation);
        Mockito.verify(this.resourceOperation).perform(this.folder);
        Mockito.verify(this.resourceOperation).perform(this.file);
        Mockito.verifyNoMoreInteractions(this.resourceOperation);
    }

    @Test
    public void shouldFetchAll() throws Exception {
        ResourcesCollection<Resource> collection = new ResourcesCollection<>(this.source, this.folder, this.file);
        List<Resource> list = collection.fetchAll();
        assertEquals(2, list.size());
        assertSame(this.folder, list.get(0));
        assertSame(this.file, list.get(1));
    }

    @Test
    public void shouldSupportFiltering() throws Exception {
        File a = Mockito.mock(File.class);
        File b = Mockito.mock(File.class);
        File c = Mockito.mock(File.class);
        File d = Mockito.mock(File.class);
        File e = Mockito.mock(File.class);
        ResourcesCollection<File> collection = new ResourcesCollection<>(this.source, a, b, c, d, e);
        Iterator<File> actual = collection.include(filterOn(a), filterOn(c), filterOn(e)).exclude(filterOn(c)).iterator();
        assertSame(a, actual.next());
        assertSame(e, actual.next());
        assertFalse(actual.hasNext());
    }

    private ResourceFilter filterOn(final Resource on) {
        return new ResourceFilter() {

            @Override
            public boolean match(ResourceFilterContext context, Resource resource) {
                return resource == on;
            }
        };
    }
}
