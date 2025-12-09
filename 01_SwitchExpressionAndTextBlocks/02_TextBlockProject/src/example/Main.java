package example;

import static java.lang.StringTemplate.STR;

public class Main {
    public static void main(String[] args) {
        String html = """
        <html>
            <body>Hello World</body>
        </html>
        """;

        System.out.println(html);

        String text = """
    Hello \
    World
    """;

        System.out.println(text);


        String name = "Lisa";
        String message = STR."""
                Hello \{name},
                Welcome to Java 21!
                """;

        System.out.println(message);
    }


}
