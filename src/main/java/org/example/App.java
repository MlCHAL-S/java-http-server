package org.example;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class Person {
    public final String name;
    public Person (String name, int age) {
        this.name = name;
        _age = age;
    }
    public int getAge () {
        return _age;
    }
    protected int _age;
}

class IndexHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        // Ustawienie nagłówka Content-Type na text/html
        exchange.getResponseHeaders().set("Content-Type", "text/html");

        // Wczytanie zawartości pliku index.html
        File file = new File("src/main/resources/index.html");
        var tpl_src = Files.readString(Path.of("src/main/resources/index.html"));

        Template tmpl = Mustache.compiler().compile(tpl_src);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("request", exchange.getRequestMethod());

        var persons = List.of(
                new Person("aaa", 123),
                new Person("bbb", 456)
        );

        data.put("persons", persons);

        var response = tmpl.execute(data);

        var bytes = response.getBytes();

        // Ustawienie kodu odpowiedzi na 200 OK i zwrócenie zawartości pliku
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes );
        os.close();
    }
}


public class App
{
    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = "";
            String requestMethod = t.getRequestMethod();
            response = switch (requestMethod) {
                // The GET method requests a representation of the specified resource. Requests using GET should only retrieve data.
                case "GET" -> "Handling GET request";
                // The POST method is used to submit an entity to the specified resource, often causing a change in state or side effects on the server.
                case "POST" -> "Handling POST request";
                // The PUT method replaces all current representations of the target resource with the request payload.
                case "PUT" -> "Handling PUT request";
                // The DELETE method deletes the specified resource.
                case "DELETE" -> "Handling DELETE request";
                // The PATCH method is used to apply partial modifications to a resource.
                case "PATCH" -> "Handling PATCH request";
                // The HEAD method asks for a response identical to that of a GET request, but without the response body.
                case "HEAD" -> "Handling HEAD request";
                // The OPTIONS method describes the communication options for the target resource.
                case "OPTIONS" -> "Handling OPTIONS request";
                // This block can catch any method that is not explicitly handled above.
                default -> "Unhandled HTTP method: " + requestMethod;
            };

            t.sendResponseHeaders(200, response.getBytes().length); // Send HTTP status 200 (OK) and response length
            OutputStream os = t.getResponseBody(); // Get the response body
            os.write(response.getBytes()); // Write the response string to the response body
            os.close(); // Close the response body stream
        }
    }

    public static void main(String[] args) throws Exception {

        // Tworzenie instancji serwera HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Tworzenie kontekstu do obsługi zapytań
        server.createContext("/", new IndexHandler());

        // Startowanie serwera
        server.start();
    }
}

