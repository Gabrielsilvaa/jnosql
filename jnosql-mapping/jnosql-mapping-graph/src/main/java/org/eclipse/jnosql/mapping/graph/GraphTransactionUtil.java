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

import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.eclipse.jnosql.mapping.config.MicroProfileSettings;

import java.util.Objects;

import static org.eclipse.jnosql.mapping.config.MappingConfigurations.GRAPH_TRANSACTION_AUTOMATIC;

/**
 * Utilitarian to {@link org.apache.tinkerpop.gremlin.structure.Transaction}
 */
final class GraphTransactionUtil {


    private static final ThreadLocal<Transaction> THREAD_LOCAL = new ThreadLocal<>();

    private GraphTransactionUtil() {
    }

    /**
     * Holds the current transaction to don't allow a {@link Transaction#commit()}
     *
     * @param transaction the {@link Transaction}
     */
    static void lock(Transaction transaction) {
        THREAD_LOCAL.set(transaction);
    }

    /**
     * Unlocks the {@link Transaction} of the current thread
     */
    static void unlock() {
        THREAD_LOCAL.remove();
    }

    /**
     * Checks if possible to {@link Transaction#commit()},
     * if checks it the {@link Transaction} holds and if it is defined as an automatic transaction.
     *
     * @param transaction the transaction
     */
    static void transaction(Transaction transaction) {
        if (isAutomatic() && isNotLock() && Objects.nonNull(transaction)) {
            transaction.commit();
        }
    }

    /**
     * Check if the transaction is enabled
     *
     * @return Check if the transaction is enabled
     */
    static boolean isAutomatic() {
        return MicroProfileSettings.INSTANCE.get(GRAPH_TRANSACTION_AUTOMATIC, String.class)
                .map(Boolean::valueOf)
                .orElse(true);
    }

    private static boolean isNotLock() {
        return THREAD_LOCAL.get() == null;
    }
}
