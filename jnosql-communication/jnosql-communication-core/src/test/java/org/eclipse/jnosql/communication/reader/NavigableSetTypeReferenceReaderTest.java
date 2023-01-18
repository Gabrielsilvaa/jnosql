/*
 *
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 *
 */
package org.eclipse.jnosql.communication.reader;

import org.eclipse.jnosql.communication.TypeReference;
import org.eclipse.jnosql.communication.TypeReferenceReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class NavigableSetTypeReferenceReaderTest {

    private final TypeReferenceReader referenceReader = new NavigableSetTypeReferenceReader();

    @Test
    public void shouldBeCompatible() {

        assertTrue(referenceReader.test(new TypeReference<SortedSet<String>>() {
        }));
        assertTrue(referenceReader.test(new TypeReference<SortedSet<Long>>() {
        }));
        assertTrue(referenceReader.test(new TypeReference<NavigableSet<String>>() {
        }));
        assertTrue(referenceReader.test(new TypeReference<NavigableSet<Long>>() {
        }));
    }


    @Test
    public void shouldNotBeCompatible() {

        assertFalse(referenceReader.test(new TypeReference<SortedSet<Animal>>() {
        }));
        assertFalse(referenceReader.test(new TypeReference<NavigableSet<Animal>>() {
        }));

        assertFalse(referenceReader.test(new TypeReference<ArrayList<BigDecimal>>() {
        }));
        assertFalse(referenceReader.test(new TypeReference<String>() {
        }));
        assertFalse(referenceReader.test(new TypeReference<Set<String>>() {
        }));
        assertFalse(referenceReader.test(new TypeReference<List<List<String>>>() {
        }));
        assertFalse(referenceReader.test(new TypeReference<Queue<String>>() {
        }));
        assertFalse(referenceReader.test(new TypeReference<Map<Integer, String>>() {
        }));
    }


    @Test
    public void shouldConvert() {
        assertEquals(new TreeSet<>(singletonList("123")), referenceReader.convert(new TypeReference<SortedSet<String>>() {
        }, "123"));
        assertEquals(new TreeSet<>(singletonList(123L)), referenceReader.convert(new TypeReference<SortedSet<Long>>() {
        }, "123"));

        assertEquals(new TreeSet<>(singletonList("123")), referenceReader.convert(new TypeReference<NavigableSet<String>>() {
        }, "123"));
        assertEquals(new TreeSet<>(singletonList(123L)), referenceReader.convert(new TypeReference<NavigableSet<Long>>() {
        }, "123"));
    }

    @Test
    public void shouldConvertAndBeMutable() {
        NavigableSet<String> strings = referenceReader.convert(new TypeReference<>() {
        }, "123");
        strings.add("456");
        Assertions.assertEquals(2, strings.size());
    }

    @Test
    public void shouldConvertAndBeMutable2() {
        NavigableSet<String> strings = referenceReader.convert(new TypeReference<>() {
        }, Arrays.asList("123", "32"));
        strings.add("456");
        Assertions.assertEquals(3, strings.size());
    }

    private static class Animal {

    }

}