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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.wavemaker.commons.io.FilterOn.PathStyle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;


/**
 * Tests for {@link FilterOn}.
 *
 * @author Phillip Webb
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FilterOnTest {

    @Mock
    private ResourceFilterContext context;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void shouldSupportCompoundFilters() throws Exception {
        ResourceFilter filter = FilterOn.names().starting("~").ending(".tmp", ".bak").notContaining("keep");
        assertTrue(filter.match(this.context, fileWithName("~file.tmp")));
        assertTrue(filter.match(this.context, fileWithName("~file.bak")));
        assertFalse(filter.match(this.context, fileWithName("file.tmp")));
        assertFalse(filter.match(this.context, fileWithName("~file.dat")));
        assertFalse(filter.match(this.context, fileWithName("~xxkeepxx.bak")));
    }

    @Test
    public void shouldFilterHiddenResources() throws Exception {
        ResourceFilter filter = FilterOn.hidden();
        assertTrue(filter.match(this.context, fileWithName(".hidden")));
        assertFalse(filter.match(this.context, fileWithName("nothidden")));
        assertTrue(filter.match(this.context, folderWithName(".hidden")));
        assertFalse(filter.match(this.context, folderWithName("nothidden")));
    }

    @Test
    public void shouldFilterNonHiddenResources() throws Exception {
        ResourceFilter filter = FilterOn.nonHidden();
        assertFalse(filter.match(this.context, fileWithName(".hidden")));
        assertTrue(filter.match(this.context, fileWithName("nothidden")));
        assertFalse(filter.match(this.context, folderWithName(".hidden")));
        assertTrue(filter.match(this.context, folderWithName("nothidden")));
    }

    @Test
    public void shouldFilterOnPatternWithFullPathStyle() throws Exception {
        ResourceFilter filter = FilterOn.antPattern(PathStyle.FULL, "/dojo/**/tests/**");
        assertTrue(filter.match(this.context, folderWithPath("/dojo/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/another/file.js")));
    }

    @Test
    public void shouldFilterOnPattern() throws Exception {
        Folder source = folderWithPath("/x/");
        BDDMockito.given(this.context.getSource()).willReturn(source);
        ResourceFilter filter = FilterOn.antPattern("dojo/**/tests/**");
        assertTrue(filter.match(this.context, folderWithPath("/x/dojo/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/x/dojo/some/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/x/dojo/some/folder/tests/another/file.js")));
    }

    @Test
    public void shouldFilterOnPatternWithExtraSlash() throws Exception {
        Folder source = folderWithPath("/");
        BDDMockito.given(this.context.getSource()).willReturn(source);
        ResourceFilter filter = FilterOn.antPattern("/dojo/**/tests/**");
        assertTrue(filter.match(this.context, folderWithPath("/dojo/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/another/file.js")));
    }

    @Test
    public void testCaseSensitiveNames() throws Exception {
        ResourceFilter filter = FilterOn.caseSensitiveNames().starting("~").ending(".tmp", ".bak");
        assertTrue(filter.match(this.context, fileWithName("~file.tmp")));
        assertTrue(filter.match(this.context, fileWithName("~file.bak")));
        assertFalse(filter.match(this.context, fileWithName("file.tmp")));
        assertFalse(filter.match(this.context, fileWithName("~file.dat")));
        assertTrue(filter.match(this.context, fileWithName("~xxkeepxx.bak")));
    }

    @Test
    public void testPaths() {
        ResourceFilter filter = FilterOn.paths();
        assertTrue(filter.match(this.context, folderWithPath("/dojo/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/another/file.js")));
    }

    @Test
    public void testPaths1() {
        ResourceFilter filter = FilterOn.paths(PathStyle.FULL);
        assertTrue(filter.match(this.context, folderWithPath("/dojo/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/another/file.js")));

    }

    @Test
    public void testPaths2() {
        ResourceFilter filter = FilterOn.caseSensitivePaths(PathStyle.FULL);
        assertTrue(filter.match(this.context, folderWithPath("/dojo/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/another/file.js")));

    }

    @Test
    public void testPaths3() {
        ResourceFilter filter = FilterOn.caseSensitivePaths();
        assertTrue(filter.match(this.context, folderWithPath("/dojo/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/file.js")));
        assertTrue(filter.match(this.context, folderWithPath("/dojo/some/folder/tests/another/file.js")));

    }

    @Test
    public void testCaseInSensitiveNames() {
        ResourceFilter filter = FilterOn.names().matching("file.tmp");
        assertTrue(filter.match(this.context, fileWithName("FiLe.tmp")));
        assertFalse(filter.match(this.context, fileWithName("~file.bak")));
        assertTrue(filter.match(this.context, fileWithName("file.tmp")));
        assertFalse(filter.match(this.context, fileWithName("~file.dat")));
    }

    private File fileWithName(String name) {
        return resourceWithName(File.class, name, null);
    }

    private Folder folderWithName(String name) {
        return resourceWithName(Folder.class, name, null);
    }

    private Folder folderWithPath(String path) {
        String name = new ResourcePath().get(path).getName();
        return resourceWithName(Folder.class, name, path);
    }

    private <T extends Resource> T resourceWithName(Class<T> resourceType, String name, final String path) {
        T resource = Mockito.mock(resourceType);
        BDDMockito.given(resource.getName()).willReturn(name);
        BDDMockito.given(resource.toString()).willReturn(path);
        BDDMockito.given(resource.toStringRelativeTo(any(Folder.class))).willAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                final ResourcePath resourcePath = new ResourcePath().get(path);
                return resourcePath.toStringRelativeTo(invocation.getArguments()[0].toString());
            }
        });
        return resource;
    }
}
