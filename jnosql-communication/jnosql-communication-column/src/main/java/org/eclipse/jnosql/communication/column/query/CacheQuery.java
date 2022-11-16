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

import jakarta.nosql.column.ColumnObserverParser;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.BiFunction;

import static java.util.Collections.synchronizedMap;

final class CacheQuery<V> {

    private final Map<String, V> store = synchronizedMap(new WeakHashMap<>());
    private final BiFunction<String, ColumnObserverParser, V> supplier;

    CacheQuery(BiFunction<String, ColumnObserverParser, V> supplier) {
        this.supplier = supplier;
    }


    public V get(String key, ColumnObserverParser observer) {
        V value = this.store.get(key);
        if (Objects.isNull(value)) {
            synchronized (this) {
                value = supplier.apply(key, observer);
                put(key, value);
            }
        }
        return value;
    }

    private V put(String key, V value) {
        return store.put(key, value);
    }
}
