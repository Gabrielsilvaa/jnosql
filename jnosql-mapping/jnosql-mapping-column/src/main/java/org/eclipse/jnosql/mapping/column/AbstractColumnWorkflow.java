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
package org.eclipse.jnosql.mapping.column;

import jakarta.nosql.column.ColumnEntity;
import jakarta.nosql.mapping.column.ColumnEntityConverter;
import jakarta.nosql.mapping.column.ColumnEventPersistManager;
import jakarta.nosql.mapping.column.ColumnWorkflow;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * The {@link ColumnWorkflow} template method
 */
public abstract class AbstractColumnWorkflow implements ColumnWorkflow {

    protected abstract ColumnEventPersistManager getEventManager();


    protected abstract ColumnEntityConverter getConverter();

    public <T> T flow(T entity, UnaryOperator<ColumnEntity> action) {

        Function<T, T> flow = getFlow(entity, action);

        return flow.apply(entity);

    }

    private <T> Function<T, T> getFlow(T entity, UnaryOperator<ColumnEntity> action) {
        UnaryOperator<T> validation = t -> Objects.requireNonNull(t, "entity is required");

        UnaryOperator<T> firePreEntity = t -> {
            getEventManager().firePreEntity(t);
            return t;
        };

        UnaryOperator<T> firePreColumnEntity = t -> {
            getEventManager().firePreColumnEntity(t);
            return t;
        };

        Function<T, ColumnEntity> converterColumn = t -> getConverter().toColumn(t);

        UnaryOperator<ColumnEntity> firePreColumn = t -> {
            getEventManager().firePreColumn(t);
            return t;
        };

        UnaryOperator<ColumnEntity> firePostColumn = t -> {
            getEventManager().firePostColumn(t);
            return t;
        };

        Function<ColumnEntity, T> converterEntity = t -> getConverter().toEntity(entity, t);

        UnaryOperator<T> firePostEntity = t -> {
            getEventManager().firePostEntity(t);
            return t;
        };

        UnaryOperator<T> firePostColumnEntity = t -> {
            getEventManager().firePostColumnEntity(t);
            return t;
        };

        return validation
                .andThen(firePreEntity)
                .andThen(firePreColumnEntity)
                .andThen(converterColumn)
                .andThen(firePreColumn)
                .andThen(action)
                .andThen(firePostColumn)
                .andThen(converterEntity)
                .andThen(firePostEntity)
                .andThen(firePostColumnEntity);
    }
}
