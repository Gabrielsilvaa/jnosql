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

import jakarta.nosql.column.ColumnManager;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.Repository;
import jakarta.nosql.mapping.column.ColumnRepositoryProducer;
import jakarta.nosql.mapping.column.ColumnTemplate;
import jakarta.nosql.mapping.column.ColumnTemplateProducer;
import org.eclipse.jnosql.mapping.reflection.EntitiesMetadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.util.Objects;

@ApplicationScoped
class DefaultColumnRepositoryProducer implements ColumnRepositoryProducer {

    @Inject
    private EntitiesMetadata entities;

    @Inject
    private Converters converters;

    @Inject
    private ColumnTemplateProducer producer;

    @Override
    public <T, K, R extends Repository<T, K>> R get(Class<R> repositoryClass, ColumnManager manager) {
        Objects.requireNonNull(repositoryClass, "repository class is required");
        Objects.requireNonNull(manager, "manager class is required");
        ColumnTemplate template = producer.get(manager);
        return get(repositoryClass, template);
    }

    @Override
    public <T, K, R extends Repository<T, K>> R get(Class<R> repositoryClass, ColumnTemplate template) {
        Objects.requireNonNull(repositoryClass, "repository class is required");
        Objects.requireNonNull(template, "template class is required");

        ColumnRepositoryProxy<T, K> handler = new ColumnRepositoryProxy<>(template,
                entities, repositoryClass, converters);
        return (R) Proxy.newProxyInstance(repositoryClass.getClassLoader(),
                new Class[]{repositoryClass},
                handler);
    }
}
