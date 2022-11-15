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

import jakarta.nosql.ServiceLoaderProvider;
import jakarta.nosql.Settings;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * The Configuration that creates an instance of {@link Graph} that given a {@link Settings} make an  {@link Graph} instance.
 */
public interface GraphConfiguration extends Function<Settings, Graph> {


    /**
     * creates and returns a  {@link GraphConfiguration}  instance from {@link java.util.ServiceLoader}
     *
     * @param <T> the configuration type
     * @return {@link GraphConfiguration} instance
     * @throws jakarta.nosql.ProviderNotFoundException when the provider is not found
     * @throws jakarta.nosql.NonUniqueResultException  when there is more than one KeyValueConfiguration
     */
    static <T extends GraphConfiguration> T getConfiguration() {
        return (T) ServiceLoaderProvider.getUnique(GraphConfiguration.class,
                ()-> ServiceLoader.load(GraphConfiguration.class));
    }
}
