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
package org.eclipse.jnosql.mapping.reflection;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.DiscriminatorColumn;
import jakarta.nosql.mapping.DiscriminatorValue;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;
import jakarta.nosql.mapping.Inheritance;
import jakarta.nosql.mapping.MappedSuperclass;
import org.eclipse.jnosql.mapping.util.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class DefaultReflections implements Reflections {

    private static final Logger LOGGER = Logger.getLogger(Reflections.class.getName());

    @Override
    public Object getValue(Object object, Field field) {

        try {
            return field.get(object);
        } catch (Exception exception) {
            LOGGER.log(Level.FINEST, "There is an issue with returning value from this field.", exception);
        }
        return null;
    }

    @Override
    public boolean setValue(Object object, Field field, Object value) {
        try {

            field.set(object, value);

        } catch (Exception exception) {
            LOGGER.log(Level.FINEST, "There is an issue with setting value from this field.", exception);
            return false;
        }
        return true;
    }

    @Override
    public <T> T newInstance(Constructor<T> constructor) {
        try {
            return constructor.newInstance();
        } catch (Exception exception) {
            LOGGER.log(Level.FINEST, "There is an issue to creating an entity from this constructor", exception);
            return null;
        }
    }

    @Override
    public <T> T newInstance(Class<T> type) {
        try {
            Constructor<T> constructor = getConstructor(type);
            return newInstance(constructor);
        } catch (Exception exception) {
            LOGGER.log(Level.FINEST, "There is an issue to creating an entity from this constructor", exception);
            return null;
        }
    }

    @Override
    public Field getField(String string, Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            if (field.getName().equals(string)) {
                return field;
            }
        }
        return null;
    }

    @Override
    public Class<?> getGenericType(Field field) {
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        return (Class<?>) genericType.getActualTypeArguments()[0];

    }

    @Override
    public void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier
                .isPublic(field.getDeclaringClass().getModifiers()))
                && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    @Override
    public <T> Constructor<T> getConstructor(Class<T> type) {
        final Predicate<Constructor<?>> defaultConstructorPredicate = c -> c.getParameterCount() == 0;
        final Predicate<Constructor<?>> customConstructorPredicate = c -> {
            for (Parameter parameter : c.getParameters()) {
                if (parameter.getAnnotation(Id.class) != null || parameter.getAnnotation(Column.class) != null) {
                    return true;
                }
            }
            return false;
        };

        List<Constructor<?>> constructors = Stream.
                of(type.getDeclaredConstructors())
                .filter(defaultConstructorPredicate.or(customConstructorPredicate))
                .collect(toList());


        if (constructors.isEmpty()) {
            throw new ConstructorException(type);
        }

        Optional<Constructor<?>> publicConstructor = constructors
                .stream()
                .sorted(ConstructorComparable.INSTANCE)
                .filter(c -> Modifier.isPublic(c.getModifiers()))
                .findFirst();
        if (publicConstructor.isPresent()) {
            return (Constructor<T>) publicConstructor.get();
        }

        Constructor<?> constructor = constructors.get(0);
        constructor.setAccessible(true);
        return (Constructor<T>) constructor;
    }

    @Override
    public String getEntityName(Class<?> entity) {
        requireNonNull(entity, "class entity is required");

        if (isInheritance(entity)) {
            return readEntity(entity.getSuperclass());
        }
        return readEntity(entity);
    }

    @Override
    public List<Field> getFields(Class<?> type) {
        requireNonNull(type, "class entity is required");

        List<Field> fields = new ArrayList<>();

        if (isMappedSuperclass(type)) {
            fields.addAll(getFields(type.getSuperclass()));
        }
        Predicate<Field> hasColumnAnnotation = f -> f.getAnnotation(Column.class) != null;
        Predicate<Field> hasIdAnnotation = f -> f.getAnnotation(Id.class) != null;

        Stream.of(type.getDeclaredFields())
                .filter(hasColumnAnnotation.or(hasIdAnnotation))
                .forEach(fields::add);
        return fields;
    }

    @Override
    public boolean isMappedSuperclass(Class<?> type) {
        requireNonNull(type, "class entity is required");
        Class<?> superclass = type.getSuperclass();
        return superclass.getAnnotation(MappedSuperclass.class) != null
                || superclass.getAnnotation(Inheritance.class) != null;
    }

    @Override
    public boolean isIdField(Field field) {
        requireNonNull(field, "field is required");
        return field.getAnnotation(Id.class) != null;
    }

    @Override
    public String getColumnName(Field field) {
        requireNonNull(field, "field is required");
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::value)
                .filter(StringUtils::isNotBlank)
                .orElse(field.getName());
    }

    @Override
    public String getIdName(Field field) {
        requireNonNull(field, "field is required");
        return Optional.ofNullable(field.getAnnotation(Id.class))
                .map(Id::value)
                .filter(StringUtils::isNotBlank)
                .orElse(field.getName());
    }

    @Override
    public Optional<InheritanceMetadata> getInheritance(Class<?> type) {
        Objects.requireNonNull(type, "entity is required");
        if (isInheritance(type)) {
            Class<?> parent = type.getSuperclass();
            String discriminatorColumn = getDiscriminatorColumn(parent);
            String discriminatorValue = getDiscriminatorValue(type);
            return Optional.of(new InheritanceMetadata(discriminatorValue, discriminatorColumn,
                    parent, type));
        } else if (type.getAnnotation(Inheritance.class) != null) {
            String discriminatorColumn = getDiscriminatorColumn(type);
            String discriminatorValue = getDiscriminatorValue(type);
            return Optional.of(new InheritanceMetadata(discriminatorValue, discriminatorColumn,
                    type, type));
        }
        return Optional.empty();
    }

    @Override
    public boolean hasInheritanceAnnotation(Class<?> entity) {
        Objects.requireNonNull(entity, "entity is required");
        return entity.getAnnotation(Inheritance.class) != null;
    }


    private String getDiscriminatorColumn(Class<?> parent) {
        return Optional
                .ofNullable(parent.getAnnotation(DiscriminatorColumn.class))
                .map(DiscriminatorColumn::value)
                .orElse(DiscriminatorColumn.DEFAULT_DISCRIMINATOR_COLUMN);
    }

    private String getDiscriminatorValue(Class<?> entity) {
        return Optional
                .ofNullable(entity.getAnnotation(DiscriminatorValue.class))
                .map(DiscriminatorValue::value)
                .orElse(entity.getSimpleName());
    }

    private String readEntity(Class<?> entity) {
        return Optional.ofNullable((Entity) entity.getAnnotation(Entity.class))
                .map(Entity::value)
                .filter(StringUtils::isNotBlank)
                .orElse(entity.getSimpleName());
    }

    private boolean isInheritance(Class<?> entity) {
        Class<?> superclass = entity.getSuperclass();
        return superclass.getAnnotation(Inheritance.class) != null;
    }
}
