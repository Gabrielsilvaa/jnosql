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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NumberReaderTest {

    private ValueReader valueReader;

    @BeforeEach
    public void init() {
        valueReader = new NumberReader();
    }

    @Test
    public void shouldValidateCompatibility() {
        assertTrue(valueReader.test(Number.class));
        assertFalse(valueReader.test(AtomicBoolean.class));
        assertFalse(valueReader.test(Boolean.class));
    }

    @Test
    public void shouldConvert() {
        assertEquals(10D, valueReader.read(Number.class, 10.00));
        assertEquals(10D, valueReader.read(Number.class, "10"));
    }


}
