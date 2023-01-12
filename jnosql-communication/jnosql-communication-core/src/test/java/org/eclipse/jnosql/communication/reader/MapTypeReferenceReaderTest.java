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
import org.eclipse.jnosql.communication.Value;
import org.eclipse.jnosql.communication.Entry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapTypeReferenceReaderTest {

    private final TypeReferenceReader referenceReader = new MapTypeReferenceReader();


    @Test
    public void shouldBeCompatible() {

        assertTrue(referenceReader.test(new TypeReference<Map<String, String>>(){}));
        assertTrue(referenceReader.test(new TypeReference<Map<Long, Integer>>(){}));

    }


    @Test
    public void shouldNotBeCompatible() {
        assertFalse(referenceReader.test(new TypeReference<ArrayList<BigDecimal>>(){}));
        assertFalse(referenceReader.test(new TypeReference<String>(){}));
        assertFalse(referenceReader.test(new TypeReference<Set<String>>(){}));
        assertFalse(referenceReader.test(new TypeReference<List<List<String>>>(){}));
        assertFalse(referenceReader.test(new TypeReference<Queue<String>>(){}));
        assertFalse(referenceReader.test(new TypeReference<Map<Integer, List<String>>>(){}));
    }

    @Test
    public void shouldConvert() {
        assertEquals(new HashMap<>(singletonMap("123", "123")), referenceReader.convert(new TypeReference<Map<String, String>>(){}, singletonMap(123, 123L)));
        assertEquals(singletonMap(123L, 123), referenceReader.convert(new TypeReference<Map<Long, Integer>>(){}, singletonMap("123", "123")));
    }

    @Test
    public void shouldCreateMutableMap() {
        Map<String, String> map = referenceReader.convert(new TypeReference<>() {
        }, singletonMap(123, 123L));
        map.put("23", "123");
        assertEquals(2, map.size());
    }


    @Test
    public void shouldMutableMap2() {
        Map<Integer, Long> oldMap = new HashMap<>();
        oldMap.put(1, 234L);
        oldMap.put(2, 2345L);
        Map<String, String> map = referenceReader.convert(new TypeReference<>() {
        }, oldMap);

        map.put("23", "123");

        assertEquals(3, map.size());
    }

    @Test
    public void shouldConvertEntryToMap() {
        Entry entry = new EntryTest("key", Value.of("value"));
        Map<String, String> map = referenceReader.convert(new TypeReference<>() {
        }, Collections.singletonList(entry));

        assertEquals(1, map.size());
        assertEquals("value", map.get("key"));
    }

    @Test
    public void shouldConvertSubEntryToMap() {
        Entry subEntry = new EntryTest("key", Value.of("value"));
        Entry entry = new EntryTest("key", Value.of(subEntry));

        Map<String, Map<String, String>> map = referenceReader.convert(new TypeReference<>() {
        }, Collections.singletonList(entry));
        assertEquals(1, map.size());
        Map<String, String> subMap = map.get("key");
        assertEquals("value", subMap.get("key"));
    }


    public static class EntryTest implements Entry {

        private final String name;

        private final Value value;

        public EntryTest(String name, Value value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Value value() {
            return value;
        }
    }
}