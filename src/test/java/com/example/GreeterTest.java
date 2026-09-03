package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreeterTest {

    @Test
    void greetsDefaultWhenNameMissing() {
        assertEquals("{\"message\":\"Hello, World!\"}", Greeter.greet(null));
        assertEquals("{\"message\":\"Hello, World!\"}", Greeter.greet("   "));
    }

    @Test
    void greetsProvidedName() {
        assertEquals("{\"message\":\"Hello, Sandeep!\"}", Greeter.greet(" Sandeep "));
    }
}
