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
package org.eclipse.jnosql.mapping.graph;


import java.util.Objects;
import java.util.function.Supplier;


/**
 * When an entity is either saved or updated it's the first event to fire
 */
public final class EntityGraphPostPersist implements Supplier<Object> {

    private final Object value;

    EntityGraphPostPersist(Object value) {
        this.value = value;
    }

    @Override
    public Object get() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityGraphPostPersist)) {
            return false;
        }
        EntityGraphPostPersist that = (EntityGraphPostPersist) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return  "EntityGraphPostPersist{" + "value=" + value +
                '}';
    }

    static EntityGraphPostPersist of(Object value) {
        Objects.requireNonNull(value, "value is required");
        return new EntityGraphPostPersist(value);
    }
}