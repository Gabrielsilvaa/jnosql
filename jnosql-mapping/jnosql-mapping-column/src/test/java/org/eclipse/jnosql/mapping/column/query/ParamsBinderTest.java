/*
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
 */
package org.eclipse.jnosql.mapping.column.query;

import jakarta.nosql.Params;
import jakarta.nosql.ServiceLoaderProvider;
import jakarta.nosql.TypeReference;
import jakarta.nosql.Value;
import org.eclipse.jnosql.communication.column.Column;
import jakarta.nosql.column.ColumnCondition;
import jakarta.nosql.column.ColumnQuery;
import jakarta.nosql.column.ColumnQueryParams;
import jakarta.nosql.column.SelectQueryConverter;
import org.eclipse.jnosql.mapping.Converters;
import org.eclipse.jnosql.mapping.reflection.EntityMetadata;
import org.eclipse.jnosql.mapping.reflection.EntitiesMetadata;
import jakarta.nosql.query.SelectQuery;
import jakarta.nosql.tck.entities.Person;
import jakarta.nosql.tck.test.CDIExtension;
import org.eclipse.jnosql.mapping.util.ParamsBinder;
import org.eclipse.jnosql.communication.query.method.SelectMethodProvider;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CDIExtension
class ParamsBinderTest {


    @Inject
    private EntitiesMetadata mappings;

    @Inject
    private Converters converters;

    private ParamsBinder paramsBinder;

    @Test
    public void shouldConvert() {

        Method method = Stream.of(PersonRepository.class.getMethods())
                .filter(m -> m.getName().equals("findByAge")).findFirst().get();
        EntityMetadata entityMetadata = mappings.get(Person.class);
        RepositoryColumnObserverParser parser = new RepositoryColumnObserverParser(entityMetadata);
        paramsBinder = new ParamsBinder(entityMetadata, converters);

        SelectMethodProvider selectMethodFactory = SelectMethodProvider.get();
        SelectQuery selectQuery = selectMethodFactory.apply(method, entityMetadata.getName());
        SelectQueryConverter converter = ServiceLoaderProvider.get(SelectQueryConverter.class,
                ()-> ServiceLoader.load(SelectQueryConverter.class));
        ColumnQueryParams columnQueryParams = converter.apply(selectQuery, parser);
        Params params = columnQueryParams.getParams();
        Object[] args = {10};
        paramsBinder.bind(params, args, method);
        ColumnQuery query = columnQueryParams.getQuery();
        ColumnCondition columnCondition = query.getCondition().get();
        Value value = columnCondition.getColumn().getValue();
        assertEquals(10, value.get());

    }

    @Test
    public void shouldConvert2() {

        Method method = Stream.of(PersonRepository.class.getMethods())
                .filter(m -> m.getName().equals("findByAgeAndName")).findFirst().get();
        EntityMetadata entityMetadata = mappings.get(Person.class);
        RepositoryColumnObserverParser parser = new RepositoryColumnObserverParser(entityMetadata);
        paramsBinder = new ParamsBinder(entityMetadata, converters);

        SelectMethodProvider selectMethodFactory = SelectMethodProvider.get();
        SelectQuery selectQuery = selectMethodFactory.apply(method, entityMetadata.getName());
        SelectQueryConverter converter = ServiceLoaderProvider.get(SelectQueryConverter.class,
                ()-> ServiceLoader.load(SelectQueryConverter.class));
        ColumnQueryParams queryParams = converter.apply(selectQuery, parser);
        Params params = queryParams.getParams();
        paramsBinder.bind(params, new Object[]{10L, "Ada"}, method);
        ColumnQuery query = queryParams.getQuery();
        ColumnCondition columnCondition = query.getCondition().get();
        List<ColumnCondition> conditions = columnCondition.getColumn().get(new TypeReference<>() {
        });
        List<Object> values = conditions.stream().map(ColumnCondition::getColumn)
                .map(Column::getValue)
                .map(Value::get).collect(Collectors.toList());
        assertEquals(10, values.get(0));
        assertEquals("Ada", values.get(1));

    }


    interface PersonRepository {

        List<Person> findByAge(Integer age);

        List<Person> findByAgeAndName(Long age, String name);
    }


}