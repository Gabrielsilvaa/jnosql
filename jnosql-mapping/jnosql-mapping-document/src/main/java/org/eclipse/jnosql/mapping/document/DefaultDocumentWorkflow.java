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
package org.eclipse.jnosql.mapping.document;


import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The default implementation of {@link jakarta.nosql.mapping.document.DocumentWorkflow}
 */
@ApplicationScoped
class DefaultDocumentWorkflow extends AbstractDocumentWorkflow {

    private DocumentEventPersistManager documentEventPersistManager;


    private DocumentEntityConverter converter;

    DefaultDocumentWorkflow() {
    }

    @Inject
    DefaultDocumentWorkflow(DocumentEventPersistManager documentEventPersistManager, DocumentEntityConverter converter) {
        this.documentEventPersistManager = documentEventPersistManager;
        this.converter = converter;
    }


    @Override
    protected DocumentEventPersistManager getEventManager() {
        return documentEventPersistManager;
    }

    @Override
    protected DocumentEntityConverter getConverter() {
        return converter;
    }
}
