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
package org.eclipse.jnosql.communication.column.query;

import jakarta.nosql.Params;
import jakarta.nosql.QueryException;
import jakarta.nosql.column.ColumnCondition;
import jakarta.nosql.column.ColumnDeleteQuery;
import jakarta.nosql.column.ColumnDeleteQueryParams;
import jakarta.nosql.column.ColumnEntity;
import jakarta.nosql.column.ColumnManager;
import jakarta.nosql.column.ColumnObserverParser;
import jakarta.nosql.column.ColumnPreparedStatement;
import jakarta.nosql.column.DeleteQueryConverter;
import jakarta.nosql.query.DeleteQuery;
import jakarta.nosql.query.DeleteQuery.DeleteQueryProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation of {@link DeleteQueryConverter}
 */
public final class DeleteQueryParser implements DeleteQueryConverter {

    private final DeleteQueryProvider deleteQueryProvider;
    private final CacheQuery<ColumnDeleteQuery> cache;

    public DeleteQueryParser() {
        this.deleteQueryProvider = DeleteQuery.getProvider();
        cache = new CacheQuery<>(this::getQuery);
    }

    Stream<ColumnEntity> query(String query, ColumnManager manager, ColumnObserverParser observer) {

        ColumnDeleteQuery columnDeleteQuery = cache.get(query, observer);
        manager.delete(columnDeleteQuery);
        return Stream.empty();
    }


    ColumnPreparedStatement prepare(String query, ColumnManager manager,
                                    ColumnObserverParser observer) {
        Params params = Params.newParams();
        ColumnDeleteQuery columnDeleteQuery = getQuery(query, params, observer);
        return DefaultColumnPreparedStatement.delete(columnDeleteQuery, params, query, manager);
    }



    @Override
    public ColumnDeleteQueryParams apply(DeleteQuery deleteQuery,
                                         ColumnObserverParser columnObserverParser) {

        requireNonNull(deleteQuery, "deleteQuery is required");
        requireNonNull(columnObserverParser, "columnObserverParser is required");
        Params params = Params.newParams();
        ColumnDeleteQuery query = getQuery(params, columnObserverParser, deleteQuery);
        return new DefaultColumnDeleteQueryParams(query, params);
    }

    private ColumnDeleteQuery getQuery(String query, Params params, ColumnObserverParser observer) {
        DeleteQuery deleteQuery = deleteQueryProvider.apply(query);

        return getQuery(params, observer, deleteQuery);
    }

    private ColumnDeleteQuery getQuery(Params params, ColumnObserverParser observer, DeleteQuery deleteQuery) {
        String columnFamily = observer.fireEntity(deleteQuery.getEntity());
        List<String> columns = deleteQuery.getFields().stream()
                .map(f -> observer.fireField(columnFamily, f))
                .collect(Collectors.toList());
        ColumnCondition condition = null;

        if (deleteQuery.getWhere().isPresent()) {
            condition = deleteQuery.getWhere().map(c -> Conditions.getCondition(c, params, observer, columnFamily)).get();
        }

        return new DefaultColumnDeleteQuery(columnFamily, condition, columns);
    }

    private ColumnDeleteQuery getQuery(String query, ColumnObserverParser observer) {
        DeleteQuery deleteQuery = deleteQueryProvider.apply(query);

        String columnFamily = observer.fireEntity(deleteQuery.getEntity());
        List<String> columns = deleteQuery.getFields().stream()
                .map(f -> observer.fireField(columnFamily, f))
                .collect(Collectors.toList());
        ColumnCondition condition = null;
        Params params = Params.newParams();

        if (deleteQuery.getWhere().isPresent()) {
            condition = deleteQuery.getWhere().map(c -> Conditions.getCondition(c, params, observer, columnFamily)).get();
        }

        if (params.isNotEmpty()) {
            throw new QueryException("To run a query with a parameter use a PrepareStatement instead.");
        }
        return new DefaultColumnDeleteQuery(columnFamily, condition, columns);
    }
}