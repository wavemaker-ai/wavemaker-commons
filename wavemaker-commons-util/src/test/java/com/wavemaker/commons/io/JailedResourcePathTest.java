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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link JailedResourcePath}.
 *
 * @author Phillip Webb
 */
public class JailedResourcePathTest {

    private final ResourcePath pathA = new ResourcePath().get("a");

    private final ResourcePath pathB = new ResourcePath().get("b");

    @Test
    public void shouldNeedJail() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> new JailedResourcePath(null, new ResourcePath()));
    }

    @Test
    public void shouldNeedPath() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> new JailedResourcePath(new ResourcePath(), null));
    }

    @Test
    public void shouldCreateFromJailAndPath() throws Exception {
        JailedResourcePath jailedResourcePath = new JailedResourcePath(this.pathA, this.pathB);
        assertEquals(this.pathA, jailedResourcePath.getJailPath());
        assertEquals(this.pathB, jailedResourcePath.getPath());
    }

    @Test
    public void shouldCreateRoot() throws Exception {
        JailedResourcePath jailedResourcePath = new JailedResourcePath();
        assertTrue(jailedResourcePath.getJailPath().isRootPath());
        assertTrue(jailedResourcePath.getPath().isRootPath());
    }

    @Test
    public void shouldGetNullParent() throws Exception {
        JailedResourcePath jailedResourcePath = new JailedResourcePath(this.pathA, new ResourcePath());
        assertNull(jailedResourcePath.getParent());
    }

    @Test
    public void shouldGetParent() throws Exception {
        JailedResourcePath jailedResourcePath = new JailedResourcePath(this.pathA, new ResourcePath().get("b/c"));
        JailedResourcePath expected = new JailedResourcePath(this.pathA, this.pathB);
        assertEquals(expected, jailedResourcePath.getParent());
    }

    @Test
    public void shouldGet() throws Exception {
        JailedResourcePath jailedResourcePath = new JailedResourcePath(this.pathA, new ResourcePath());
        jailedResourcePath = jailedResourcePath.get("b/c");
        JailedResourcePath expected = new JailedResourcePath(this.pathA, new ResourcePath().get("b/c"));
        assertEquals(expected, jailedResourcePath);

    }

    @Test
    public void shouldGetUnjailedRootPath() throws Exception {
        JailedResourcePath jailedResourcePath = new JailedResourcePath(new ResourcePath(), this.pathA);
        assertEquals(new ResourcePath().get("a"), jailedResourcePath.getUnjailedPath());
    }

    @Test
    public void shouldGetUnjailedPath() throws Exception {
        JailedResourcePath jailedResourcePath = new JailedResourcePath(this.pathA, this.pathB);
        assertEquals(new ResourcePath().get("a/b"), jailedResourcePath.getUnjailedPath());
    }

    @Test
    public void shouldUsePathToString() throws Exception {
        JailedResourcePath jailedResourcePath = new JailedResourcePath(this.pathA, this.pathB);
        assertEquals("/b", jailedResourcePath.toString());
    }
}
