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

import jakarta.nosql.Value;
import jakarta.nosql.keyvalue.KeyValueEntity;
import jakarta.nosql.mapping.IdNotFoundException;
import jakarta.nosql.mapping.keyvalue.KeyValueEntityConverter;
import jakarta.nosql.tck.entities.Car;
import jakarta.nosql.tck.entities.Person;
import jakarta.nosql.tck.entities.Plate;
import jakarta.nosql.tck.entities.User;
import jakarta.nosql.tck.entities.Worker;
import jakarta.nosql.tck.test.CDIExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CDIExtension
public class DefaultKeyValueEntityConverterTest {

    @Inject
    private KeyValueEntityConverter converter;

    @Test
    public void shouldReturnNPEWhenEntityIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> converter.toKeyValue(null));
    }

    @Test
    public void shouldReturnErrorWhenThereIsNotKeyAnnotation() {
        Assertions.assertThrows(IdNotFoundException.class, () -> converter.toKeyValue(new Worker()));
    }

    @Test
    public void shouldReturnErrorWhenTheKeyIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            User user = new User(null, "name", 24);
            converter.toKeyValue(user);
        });
    }

    @Test
    public void shouldConvertToKeyValue() {
        User user = new User("nickname", "name", 24);
        KeyValueEntity keyValueEntity = converter.toKeyValue(user);
        assertEquals("nickname", keyValueEntity.getKey());
        assertEquals(user, keyValueEntity.getValue());
    }

    @Test
    public void shouldReturnNPEWhenKeyValueIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> converter.toEntity(User.class, (KeyValueEntity) null));
    }

    @Test
    public void shouldReturnNPEWhenClassIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> converter.toEntity(null,
                KeyValueEntity.of("user", new User("nickname", "name", 21))));
    }

    @Test
    public void shouldReturnErrorWhenTheKeyIsMissing() {
        Assertions.assertThrows(IdNotFoundException.class, () -> converter.toEntity(Worker.class,
                KeyValueEntity.of("worker", new Worker())));
    }

    @Test
    public void shouldConvertToEntity() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class,
                KeyValueEntity.of("user", expectedUser));
        assertEquals(expectedUser, user);
    }

    @Test
    public void shouldConvertAndFeedTheKeyValue() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class,
                KeyValueEntity.of("nickname", new User(null, "name", 21)));
        assertEquals(expectedUser, user);
    }

    @Test
    public void shouldConvertAndFeedTheKeyValueIfKeyAndFieldAreDifferent() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class,
                KeyValueEntity.of("nickname", new User("newName", "name", 21)));
        assertEquals(expectedUser, user);
    }

    @Test
    public void shouldConvertValueToEntity() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class, KeyValueEntity.of("nickname", Value.of(expectedUser)));
        assertEquals(expectedUser, user);
    }

    @Test
    public void shouldConvertToEntityKeyWhenThereIsConverterAnnotation() {
        Car car = new Car();
        car.setName("Ferrari");

        Car ferrari = converter.toEntity(Car.class, KeyValueEntity.of("123-BRL", car));
        assertEquals(Plate.of("123-BRL"), ferrari.getPlate());
        assertEquals(car.getName(), ferrari.getName());
    }

    @Test
    public void shouldConvertToKeyWhenThereIsConverterAnnotation() {
        Car car = new Car();
        car.setPlate(Plate.of("123-BRL"));
        car.setName("Ferrari");
        KeyValueEntity entity = converter.toKeyValue(car);

        Assertions.assertEquals("123-BRL", entity.getKey());
        Assertions.assertEquals(car, entity.getValue());
    }

    @Test
    public void shouldConvertToEntityKeyWhenKeyTypeIsDifferent() {

        Person person = Person.builder().withName("Ada").build();
        Person ada = converter.toEntity(Person.class, KeyValueEntity.of("123", person));

        Assertions.assertEquals(123L, ada.getId());
        Assertions.assertEquals(ada.getName(), person.getName());
    }

    @Test
    public void shouldConvertToKeyWhenKeyTypeIsDifferent() {
        Person person = Person.builder().withId(123L).withName("Ada").build();
        KeyValueEntity entity = converter.toKeyValue(person);
        Assertions.assertEquals(123L, entity.getKey());
    }

}