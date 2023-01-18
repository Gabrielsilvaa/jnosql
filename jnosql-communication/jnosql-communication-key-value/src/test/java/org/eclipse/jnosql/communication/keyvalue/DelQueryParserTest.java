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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DelQueryParserTest {

    private final DelQueryParser parser = new DelQueryParser();

    private final BucketManager manager = Mockito.mock(BucketManager.class);

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del \"Diana\""})
    public void shouldReturnParserQuery1(String query) {

        ArgumentCaptor<List<Object>> captor = ArgumentCaptor.forClass(List.class);

        parser.query(query, manager);

        Mockito.verify(manager).delete(captor.capture());
        List<Object> value = captor.getValue();

        assertEquals(1, value.size());
        assertThat(value).contains("Diana");
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del 12"})
    public void shouldReturnParserQuery2(String query) {

        ArgumentCaptor<List<Object>> captor = ArgumentCaptor.forClass(List.class);

        parser.query(query, manager);

        Mockito.verify(manager).delete(captor.capture());
        List<Object> value = captor.getValue();

        assertEquals(1, value.size());
        assertThat(value).contains(12L);
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del {\"Ana\" : \"Sister\", \"Maria\" : \"Mother\"}"})
    public void shouldReturnParserQuery3(String query) {

        ArgumentCaptor<List<Object>> captor = ArgumentCaptor.forClass(List.class);

        parser.query(query, manager);

        Mockito.verify(manager).delete(captor.capture());
        List<Object> value = captor.getValue();

        assertEquals(1, value.size());
        assertThat(value).contains("{\"Ana\":\"Sister\",\"Maria\":\"Mother\"}");
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del convert(\"2018-01-10\", java.time.LocalDate)"})
    public void shouldReturnParserQuery4(String query) {
        ArgumentCaptor<List<Object>> captor = ArgumentCaptor.forClass(List.class);

        parser.query(query, manager);

        Mockito.verify(manager).delete(captor.capture());
        List<Object> value = captor.getValue();

        assertEquals(1, value.size());

        assertThat(value).contains(LocalDate.parse("2018-01-10"));
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"remove @id"})
    public void shouldReturnErrorWhenUseParameterInQuery(String query) {
        assertThrows(QueryException.class, () -> parser.query(query, manager));
    }


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del @id"})
    public void shouldReturnErrorWhenDontBindParameters(String query) {

        KeyValuePreparedStatement prepare = parser.prepare(query, manager);
        assertThrows(QueryException.class, prepare::result);
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del @id"})
    public void shouldExecutePrepareStatement(String query) {

        ArgumentCaptor<List<Object>> captor = ArgumentCaptor.forClass(List.class);
        KeyValuePreparedStatement prepare = parser.prepare(query, manager);
        prepare.bind("id", 10);
        prepare.result();

        Mockito.verify(manager).delete(captor.capture());
        List<Object> value = captor.getValue();

        assertEquals(1, value.size());

        assertThat(value).contains(10);
    }


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del @id, @id2"})
    public void shouldExecutePrepareStatement2(String query) {

        ArgumentCaptor<List<Object>> captor = ArgumentCaptor.forClass(List.class);
        KeyValuePreparedStatement prepare = parser.prepare(query, manager);
        prepare.bind("id", 10);
        prepare.bind("id2", 11);
        prepare.result();

        Mockito.verify(manager).delete(captor.capture());
        List<Object> value = captor.getValue();

        assertEquals(2, value.size());

        assertThat(value).contains(10, 11);
    }

}