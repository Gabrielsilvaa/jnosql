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

public class CharacterReaderTest {

    private ValueReader valueReader;

    @BeforeEach
    public void init() {
        valueReader = new CharacterReader();
    }

    @Test
    public void shouldValidateCompatibility() {
        assertTrue(valueReader.test(Character.class));
        assertFalse(valueReader.test(AtomicBoolean.class));
    }

    @Test
    public void shouldConvert() {
        Character character = 'o';
        assertEquals(character, valueReader.read(Character.class, character));
        assertEquals(Character.valueOf((char) 10), valueReader.read(Character.class, 10.00));
        assertEquals(Character.valueOf('1'), valueReader.read(Character.class, "10"));
        assertEquals(Character.valueOf(Character.MIN_VALUE), valueReader.read(Character.class, ""));
    }


}
