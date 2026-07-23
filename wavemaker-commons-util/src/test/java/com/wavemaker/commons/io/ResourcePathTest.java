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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ResourcePath}.
 *
 * @author Phillip Webb
 */
public class ResourcePathTest {

    @Test
    public void shouldCreateRoot() throws Exception {
        ResourcePath path = new ResourcePath();
        assertEquals("", path.getName());
        assertEquals("", path.toString());
    }

    @Test
    public void shouldNotCreateNullNested() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> new ResourcePath().get(null));
    }

    @Test
    public void shouldNotCreateEmptyNested() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> new ResourcePath().get(""));
    }

    @Test
    public void shouldGetSimpleNested() throws Exception {
        ResourcePath path = new ResourcePath().get("a");
        assertEquals("a", path.getName());
        assertEquals("/a", path.toString());
    }

    @Test
    public void shouldGetDoubleNested() throws Exception {
        ResourcePath path = new ResourcePath().get("a").get("b");
        assertEquals("b", path.getName());
        assertEquals("/a/b", path.toString());
    }

    @Test
    public void shouldGetNestedString() throws Exception {
        ResourcePath path = new ResourcePath().get("a/b");
        assertEquals("b", path.getName());
        assertEquals("/a/b", path.toString());
    }

    @Test
    public void shouldGetRelative() throws Exception {
        ResourcePath path = new ResourcePath().get("a/b/../c");
        assertEquals("c", path.getName());
        assertEquals("/a/c", path.toString());
    }

    @Test
    public void shouldGetRelativeAtEnd() throws Exception {
        ResourcePath path = new ResourcePath().get("a/b/c/d/../..");
        assertEquals("b", path.getName());
        assertEquals("/a/b", path.toString());

    }

    @Test
    public void shouldNotAllowRelativePastRoot() throws Exception {
        assertThrows(IllegalStateException.class, () -> new ResourcePath().get("a/b/../../.."));
    }

    @Test
    public void shouldSupportSlashAtFront() throws Exception {
        ResourcePath path = new ResourcePath().get("a").get("/b");
        assertEquals("b", path.getName());
        assertEquals("/b", path.toString());
    }

    @Test
    public void shouldSupportSlashAtEnd() throws Exception {
        ResourcePath path = new ResourcePath().get("a/b/");
        assertEquals("b", path.getName());
        assertEquals("/a/b", path.toString());
    }

    @Test
    public void shouldIgnoreDoubleSlash() throws Exception {
        ResourcePath path = new ResourcePath().get("a///b/");
        assertEquals("b", path.getName());
        assertEquals("/a/b", path.toString());
    }

    @Test
    public void shouldGetParent() throws Exception {
        ResourcePath path = new ResourcePath().get("a/b/").getParent();
        assertEquals("a", path.getName());
        assertEquals("/a", path.toString());
    }

    @Test
    public void shouldBeRootPath() throws Exception {
        ResourcePath path = new ResourcePath();
        assertTrue(path.isRootPath());
    }

    @Test
    public void shouldNoteBeRootPath() throws Exception {
        ResourcePath path = new ResourcePath().get("a");
        assertFalse(path.isRootPath());
    }

    @Test
    public void shouldAppendPath() throws Exception {
        ResourcePath root = new ResourcePath();
        ResourcePath ab = new ResourcePath().get("a/b");
        ResourcePath cd = new ResourcePath().get("c/d");
        assertEquals(ab, root.append(ab));
        assertEquals(ab, ab.append(root));
        assertEquals(new ResourcePath().get("a/b/c/d"), ab.append(cd));
    }

    @Test
    public void shouldReturnToStringRelativeToOther() throws Exception {
        ResourcePath abcd = new ResourcePath().get("a/b/c/d");
        assertEquals("c/d", abcd.toStringRelativeTo("/a/b/"));
    }

    @Test
    public void shouldReturnEmptyToStringRelativeToSelf() throws Exception {
        ResourcePath ab = new ResourcePath().get("a/b");
        assertEquals("", ab.toStringRelativeTo("/a/b/"));
    }

    @Test
    public void shouldNotReturnToStringRelativeToNonParent() throws Exception {
        ResourcePath abcd = new ResourcePath().get("a/b/c/d");
        assertThrows(IllegalArgumentException.class, () -> abcd.toStringRelativeTo("/a/c/"));
    }

}
