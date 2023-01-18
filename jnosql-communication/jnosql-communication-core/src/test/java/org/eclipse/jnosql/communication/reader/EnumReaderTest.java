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

import org.eclipse.jnosql.communication.ValueReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnumReaderTest {

    private ValueReader valueReader;

    @BeforeEach
    public void init() {
        valueReader = new EnumReader();
    }

    @Test
    public void shouldValidateCompatibility() {
        assertTrue(valueReader.test(Enum.class));
        assertTrue(valueReader.test(ExampleNumber.class));
        assertFalse(valueReader.test(AtomicBoolean.class));
    }

    @Test
    public void shouldConvert() {
        ExampleNumber value = ExampleNumber.ONE;
        assertEquals(value, valueReader.read(ExampleNumber.class, value));
        assertEquals(value, valueReader.read(ExampleNumber.class, 0));
        assertEquals(value, valueReader.read(ExampleNumber.class, "ONE"));
        assertEquals(ExampleNumber.TWO, valueReader.read(ExampleNumber.class, 1));
        assertEquals(ExampleNumber.TWO, valueReader.read(ExampleNumber.class, "TWO"));
    }

    @Test
    public void shouldReturnErrorInIndex() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> valueReader.read(ExampleNumber.class, 10));
    }

    @Test
    public void shouldReturnErrorInName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> valueReader.read(ExampleNumber.class, "FOUR"));
    }


    enum ExampleNumber {
        ONE, TWO
    }


}
