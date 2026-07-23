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
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link FilteredIterator}.
 *
 * @author Phillip Webb
 */
public class FilteredIteratorTest {

    private static final Collection<Integer> NUMBERS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

    @Test
    public void shouldFilter() {
        EvenNumbersIterator evenNumbers = new EvenNumbersIterator(NUMBERS.iterator());
        assertTrue(evenNumbers.hasNext());
        assertEquals(0, evenNumbers.next());
        assertTrue(evenNumbers.hasNext());
        assertEquals(2, evenNumbers.next());
        assertTrue(evenNumbers.hasNext());
        assertEquals(4, evenNumbers.next());
        assertTrue(evenNumbers.hasNext());
        assertEquals(6, evenNumbers.next());
        assertTrue(evenNumbers.hasNext());
        assertEquals(8, evenNumbers.next());
        assertFalse(evenNumbers.hasNext());
    }

    @Test
    public void shouldThrowNoSuchElementException() throws Exception {
        EvenNumbersIterator evenNumbers = new EvenNumbersIterator(NUMBERS.iterator());
        while (evenNumbers.hasNext()) {
            evenNumbers.next();
        }
        assertThrows(NoSuchElementException.class, () -> evenNumbers.next());
    }

    @Test
    public void shouldSupportMultipleCallsToHasNext() throws Exception {
        EvenNumbersIterator evenNumbers = new EvenNumbersIterator(NUMBERS.iterator());
        int i = 0;
        while (evenNumbers.hasNext()) {
            assertTrue(evenNumbers.hasNext());
            assertEquals(i, evenNumbers.next());
            i += 2;
        }
        assertFalse(evenNumbers.hasNext());
        assertFalse(evenNumbers.hasNext());
    }

    private static class EvenNumbersIterator extends FilteredIterator<Integer> {

        public EvenNumbersIterator(Iterator<Integer> sourceIterator) {
            super(sourceIterator);
        }

        @Override
        protected boolean isElementFiltered(Integer element) {
            return element % 2 != 0;
        }
    }

}
