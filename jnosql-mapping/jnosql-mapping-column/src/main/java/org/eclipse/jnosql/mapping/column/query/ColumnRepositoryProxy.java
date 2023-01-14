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


import org.eclipse.jnosql.mapping.Converters;
import jakarta.nosql.mapping.Repository;
import jakarta.nosql.mapping.column.ColumnTemplate;
import org.eclipse.jnosql.mapping.reflection.EntityMetadata;
import org.eclipse.jnosql.mapping.reflection.EntitiesMetadata;

import java.lang.reflect.ParameterizedType;


/**
 * Proxy handle to generate {@link Repository}
 *
 * @param <T>  the type
 * @param <K> the K type
 */
class ColumnRepositoryProxy<T, K> extends AbstractColumnRepositoryProxy {


    private final ColumnTemplate template;

    private final ColumnRepository repository;

    private final EntityMetadata entityMetadata;

    private final Converters converters;


    ColumnRepositoryProxy(ColumnTemplate template, EntitiesMetadata entities, Class<?> repositoryType,
                          Converters converters) {
        this.template = template;
        Class<T> typeClass = (Class) ((ParameterizedType) repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
        this.entityMetadata = entities.get(typeClass);
        this.repository = new ColumnRepository(template, entityMetadata);
        this.converters = converters;
    }

    @Override
    protected Repository getRepository() {
        return repository;
    }

    @Override
    protected EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    @Override
    protected ColumnTemplate getTemplate() {
        return template;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }


    static class ColumnRepository extends AbstractColumnRepository implements Repository {

        private final ColumnTemplate template;

        private final EntityMetadata entityMetadata;

        ColumnRepository(ColumnTemplate template, EntityMetadata entityMetadata) {
            this.template = template;
            this.entityMetadata = entityMetadata;
        }

        @Override
        protected ColumnTemplate getTemplate() {
            return template;
        }

        @Override
        protected EntityMetadata getEntityMetadata() {
            return entityMetadata;
        }

    }
}
