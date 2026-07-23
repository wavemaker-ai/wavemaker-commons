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

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for {@link FilteredResources}.
 *
 * @author Phillip Webb
 */
@ExtendWith(MockitoExtension.class)
public class FilteredResourcesTest {

    @Mock
    private Folder source;

    @Mock
    private File fileA;

    @Mock
    private File fileB;

    @Mock
    private File fileC;

    @Mock
    private File fileD;

    private Resources<File> resources;

    @BeforeEach
    public void setup() {
        this.resources = new ResourcesCollection<>(this.source, this.fileA, this.fileB, this.fileC, this.fileD);
    }

    @Test
    public void shoulNotFilterAnyIncluded() throws Exception {
        Resources<File> actual = FilteredResources.include(this.resources, filterOn(this.fileA), filterOn(this.fileC));
        assertMatches(actual, this.fileA, this.fileC);
    }

    @Test
    public void shouldFilterEveryExcluded() throws Exception {
        Resources<File> actual = FilteredResources.exclude(this.resources, filterOn(this.fileA), filterOn(this.fileC));
        assertMatches(actual, this.fileB, this.fileD);
    }

    private void assertMatches(Resources<File> actual, File... expected) {
        Iterator<File> iterator = actual.iterator();
        for (File file : expected) {
            assertSame(file, iterator.next());
        }
        assertFalse(iterator.hasNext());
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
