package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class App {

    private static final int MAX_NUMBER = 100;
    private static final int MAX_GAMES = 10_000;
    private static final Random RANDOM = new SecureRandom();
    private static final Map<String, Game> GAMES = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", App::serveIndex);
        server.createContext("/api/new", App::newGame);
        server.createContext("/api/guess", App::guess);
        server.createContext("/health", exchange -> send(exchange, 200, "application/json", "{\"status\":\"UP\"}"));
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("Guess the Number running on http://localhost:" + port);
    }

    private static void serveIndex(HttpExchange exchange) throws IOException {
        if (!"/".equals(exchange.getRequestURI().getPath())) {
            send(exchange, 404, "application/json", "{\"error\":\"not found\"}");
            return;
        }
        try (InputStream in = App.class.getResourceAsStream("/index.html")) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int read;
            while ((read = in.read(chunk)) != -1) {
                buffer.write(chunk, 0, read);
            }
            send(exchange, 200, "text/html; charset=utf-8", buffer.toString("UTF-8"));
        }
    }

    private static void newGame(HttpExchange exchange) throws IOException {
        if (GAMES.size() >= MAX_GAMES) {
            GAMES.clear();
        }
        String id = UUID.randomUUID().toString();
        GAMES.put(id, Game.create(MAX_NUMBER, RANDOM));
        send(exchange, 200, "application/json", "{\"id\":\"" + id + "\",\"max\":" + MAX_NUMBER + "}");
    }

    private static void guess(HttpExchange exchange) throws IOException {
        Map<String, String> params = queryParams(exchange.getRequestURI().getRawQuery());
        String id = params.getOrDefault("id", "");
        Game game = GAMES.get(id);
        if (game == null) {
            send(exchange, 404, "application/json", "{\"error\":\"Unknown game - start a new one.\"}");
            return;
        }
        int value;
        try {
            value = Integer.parseInt(params.getOrDefault("value", "").trim());
        } catch (NumberFormatException e) {
            send(exchange, 400, "application/json", "{\"error\":\"Enter a number.\"}");
            return;
        }
        try {
            Game.Result result = game.guess(value);
            if (game.isSolved()) {
                GAMES.remove(id);
            }
            send(exchange, 200, "application/json",
                    "{\"result\":\"" + result + "\",\"attempts\":" + game.getAttempts()
                            + ",\"solved\":" + game.isSolved() + "}");
        } catch (IllegalArgumentException e) {
            send(exchange, 400, "application/json",
                    "{\"error\":\"Pick a number between 1 and " + MAX_NUMBER + ".\"}");
        }
    }

    private static Map<String, String> queryParams(String rawQuery) {
        Map<String, String> params = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return params;
        }
        for (String pair : rawQuery.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0) {
                params.put(decode(pair.substring(0, eq)), decode(pair.substring(eq + 1)));
            }
        }
        return params;
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private static void send(HttpExchange exchange, int status, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
