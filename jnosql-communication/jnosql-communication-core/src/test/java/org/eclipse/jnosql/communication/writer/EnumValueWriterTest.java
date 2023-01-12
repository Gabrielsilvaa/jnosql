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
package org.eclipse.jnosql.communication.writer;

import org.eclipse.jnosql.communication.ValueWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnumValueWriterTest {
    private ValueWriter<Enum<?>, String> valueWriter;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp() {
        valueWriter = new EnumValueWriter();
    }

    @Test
    public void shouldVerifyCompatibility() {
        assertTrue(valueWriter.test(Month.class));
        assertTrue(valueWriter.test(DayOfWeek.class));
    }

    @Test
    public void shouldNotVerifyCompatibility() {
        assertFalse(valueWriter.test(Integer.class));
        assertFalse(valueWriter.test(List.class));
    }

    @Test
    public void shouldConvert() {
        String result = valueWriter.write(JANUARY);
        assertEquals(JANUARY.name(), result);
    }
}