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

import jakarta.nosql.NonUniqueResultException;
import org.eclipse.jnosql.mapping.test.jupiter.CDIExtension;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.structure.T;
import org.eclipse.jnosql.mapping.graph.entities.Animal;
import org.eclipse.jnosql.mapping.graph.entities.Book;
import org.eclipse.jnosql.mapping.graph.entities.Person;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@CDIExtension
public class DefaultVertexTraversalTest extends AbstractTraversalTest {


    @Test
    public void shouldReturnErrorWhenVertexIdIsNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex(null));
    }

    @Test
    public void shouldGetVertexFromId() {
        List<Person> people = graphTemplate.getTraversalVertex(otavio.getId(), poliana.getId()).<Person>getResult()
                .collect(toList());

        assertThat(people).contains(otavio, poliana);
    }

    @Test
    public void shouldDefineLimit() {
        List<Person> people = graphTemplate.getTraversalVertex(otavio.getId(), poliana.getId(),
                        paulo.getId()).limit(1)
                .<Person>getResult()
                .collect(toList());

        assertEquals(1, people.size());
        assertThat(people).contains(otavio);
    }

    @Test
    public void shouldDefineLimit2() {
        List<Person> people = graphTemplate.getTraversalVertex(otavio.getId(), poliana.getId(), paulo.getId()).
                <Person>next(2)
                .collect(toList());

        assertEquals(2, people.size());
        assertThat(people).contains(otavio, poliana);
    }

    @Test
    public void shouldNext() {
        Optional<?> next = graphTemplate.getTraversalVertex().next();
        assertTrue(next.isPresent());
    }

    @Test
    public void shouldEmptyNext() {
        Optional<?> next = graphTemplate.getTraversalVertex(-12).next();
        assertFalse(next.isPresent());
    }


    @Test
    public void shouldHave() {
        Optional<Person> person = graphTemplate.getTraversalVertex().has("name", "Poliana").next();
        assertTrue(person.isPresent());
        assertEquals(person.get(), poliana);
    }

    @Test
    public void shouldReturnErrorWhenHasNullKey() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex()
                .has((String) null, "Poliana")
                .next());
    }


    @Test
    public void shouldReturnErrorWhenHasNullValue() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().has("name", null)
                .next());
    }

    @Test
    public void shouldHaveId() {
        Optional<Person> person = graphTemplate.getTraversalVertex().has(T.id, poliana.getId()).next();
        assertTrue(person.isPresent());
        assertEquals(person.get(), poliana);
    }

    @Test
    public void shouldReturnErrorWhenHasIdHasNullValue() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().has(T.id, null).next());
    }

    @Test
    public void shouldReturnErrorWhenHasIdHasNullAccessor() {
        assertThrows(NullPointerException.class, () -> {
            T id = null;
            graphTemplate.getTraversalVertex().has(id, poliana.getId()).next();
        });
    }


    @Test
    public void shouldHavePredicate() {
        List<?> result = graphTemplate.getTraversalVertex().has("age", P.gt(26))
                .getResult()
                .collect(toList());
        assertEquals(5, result.size());
    }

    @Test
    public void shouldReturnErrorWhenHasPredicateIsNull() {
        assertThrows(NullPointerException.class, () -> {
            P<Integer> gt = null;
            graphTemplate.getTraversalVertex().has("age", gt)
                    .getResult()
                    .collect(toList());
        });
    }

    @Test
    public void shouldReturnErrorWhenHasKeyIsNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().has((String) null,
                        P.gt(26))
                .getResult()
                .collect(toList()));
    }

    @Test
    public void shouldHaveLabel() {
        List<Book> books = graphTemplate.getTraversalVertex().hasLabel("Book").<Book>getResult().collect(toList());
        assertEquals(3, books.size());
        assertThat(books).contains(shack, license, effectiveJava);
    }

    @Test
    public void shouldHaveLabel2() {

        List<Object> entities = graphTemplate.getTraversalVertex()
                .hasLabel(P.eq("Book").or(P.eq("Person")))
                .getResult().collect(toList());
        assertThat(entities).hasSize(6).contains(shack, license, effectiveJava, otavio, poliana, paulo);
    }

    @Test
    public void shouldReturnErrorWhenHasLabelHasNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().hasLabel((String) null)
                .<Book>getResult().collect(toList()));
    }

    @Test
    public void shouldIn() {
        List<Book> books = graphTemplate.getTraversalVertex().out(READS).<Book>getResult().collect(toList());
        assertEquals(3, books.size());
        assertThat(books).contains(shack, license, effectiveJava);
    }

    @Test
    public void shouldReturnErrorWhenInIsNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().out((String) null).<Book>getResult().collect(toList()));
    }

    @Test
    public void shouldOut() {
        List<Person> people = graphTemplate.getTraversalVertex().in(READS).<Person>getResult().collect(toList());
        assertEquals(3, people.size());
        assertThat(people).contains(otavio, poliana, paulo);
    }

    @Test
    public void shouldReturnErrorWhenOutIsNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().in((String) null).<Person>getResult().collect(toList()));
    }

    @Test
    public void shouldBoth() {
        List<?> entities = graphTemplate.getTraversalVertex().both(READS)
                .<Person>getResult().collect(toList());
        assertEquals(6, entities.size());
    }

    @Test
    public void shouldReturnErrorWhenBothIsNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().both((String) null)
                .<Person>getResult().collect(toList()));
    }

    @Test
    public void shouldNot() {
        List<?> result = graphTemplate.getTraversalVertex().hasNot("year").getResult().collect(toList());
        assertEquals(6, result.size());
    }

    @Test
    public void shouldReturnErrorWhenHasNotIsNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().hasNot((String) null)
                .getResult().collect(toList()));
    }

    @Test
    public void shouldCount() {
        long count = graphTemplate.getTraversalVertex().both(READS).count();
        assertEquals(6L, count);
    }

    @Test
    public void shouldReturnZeroWhenCountIsEmpty() {
        long count = graphTemplate.getTraversalVertex().both("WRITES").count();
        assertEquals(0L, count);
    }

    @Test
    public void shouldDefinesLimit() {
        long count = graphTemplate.getTraversalVertex().limit(1L).count();
        assertEquals(1L, count);
        assertNotEquals(graphTemplate.getTraversalVertex().count(), count);
    }

    @Test
    public void shouldDefinesRange() {
        long count = graphTemplate.getTraversalVertex().range(1, 3).count();
        assertEquals(2L, count);
        assertNotEquals(graphTemplate.getTraversalVertex().count(), count);
    }

    @Test
    public void shouldMapValuesAsStream() {
        List<Map<String, Object>> maps = graphTemplate.getTraversalVertex().hasLabel("Person")
                .valueMap("name").stream().collect(toList());

        assertFalse(maps.isEmpty());
        assertEquals(3, maps.size());

        List<String> names = new ArrayList<>();

        maps.forEach(m -> names.add(((List) m.get("name")).get(0).toString()));

        assertThat(names).contains("Otavio", "Poliana", "Paulo");
    }

    @Test
    public void shouldMapValuesAsStreamLimit() {
        List<Map<String, Object>> maps = graphTemplate.getTraversalVertex().hasLabel("Person")
                .valueMap("name").next(2).collect(toList());

        assertFalse(maps.isEmpty());
        assertEquals(2, maps.size());
    }


    @Test
    public void shouldReturnMapValueAsEmptyStream() {
        Stream<Map<String, Object>> stream = graphTemplate.getTraversalVertex().hasLabel("Person")
                .valueMap("noField").stream();
        assertTrue(stream.allMatch(m -> Objects.isNull(m.get("noFoundProperty"))));
    }

    @Test
    public void shouldReturnNext() {
        Map<String, Object> map = graphTemplate.getTraversalVertex().hasLabel("Person")
                .valueMap("name").next();

        assertNotNull(map);
        assertFalse(map.isEmpty());
    }


    @Test
    public void shouldRepeatTimesTraversal() {
        Animal lion = graphTemplate.insert(new Animal("lion"));
        Animal snake = graphTemplate.insert(new Animal("snake"));
        Animal mouse = graphTemplate.insert(new Animal("mouse"));
        Animal plant = graphTemplate.insert(new Animal("plant"));

        graphTemplate.edge(lion, "eats", snake).add("when", "night");
        graphTemplate.edge(snake, "eats", mouse);
        graphTemplate.edge(mouse, "eats", plant);
        Optional<Animal> animal = graphTemplate.getTraversalVertex().repeat().out("eats").times(3).next();
        assertTrue(animal.isPresent());
        assertEquals(plant, animal.get());

    }

    @Test
    public void shouldRepeatTimesTraversal2() {
        Animal lion = graphTemplate.insert(new Animal("lion"));
        Animal snake = graphTemplate.insert(new Animal("snake"));
        Animal mouse = graphTemplate.insert(new Animal("mouse"));
        Animal plant = graphTemplate.insert(new Animal("plant"));

        graphTemplate.edge(lion, "eats", snake).add("when", "night");
        graphTemplate.edge(snake, "eats", mouse);
        graphTemplate.edge(mouse, "eats", plant);
        Optional<Animal> animal = graphTemplate.getTraversalVertex().repeat().in("eats").times(3).next();
        assertTrue(animal.isPresent());
        assertEquals(lion, animal.get());

    }

    @Test
    public void shouldRepeatUntilTraversal() {
        Animal lion = graphTemplate.insert(new Animal("lion"));
        Animal snake = graphTemplate.insert(new Animal("snake"));
        Animal mouse = graphTemplate.insert(new Animal("mouse"));
        Animal plant = graphTemplate.insert(new Animal("plant"));

        graphTemplate.edge(lion, "eats", snake);
        graphTemplate.edge(snake, "eats", mouse);
        graphTemplate.edge(mouse, "eats", plant);

        Optional<Animal> animal = graphTemplate.getTraversalVertex()
                .repeat().out("eats")
                .until().has("name", "plant").next();

        assertTrue(animal.isPresent());


        assertEquals(plant, animal.get());
    }

    @Test
    public void shouldRepeatUntilTraversal2() {
        Animal lion = graphTemplate.insert(new Animal("lion"));
        Animal snake = graphTemplate.insert(new Animal("snake"));
        Animal mouse = graphTemplate.insert(new Animal("mouse"));
        Animal plant = graphTemplate.insert(new Animal("plant"));

        graphTemplate.edge(lion, "eats", snake);
        graphTemplate.edge(snake, "eats", mouse);
        graphTemplate.edge(mouse, "eats", plant);

        Optional<Animal> animal = graphTemplate.getTraversalVertex()
                .repeat().in("eats")
                .until().has("name", "lion").next();

        assertTrue(animal.isPresent());


        assertEquals(lion, animal.get());
    }


    @Test
    public void shouldReturnErrorWhenTheOrderIsNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().orderBy(null));
    }

    @Test
    public void shouldReturnErrorWhenThePropertyDoesNotExist() {
        assertThrows(NoSuchElementException.class, () ->
                graphTemplate.getTraversalVertex().orderBy("wrong property").asc().next().get());
    }

    @Test
    public void shouldOrderAsc() {
        String property = "name";

        List<String> properties = graphTemplate.getTraversalVertex()
                .hasLabel("Book")
                .has(property)
                .orderBy(property)
                .asc().<Book>getResult()
                .map(Book::getName)
                .collect(toList());

        assertThat(properties).contains("Effective Java", "Software License", "The Shack");
    }

    @Test
    public void shouldOrderDesc() {
        String property = "name";

        List<String> properties = graphTemplate.getTraversalVertex()
                .hasLabel("Book")
                .has(property)
                .orderBy(property)
                .desc().<Book>getResult()
                .map(Book::getName)
                .collect(toList());

        assertThat(properties).contains("The Shack", "Software License", "Effective Java");
    }

    @Test
    public void shouldReturnErrorWhenHasLabelStringNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().hasLabel((String) null));
    }

    @Test
    public void shouldReturnErrorWhenHasLabelSupplierNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().hasLabel((Supplier<String>) null));
    }

    @Test
    public void shouldReturnErrorWhenHasLabelEntityClassNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().hasLabel((Class<?>) null));
    }

    @Test
    public void shouldReturnHasLabel() {
        assertTrue(graphTemplate.getTraversalVertex().hasLabel("Person").getResult().allMatch(Person.class::isInstance));
        assertTrue(graphTemplate.getTraversalVertex().hasLabel(() -> "Book").getResult().allMatch(Book.class::isInstance));
        assertTrue(graphTemplate.getTraversalVertex().hasLabel(Animal.class).getResult().allMatch(Animal.class::isInstance));
    }

    @Test
    public void shouldReturnResultAsList() {
        List<Person> people = graphTemplate.getTraversalVertex().hasLabel("Person")
                .<Person>getResult()
                .collect(Collectors.toList());
        assertEquals(3, people.size());
    }

    @Test
    public void shouldReturnErrorWhenThereAreMoreThanOneInGetSingleResult() {
        assertThrows(NonUniqueResultException.class, () -> graphTemplate.getTraversalVertex().hasLabel("Person").getSingleResult());
    }

    @Test
    public void shouldReturnOptionalEmptyWhenThereIsNotResultInSingleResult() {
        Optional<Object> entity = graphTemplate.getTraversalVertex().hasLabel("NoEntity").getSingleResult();
        assertFalse(entity.isPresent());
    }

    @Test
    public void shouldReturnSingleResult() {
        String name = "Poliana";
        Optional<Person> poliana = graphTemplate.getTraversalVertex().hasLabel("Person").
                has("name", name).getSingleResult();
        assertEquals(name, poliana.map(Person::getName).orElse(""));
    }

    @Test
    public void shouldReturnErrorWhenPredicateIsNull() {
        assertThrows(NullPointerException.class, () -> graphTemplate.getTraversalVertex().filter(null));
    }

    @Test
    public void shouldPredicate() {
        long count = graphTemplate.getTraversalVertex()
                .hasLabel(Person.class)
                .filter(Person::isAdult).count();
        assertEquals(3L, count);
    }

    @Test
    public void shouldDedup() {

        graphTemplate.edge(otavio, "knows", paulo);
        graphTemplate.edge(paulo, "knows", otavio);
        graphTemplate.edge(otavio, "knows", poliana);
        graphTemplate.edge(poliana, "knows", otavio);
        graphTemplate.edge(poliana, "knows", paulo);
        graphTemplate.edge(paulo, "knows", poliana);

        List<Person> people = graphTemplate.getTraversalVertex()
                .hasLabel(Person.class)
                .in("knows").<Person>getResult()
                .collect(Collectors.toList());

        assertEquals(6, people.size());

        people = graphTemplate.getTraversalVertex()
                .hasLabel(Person.class)
                .in("knows").dedup().<Person>getResult()
                .collect(Collectors.toList());

        assertEquals(3, people.size());
    }

    @Test
    public void shouldCreateTree() {
        Animal lion = graphTemplate.insert(new Animal("lion"));
        Animal zebra = graphTemplate.insert(new Animal("zebra"));
        Animal giraffe = graphTemplate.insert(new Animal("giraffe"));
        Animal grass = graphTemplate.insert(new Animal("grass"));

        graphTemplate.edge(lion, "eats", giraffe);
        graphTemplate.edge(lion, "eats", zebra);
        graphTemplate.edge(zebra, "eats", grass);
        graphTemplate.edge(giraffe, "eats", grass);

        EntityTree tree = graphTemplate.getTraversalVertex()
                .hasLabel(Animal.class)
                .in("eats")
                .tree();

        assertNotNull(tree);
    }

}