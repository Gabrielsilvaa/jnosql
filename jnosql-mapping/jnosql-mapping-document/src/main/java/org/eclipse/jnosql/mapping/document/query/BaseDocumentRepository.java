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
package org.eclipse.jnosql.mapping.document.query;

import jakarta.nosql.Params;
import jakarta.nosql.ServiceLoaderProvider;
import jakarta.nosql.Sort;
import jakarta.nosql.document.DeleteQueryConverter;
import jakarta.nosql.document.DocumentDeleteQuery;
import jakarta.nosql.document.DocumentDeleteQueryParams;
import jakarta.nosql.document.DocumentObserverParser;
import jakarta.nosql.document.DocumentQuery;
import jakarta.nosql.document.DocumentQueryParams;
import jakarta.nosql.document.SelectQueryConverter;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.Page;
import jakarta.nosql.mapping.Pagination;
import jakarta.nosql.mapping.document.DocumentQueryPagination;
import jakarta.nosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.document.MappingDocumentQuery;
import org.eclipse.jnosql.mapping.reflection.EntityMetadata;
import jakarta.nosql.query.DeleteQuery;
import jakarta.nosql.query.SelectQuery;
import org.eclipse.jnosql.mapping.repository.DynamicReturn;
import org.eclipse.jnosql.mapping.util.ParamsBinder;
import org.eclipse.jnosql.communication.query.method.DeleteMethodProvider;
import org.eclipse.jnosql.communication.query.method.SelectMethodProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class BaseDocumentRepository<T> {

    protected abstract Converters getConverters();

    protected abstract EntityMetadata getEntityMetadata();

    protected abstract DocumentTemplate getTemplate();

    private DocumentObserverParser parser;

    private ParamsBinder paramsBinder;


    protected DocumentQuery getQuery(Method method, Object[] args) {
        SelectMethodProvider methodProvider = SelectMethodProvider.get();
        SelectQuery selectQuery = methodProvider.apply(method, getEntityMetadata().getName());
        SelectQueryConverter converter = ServiceLoaderProvider.get(SelectQueryConverter.class,
                ()-> ServiceLoader.load(SelectQueryConverter.class));
        DocumentQueryParams queryParams = converter.apply(selectQuery, getParser());
        DocumentQuery query = queryParams.getQuery();
        Params params = queryParams.getParams();
        getParamsBinder().bind(params, args, method);
        return getQuerySorts(args, query);
    }

    protected DocumentQuery getQuerySorts(Object[] args, DocumentQuery query) {
        List<Sort> sorts = DynamicReturn.findSorts(args);
        if (!sorts.isEmpty()) {
            List<Sort> newOrders = new ArrayList<>();
            newOrders.addAll(query.getSorts());
            newOrders.addAll(sorts);
            return new MappingDocumentQuery(newOrders, query.getLimit(), query.getSkip(),
                    query.getCondition().orElse(null), query.getDocumentCollection());
        }
        return query;
    }

    protected DocumentDeleteQuery getDeleteQuery(Method method, Object[] args) {
        DeleteMethodProvider methodProvider = DeleteMethodProvider.get();
        DeleteQuery deleteQuery = methodProvider.apply(method, getEntityMetadata().getName());
        DeleteQueryConverter converter = ServiceLoaderProvider.get(DeleteQueryConverter.class,
                ()-> ServiceLoader.load(DeleteQueryConverter.class));
        DocumentDeleteQueryParams queryParams = converter.apply(deleteQuery, getParser());
        DocumentDeleteQuery query = queryParams.getQuery();
        Params params = queryParams.getParams();
        getParamsBinder().bind(params, args, method);
        return query;
    }


    protected DocumentObserverParser getParser() {
        if (parser == null) {
            this.parser = new RepositoryDocumentObserverParser(getEntityMetadata());
        }
        return parser;
    }

    protected ParamsBinder getParamsBinder() {
        if (Objects.isNull(paramsBinder)) {
            this.paramsBinder = new ParamsBinder(getEntityMetadata(), getConverters());
        }
        return paramsBinder;
    }

    protected Object executeQuery(Method method, Object[] args, Class<?> typeClass, DocumentQuery query) {
        DynamicReturn<?> dynamicReturn = DynamicReturn.builder()
                .withClassSource(typeClass)
                .withMethodSource(method)
                .withResult(() -> getTemplate().select(query))
                .withSingleResult(() -> getTemplate().singleResult(query))
                .withPagination(DynamicReturn.findPagination(args))
                .withStreamPagination(listPagination(query))
                .withSingleResultPagination(getSingleResult(query))
                .withPage(getPage(query))
                .build();
        return dynamicReturn.execute();
    }

    protected Function<Pagination, Page<T>> getPage(DocumentQuery query) {
        return p -> getTemplate().select(DocumentQueryPagination.of(query, p));
    }

    protected Function<Pagination, Optional<T>> getSingleResult(DocumentQuery query) {
        return p -> {
            DocumentQuery queryPagination = DocumentQueryPagination.of(query, p);
            return getTemplate().singleResult(queryPagination);
        };
    }

    protected Function<Pagination, Stream<T>> listPagination(DocumentQuery query) {
        return p -> {
            DocumentQuery queryPagination = DocumentQueryPagination.of(query, p);
            return getTemplate().select(queryPagination);
        };
    }

}
