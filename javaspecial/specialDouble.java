import java.awt.Desktop;
import java.net.URI;

public class specialDouble {
    public static void main(String[] args) throws Exception {
        String page = """
                <!doctype html>
                <html>
                <head><meta charset=\"UTF-8\"><title>You Are Special</title>
                <style>
                    body { margin: 0; min-height: 100vh; display: grid; place-items: center;
                           background: #fff4c2; font-family: sans-serif; text-align: center; }
                    h1 { color: #159447; font-size: 3rem; }
                    .monkeys { font-size: 4rem; line-height: 1.5; max-width: 700px; }
                </style></head>
                <body><main><h1>You are very special!</h1>
                <div class=\"monkeys\">🐒 🐒 🐒 🐒 🐒<br>🐒 🐒 🐒 🐒 🐒</div></main></body>
                </html>
                """;

        java.nio.file.Path file = java.nio.file.Files.createTempFile("special-monkeys-", ".html");
        java.nio.file.Files.writeString(file, page);
        Desktop.getDesktop().browse(new URI(file.toUri().toString()));
    }
}