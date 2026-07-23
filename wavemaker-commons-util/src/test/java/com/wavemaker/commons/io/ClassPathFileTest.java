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
import org.springframework.core.io.ClassPathResource;

import com.wavemaker.commons.io.exception.ResourceDoesNotExistException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link ClassPathFile}.
 *
 * @author Phillip Webb
 */
public class ClassPathFileTest {

    @Test
    public void shouldLoadRelativeToClass() throws Exception {
        File file = new ClassPathFile(getClass(), "a.txt");
        assertEquals("a", file.getContent().asString());
    }

    @Test
    public void shouldLoadFromPathUsingClass() throws Exception {
        File file = new ClassPathFile(getClass(), "/com/wavemaker/commons/io/a.txt");
        assertEquals("a", file.getContent().asString());
    }

    @Test
    public void shouldLoadUsingExactPath() throws Exception {
        ClassPathResource resource = new ClassPathResource("/com/wavemaker/commons/io/a.txt");
        System.out.println(resource.getInputStream());
        File file = new ClassPathFile("/com/wavemaker/commons/io/a.txt");
        assertEquals("a", file.getContent().asString());
    }

    @Test
    public void shouldSupportNotExists() throws Exception {
        File file = new ClassPathFile("/com/wavemaker/platform/io/missing.txt");
        assertFalse(file.exists());
    }

    @Test
    public void shouldThrowOnLoadNotExists() throws Exception {
        File file = new ClassPathFile(getClass(), "missing.txt");
        assertThrows(ResourceDoesNotExistException.class, () -> file.getContent().asString());
    }

    @Test
    public void testGetName() {
        File file = new ClassPathFile(getClass(), "a.txt");
        assertEquals("a.txt", file.getName());
    }
}
