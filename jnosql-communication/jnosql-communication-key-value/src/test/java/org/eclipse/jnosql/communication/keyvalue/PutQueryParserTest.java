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
package org.eclipse.jnosql.communication.keyvalue;

import org.eclipse.jnosql.communication.QueryException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PutQueryParserTest {

    private final PutQueryParser parser = new PutQueryParser();

    private final BucketManager manager = Mockito.mock(BucketManager.class);

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {\"Diana\", \"Hunt\"}"})
    public void shouldReturnParserQuery1(String query) {

        ArgumentCaptor<KeyValueEntity> captor = ArgumentCaptor.forClass(KeyValueEntity.class);

        parser.query(query, manager);

        Mockito.verify(manager).put(captor.capture());
        KeyValueEntity entity = captor.getValue();

        assertEquals("Diana", entity.key());
        assertEquals("Hunt", entity.value());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {\"Diana\", \"goddess of hunt\", 10 hour}"})
    public void shouldReturnParserQuery2(String query) {

        ArgumentCaptor<KeyValueEntity> captor = ArgumentCaptor.forClass(KeyValueEntity.class);
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);

        parser.query(query, manager);

        Mockito.verify(manager).put(captor.capture(), durationCaptor.capture());
        KeyValueEntity entity = captor.getValue();
        Duration ttl = durationCaptor.getValue();

        assertEquals(Duration.ofHours(10), ttl);
        assertEquals("Diana", entity.key());
        assertEquals("goddess of hunt", entity.value());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {\"Diana\", @value}"})
    public void shouldReturnErrorWhenUseParameterInQuery(String query) {
        assertThrows(QueryException.class, () -> parser.query(query, manager));
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {\"Diana\", @value}"})
    public void shouldReturnErrorWhenDontBindParameters(String query) {

        KeyValuePreparedStatement prepare = parser.prepare(query, manager);
        assertThrows(QueryException.class, prepare::result);
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {\"Diana\", @value}"})
    public void shouldExecutePrepareStatement(String query) {
        KeyValuePreparedStatement prepare = parser.prepare(query, manager);
        prepare.bind("value", "Hunt");
        prepare.result();
        ArgumentCaptor<KeyValueEntity> captor = ArgumentCaptor.forClass(KeyValueEntity.class);

        Mockito.verify(manager).put(captor.capture());
        KeyValueEntity entity = captor.getValue();

        assertEquals("Diana", entity.key());
        assertEquals("Hunt", entity.value());
    }
}