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
package org.eclipse.jnosql.mapping.keyvalue.query;


import jakarta.nosql.mapping.Repository;
import jakarta.nosql.mapping.keyvalue.KeyValueTemplate;

import java.lang.reflect.ParameterizedType;

class KeyValueRepositoryProxy<T> extends AbstractKeyValueRepositoryProxy<T> {

    private final DefaultKeyValueRepository repository;
    private final KeyValueTemplate template;
    private final Class<T> type;

    KeyValueRepositoryProxy(Class<?> repositoryType, KeyValueTemplate template) {
        Class<T> typeClass = (Class) ((ParameterizedType) repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
        this.repository = new DefaultKeyValueRepository(typeClass, template);
        this.template = template;
        this.type = typeClass;
    }

    @Override
    protected Repository getRepository() {
        return repository;
    }

    @Override
    protected KeyValueTemplate getTemplate() {
        return template;
    }

    @Override
    protected Class getType() {
        return type;
    }


}
