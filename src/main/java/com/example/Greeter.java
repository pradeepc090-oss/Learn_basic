package com.example;

public final class Greeter {

    private Greeter() {
    }

    public static String greet(String name) {
        String target = (name == null || name.trim().isEmpty()) ? "World" : name.trim();
        return "{\"message\":\"Hello, " + target + "!\"}";
    }
}
