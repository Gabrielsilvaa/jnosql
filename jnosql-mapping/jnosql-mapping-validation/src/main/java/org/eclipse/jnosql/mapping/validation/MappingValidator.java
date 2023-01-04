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
package org.eclipse.jnosql.mapping.validation;


import org.eclipse.jnosql.mapping.reflection.ConstructorEvent;

/**
 * Validates bean instances. Implementations of this interface must be thread-safe.
 */
public interface MappingValidator {


    /**
     * Validate an entity using entity validation
     *
     * @param entity the entity to be validated
     * @param <T>    the type
     * @throws NullPointerException                          when entity is null
     * @throws jakarta.validation.ConstraintViolationException when {@link jakarta.validation.Validator#validate(Object, Class[])}
     *                                                       returns a non-empty collection
     */
    <T> void validate(T entity);


    /**
     * Validate an entity using entity validation
     *
     * @param event the event
     * @throws NullPointerException                          when entity is null
     * @throws jakarta.validation.ConstraintViolationException when {@link jakarta.validation.Validator#validate(Object, Class[])}
     *                                                       returns a non-empty collection
     */
    void validate(ConstructorEvent event);
}
