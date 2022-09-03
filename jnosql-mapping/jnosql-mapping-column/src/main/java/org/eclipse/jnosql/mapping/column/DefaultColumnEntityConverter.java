/*
 *  Copyright (c) 2017 Otávio Santana and others
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


import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.column.ColumnEntityConverter;
import org.eclipse.jnosql.mapping.reflection.EntitiesMetadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * The default implementation to {@link ColumnEntityConverter}
 */
@ApplicationScoped
class DefaultColumnEntityConverter extends AbstractColumnEntityConverter implements ColumnEntityConverter {

    @Inject
    private EntitiesMetadata entities;

    @Inject
    private Converters converters;

    @Override
    protected EntitiesMetadata getEntities() {
        return entities;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }
}