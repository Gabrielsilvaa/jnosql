/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *  You may elect to redistribute this code under either of these licenses.
 *  Contributors:
 *  Otavio Santana
 */

package org.eclipse.jnosql.communication.query;


import org.eclipse.jnosql.communication.QueryException;

import java.util.Objects;

final class Elements {

    private static final String MESSAGE = "There is an error when trying to convert the value";

    private Elements() {
    }

    static QueryValue<?> getElement(QueryParser.ElementContext elementContext) {
        if (Objects.nonNull(elementContext.string())) {
            return StringQueryValue.of(elementContext.string());
        }
        if (Objects.nonNull(elementContext.number())) {
            return NumberQueryValue.of(elementContext.number());
        }
        throw new QueryException(MESSAGE);
    }

}
