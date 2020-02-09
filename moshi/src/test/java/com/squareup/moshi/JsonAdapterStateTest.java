package com.squareup.moshi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class JsonAdapterStateTest {

    private Moshi moshi;

    @BeforeEach
    public void initializeBeforeEach() {
        this.moshi = new Moshi.Builder().build();
    }

    @Test
    public void nonNullJsonAdapterTest() throws IOException {
        String json = "null";

        JsonAdapter<Person> adapter = moshi.adapter(Person.class);
        boolean exceptionThrown = false;
        try {
            adapter.nonNull().fromJson(json);
        } catch (JsonDataException exception) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    public void nullSafeJsonAdapterTest() throws IOException {
        String json = "null";

        JsonAdapter<Person> adapter = moshi.adapter(Person.class);
        Person generatedPerson = adapter.nullSafe().fromJson(json);

        assertNull(generatedPerson);
    }

    @Test
    public void nullSafeToNullSafeJsonAdapterTest() throws IOException {
        String json = "null";

        JsonAdapter<Person> adapter = moshi.adapter(Person.class).nullSafe();

        assertNull(adapter.fromJson(json));
        assertNull(adapter.nullSafe().fromJson(json));
    }

    @Test
    public void nullSafeToNonNullJsonAdapterTest() throws IOException {
        String json = "null";

        JsonAdapter<Person> adapter = moshi.adapter(Person.class).nullSafe();

        assertNull(adapter.fromJson(json));
        boolean exceptionThrown = false;
        try {
            adapter.nonNull().fromJson(json);
        } catch (JsonDataException exception) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    public static class Person {
        String name;
        Integer age;

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
