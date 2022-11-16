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
package org.eclipse.jnosql.mapping.keyvalue;


import jakarta.nosql.keyvalue.BucketManager;
import jakarta.nosql.mapping.keyvalue.KeyValueEntityConverter;
import jakarta.nosql.mapping.keyvalue.KeyValueEventPersistManager;
import jakarta.nosql.mapping.keyvalue.KeyValueTemplate;
import jakarta.nosql.mapping.keyvalue.KeyValueTemplateProducer;
import jakarta.nosql.mapping.keyvalue.KeyValueWorkflow;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import java.util.Objects;

@ApplicationScoped
class DefaultKeyValueTemplateProducer implements KeyValueTemplateProducer {

    @Inject
    private KeyValueEntityConverter converter;
    @Inject
    private KeyValueWorkflow flow;

    @Inject
    private KeyValueEventPersistManager eventManager;

    @Override
    public KeyValueTemplate get(BucketManager manager) {
        Objects.requireNonNull(manager, "manager is required");
        return new ProducerKeyValueTemplate(converter, flow, manager, eventManager);
    }

    @Vetoed
    static class ProducerKeyValueTemplate extends AbstractKeyValueTemplate {

        private KeyValueEntityConverter converter;

        private KeyValueWorkflow flow;

        private BucketManager manager;

        private KeyValueEventPersistManager eventManager;

        ProducerKeyValueTemplate(KeyValueEntityConverter converter, KeyValueWorkflow flow,
                                 BucketManager manager, KeyValueEventPersistManager eventManager) {
            this.converter = converter;
            this.flow = flow;
            this.manager = manager;
            this.eventManager = eventManager;
        }

        ProducerKeyValueTemplate() {
        }

        @Override
        protected KeyValueEntityConverter getConverter() {
            return converter;
        }

        @Override
        protected BucketManager getManager() {
            return manager;
        }

        @Override
        protected KeyValueWorkflow getFlow() {
            return flow;
        }

        @Override
        protected KeyValueEventPersistManager getEventManager() {
            return eventManager;
        }
    }
}
