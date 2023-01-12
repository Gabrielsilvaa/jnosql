/*
 *
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.jnosql.communication.document;

/**
 * An observer to a parser; this observer allows checking both the name of an entity and the fields.
 * This observer might be used to the mapper process.
 */
public interface DocumentObserverParser {

    DocumentObserverParser EMPTY = new DocumentObserverParser() {

    };

    /**
     * Fire an event to entity name
     *
     * @param entity the entity
     * @return the field result
     */
    default String fireEntity(String entity) {
        return entity;
    }

    /**
     * Fire an event to each field in case of mapper process
     *
     * @param document the document
     * @param entity   the entity
     * @return the field result
     */
    default String fireField(String entity, String document) {
        return document;
    }


}
