# Änderungen an `java.lang.String` zwischen Java 17 und Java 21

Die Klasse `java.lang.String` ist eine der stabilsten Klassen im gesamten JDK.
Zwischen Java 17 und Java 21 gab es keine neuen Methoden, aber einige wichtige interne
und sprachbezogene Neuerungen, die Strings betreffen.

Dieses Dokument fasst alle relevanten Änderungen zusammen.

---

## 1. Keine API-Änderungen an `String` selbst (Java 17-21)

Die öffentliche API von `String` ist seit vielen Jahren unverändert.

Keine der folgenden Arten von Änderungen ist zwischen Java 17 und 21 passiert:

- Keine neuen Methoden
- Keine entfernten Methoden
- Keine geänderten Signaturen
- Keine neuen Interfaces

Das bedeutet:
--> Ein Java-17-Entwickler und ein Java-21-Entwickler benutzen exakt dieselbe String-API.

---

## 2. Interne Verbesserung: Optimierte Compact-Strings (weitergeführt)

Compact Strings wurden in Java 9 eingeführt (Speicheroptimierung durch UTF-16 oder Latin-1).
Zwischen Java 17 und 21 wurde diese Optimierung weiter verbessert:

- effizientere Konvertierungen
- verbesserte interne HotSpot-Optimierungen
- bessere Escape-Analysen

Für dich als Entwickler unsichtbar, aber Strings sind seit Java 17-21 schneller und speichereffizienter.

---

## 3. String-bezogene Sprachfeatures (wichtige Neuerungen)

Obwohl sich `String` selbst nicht geändert hat, gab es mehrere große Java-Sprachfeatures,
die Strings betreffen.

### 3.1 Textblöcke (final seit Java 15, somit auch in 17-21)

```java
String json = """
{
    "name": "Alice",
    "age": 30
}
""";
```

Relevanz für Java 17-21:
- seit Java 15 final
- voll verfügbar und unverändert in 17-21

---

### 3.2 Pattern Matching und `String` (Java 17-21)

```java
if (obj instanceof String s) {
    System.out.println(s.toUpperCase());
}
```

Mit Java 21 wurde Pattern Matching for `switch` finalisiert:

```java
switch (obj) {
    case String s -> System.out.println("Ein String: " + s);
    case null     -> System.out.println("null!");
    default       -> System.out.println("Etwas anderes");
}
```

---

### 3.3 String Templates (Java 21 - Preview)

```java
String name = "Lisa";
String message = STR."Hello \{name}, today is \{LocalDate.now()}!";
```

String Templates sind syntaktischer Zucker für String-Interpolation.  
Java 21: Preview-Feature.

---

## 4. String + Collections + APIs

### 4.1 `String::lines` (bereits seit Java 11)

Unverändert zwischen Java 17 und 21:

```java
"Hallo
Welt".lines().forEach(System.out::println);
```

### 4.2 Default-Encoding UTF-8 (seit Java 18)

```java
byte[] utf8 = "Hallo".getBytes(); // garantiert UTF-8 ab Java 18
```

---

## 5. Zusammenfassung fuer Java 17-21

| Bereich | Änderung | Version |
|--------|----------|---------|
| Öffentliche `String`-API | Keine Änderungen | 17-21 |
| Compact-String-Optimierungen | interne Performance-Verbesserungen | 17-21 |
| Textblöcke | final seit Java 15 | verfügbar in 17-21 |
| Pattern Matching | Verbesserungen fuer switch | 17-21 |
| String Templates | Preview | Java 21 |
| Default Encoding = UTF-8 | betrifft `String.getBytes()` | seit Java 18 |

---

## Fazit

`String` selbst ist zwischen Java 17 und Java 21 unverändert.
Die relevanten Neuerungen stammen aus den Sprachfeatures und dem Encoding-Verhalten.
