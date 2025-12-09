# Entwicklung der Java-Textblöcke (Java 11 – 21)

Java-Textblöcke haben sich im Zeitraum von **Java 13 bis Java 15** entwickelt und sind seitdem stabil. Zwischen **Java 16 und Java 21** gab es **keine weiteren Änderungen** an der Textblock-Syntax oder -Semantik.

## Vor Java 13 (einschließlich Java 11)
- Keine Textblöcke vorhanden.
- Mehrzeilige Strings mussten über `\n`, String-Konkatenation oder `StringBuilder` erzeugt werden.
- Ein früherer Entwurf für *Raw String Literals* (JEP 326, Java 12) wurde verworfen.

## Java 13 — Textblöcke (Preview, JEP 355)
Erste Einführung von mehrzeiligen String-Literalen über `"""`.

```java
String html = """
    <html>
        <body>Hello World</body>
    </html>
    """;
```

Eigenschaften:
- Klarere Darstellung von mehrzeiligen Texten (HTML, SQL, JSON …)
- Automatische Normalisierung von Zeilenumbrüchen auf `\n`
- Automatisches Entfernen gemeinsamer Einrückungen („incidental whitespace“)

## Java 14 — Zweite Preview (JEP 368)
Die grundlegende Syntax bleibt, aber es kommen zwei wichtige Ergänzungen dazu:

### 1. Escape-Sequenz `\s`
- Repräsentiert ein einzelnes Leerzeichen (`U+0020`)
- Hilfreich für sichtbare oder konsistente Leerzeichen am Zeilenende

### 2. `\<line-terminator>` — Unterdrückter Zeilenumbruch
```java
String text = """
    Hello \
    World
    """;
```

## Java 15 — Finalisierung (JEP 378)
Textblöcke werden ein **vollwertiges Sprachfeature**.
- Keine `--enable-preview`-Flags mehr
- Semantik entspricht weitgehend der Java-14-Preview
- Seit Java 15 unverändert standardisiert

## Java 16–21 — Stabilisierung, keine Syntaxänderungen
- Keine Änderungen an Textblöcken selbst
- Neue Features wie String Templates (Java 21, Preview) ergänzen Textblöcke, ersetzen sie aber nicht

```java
String name = "Lisa";
String message = STR."""
    Hello \{name},
    Welcome to Java 21!
    """;
```

## Kurz-Zusammenfassung
| Version | Status der Textblöcke | Bemerkungen |
|--------|------------------------|-------------|
| **Java 11** | – | keine Textblöcke |
| **Java 12** | – | gescheiterter Entwurf für Raw String Literals |
| **Java 13** | Preview | erste Version der Textblöcke |
| **Java 14** | Second Preview | neue Escapes `\s` und `\` |
| **Java 15** | Final | Textblöcke werden Standard |
| **Java 16–21** | stabil | keine Änderungen |

