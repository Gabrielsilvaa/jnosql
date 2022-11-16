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
package org.eclipse.jnosql.communication.document.query;

import jakarta.nosql.Params;
import jakarta.nosql.QueryException;
import jakarta.nosql.document.DocumentManager;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.document.DocumentObserverParser;
import jakarta.nosql.document.DocumentPreparedStatement;
import jakarta.nosql.query.Condition;
import jakarta.nosql.query.JSONQueryValue;
import jakarta.nosql.query.UpdateQuery;
import jakarta.nosql.query.UpdateQuery.UpdateQueryProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

final class UpdateQueryParser extends ConditionQueryParser {

    private final UpdateQueryProvider supplier;

    UpdateQueryParser() {
        this.supplier = UpdateQuery.getProvider();
    }

    Stream<DocumentEntity> query(String query, DocumentManager collectionManager, DocumentObserverParser observer) {

        UpdateQuery updateQuery = supplier.apply(query);

        Params params = Params.newParams();

        DocumentEntity entity = getEntity(params, updateQuery, observer);

        if (params.isNotEmpty()) {
            throw new QueryException("To run a query with a parameter use a PrepareStatement instead.");
        }
        return Stream.of(collectionManager.update(entity));
    }


    DocumentPreparedStatement prepare(String query, DocumentManager collectionManager, DocumentObserverParser observer) {

        Params params = Params.newParams();

        UpdateQuery updateQuery = supplier.apply(query);

        DocumentEntity entity = getEntity(params, updateQuery, observer);
        return DefaultDocumentPreparedStatement.update(entity, params, query, collectionManager);
    }

    private DocumentEntity getEntity(Params params, UpdateQuery updateQuery, DocumentObserverParser observer) {
        String collection = observer.fireEntity(updateQuery.getEntity());
        return getEntity(new UpdateQueryConditioinSupplier(updateQuery), collection, params, observer);
    }

    private static final class UpdateQueryConditioinSupplier implements ConditionQuerySupplier {
        private final UpdateQuery query;

        private UpdateQueryConditioinSupplier(UpdateQuery query) {
            this.query = query;
        }

        @Override
        public List<Condition> getConditions() {
            return query.getConditions();
        }

        @Override
        public Optional<JSONQueryValue> getValue() {
            return query.getValue();
        }
    }

}
