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
package org.eclipse.jnosql.artemis.document;

import jakarta.nosql.document.Document;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import org.eclipse.jnosql.artemis.reflection.FieldValue;

import java.util.List;

/**
 * The specialist {@link FieldValue} to document
 */
public interface DocumentFieldValue extends FieldValue {

    /**
     * Converts an entity to a {@link List} of documents
     * @param converter the converter
     * @param converters the converters
     * @param <X> the type of the entity attribute
     * @param <Y> the type of the database column
     * @return a {@link List} of documents from the field
     */
    <X, Y> List<Document> toDocument(DocumentEntityConverter converter, Converters converters);
}
