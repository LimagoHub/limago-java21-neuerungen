# Entwicklung der *switch expressions* von Java 11 bis Java 21

Die `switch`-Syntax hat sich zwischen Java 11 und Java 21 stark
weiterentwickelt --- von einer rein imperativen Kontrollstruktur hin zu
einem mächtigen Pattern-Matching-Mechanismus.

------------------------------------------------------------------------

## Java 11 und älter -- Klassischer `switch` (**nur Statement**)

**Eigenschaften:**

-   `switch` ist **kein Ausdruck**, liefert also keinen Wert.
-   **Fallthrough** ist Standard → `break` zwingend.
-   Keine kompakten Case-Labels möglich.

**Beispiel:**

``` java
int numLetters;
switch (day) {
    case MONDAY:
    case FRIDAY:
    case SUNDAY:
        numLetters = 6;
        break;
    case TUESDAY:
        numLetters = 7;
        break;
    default:
        numLetters = 8;
}
```

------------------------------------------------------------------------

## Java 12 -- Switch Expressions (Preview 1)

**Neuerungen:**

-   `switch` kann **Expression** sein.
-   **Arrow-Cases** ohne Fallthrough.
-   Mehrere Labels pro Case: `case A, B ->`
-   Rückgabewerte direkt möglich.
-   Blockarme nutzen noch `break value`.

**Beispiel:**

``` java
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY -> 7;
    default -> 8;
};
```

------------------------------------------------------------------------

## Java 13 -- Switch Expressions (Preview 2)

### Änderung:

-   `break value` wird ersetzt durch **`yield`**.

**Beispiel:**

``` java
int result = switch (day) {
    case MONDAY -> 0;
    case TUESDAY -> 1;
    default -> {
        int temp = compute(day);
        yield temp;
    }
};
```

------------------------------------------------------------------------

## Java 14 -- Finalisierung der switch expressions

-   `switch` Expressions sind jetzt **offiziell Teil der Sprache**.
-   Arrow-Syntax ist final.
-   Blockarme nutzen zwingend `yield`.
-   Expressions müssen exhaustiv sein.

**Beispiel:**

``` java
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY -> 7;
    case WEDNESDAY -> {
        int len = day.toString().length();
        yield len;
    }
    default -> 8;
};
```

------------------------------------------------------------------------

## Java 17--20 -- Pattern Matching Previews

-   Typ-Patterns in `switch`
-   Record-Destructuring
-   `when` Guards

------------------------------------------------------------------------

## Java 21 -- Finales Pattern Matching for switch

**Beispiel:**

``` java
static String describe(Object obj) {
    return switch (obj) {
        case null -> "null";
        case String s -> "String: " + s;
        case Integer i && i > 0 -> "Positive Zahl: " + i;
        case Integer i -> "Nicht-positive Zahl: " + i;
        case Point(int x, int y) -> "Point(" + x + ", " + y + ")";
        default -> "Unbekannter Typ";
    };
}
```

``` java
record Point(int x, int y) {}

String desc = switch (p) {
    case Point(int x, int y) when x == 0 && y == 0 -> "Origin";
    case Point(int x, int y) -> "Point(" + x + "," + y + ")";
    default -> "Other";
};
```

------------------------------------------------------------------------

## Zusammenfassung

  Java-Version   Status          Änderungen
  -------------- --------------- --------------------------------------------------
  11             Nur Statement   kein Wert, Fallthrough
  12             Preview         erste Expressions, `case A, B ->`, `break value`
  13             Preview         Einführung `yield`
  14             Final           switch Expressions final
  17--20         Preview         Pattern Matching
  21             Final           Pattern Matching for switch

  # Was kann alles in `case` verarbeitet werden? (Java 21)

Hier findest du eine vollständige Übersicht darüber, was in Java 21 in
`switch`-Case-Labels erlaubt ist -- inklusive klassischer Werte, neuer
Pattern-Matching-Funktionen und aller syntaktischen Regeln.

------------------------------------------------------------------------

## 1. Konstante Werte (klassisch)

`case` kann konstante Werte prüfen:

-   Ganzzahlen (`int`, `byte`, `short`, `char`)
-   `String`-Konstanten
-   `enum`-Konstanten
-   Compile-Time-Constant-Ausdrücke

**Beispiel:**

``` java
switch (n) {
    case 1:
        ...
    case 2:
        ...
    case 10 * 2:
        ...
}
```

------------------------------------------------------------------------

## 2. Mehrere Werte pro Case

Seit Java 12 möglich:

``` java
case MONDAY, FRIDAY, SUNDAY -> ...
```

------------------------------------------------------------------------

## 3. Arrow-Cases (`->`)

Moderne, fallthrough-freie Syntax:

``` java
case 1 -> "One";
case 2 -> "Two";
```

------------------------------------------------------------------------

## 4. default-Case

Standardfall:

``` java
default -> "Unknown";
```

------------------------------------------------------------------------

## 5. Type Patterns

Java 21 erlaubt Typprüfungen im switch:

``` java
case String s -> "String: " + s;
case Number n -> "Number: " + n;
```

------------------------------------------------------------------------

## 6. Record Patterns

Case kann Records entpacken:

``` java
record Point(int x, int y) {}
case Point(int x, int y) -> "X=" + x + ", Y=" + y;
```

Auch verschachtelt:

``` java
case Point(int x, Point(int y, int z)) -> ...
```

------------------------------------------------------------------------

## 7. null-Case

Java 21 erlaubt explizite Null-Prüfung:

``` java
case null -> "Null value";
```

Regeln:

-   Steht **vor** allen Typ-Patterns.
-   Fehlt `case null`, fängt `default` den Nullfall ab.

------------------------------------------------------------------------

## 8. Guards (`when`)

Bedingte Pattern:

``` java
case Integer i when i > 0 -> "Positive";
case Integer i when i == 0 -> "Zero";
case Integer i -> "Negative";
```

------------------------------------------------------------------------

## 9. Case-Blöcke mit `yield`

Nur in switch-Expressions erlaubt:

``` java
case 1 -> {
    int r = compute();
    yield r;
}
```

------------------------------------------------------------------------

## 10. Exhaustiveness

Eine switch-Expression muss vollständig sein.

Bei sealed classes erkennt der Compiler das automatisch:

``` java
sealed interface Shape permits Circle, Square, Rectangle {}

switch (shape) {
    case Circle c -> ...
    case Square s -> ...
    case Rectangle r -> ...
}
```

Kein `default` nötig.

------------------------------------------------------------------------

# Übersichtstabelle

  Art des Case-Labels   Beispiel                     Seit
  --------------------- ---------------------------- ------------------
  Konstante Werte       `case 1:`                    immer
  Mehrere Werte         `case A, B ->`               Java 12
  Arrow-Syntax          `case X ->`                  Java 12
  default               `default ->`                 immer
  Type Patterns         `case String s ->`           final in Java 21
  Record Patterns       `case Point(int x, int y)`   final in Java 21
  null                  `case null ->`               Java 21
  Guards                `case X when cond`           Java 21
  yield-Blöcke          `case X -> { yield v; }`     final in Java 14

------------------------------------------------------------------------

# Zusammenfassung

Ein `case` kann in Java 21 Folgendes verarbeiten:

-   Konstante Werte
-   Mehrere Werte pro Case
-   Arrow-Labels
-   default
-   Type Patterns
-   Record Patterns
-   null
-   Guards mit `when`
-   Blockarme mit `yield`
-   Sealed-Class-Exhaustiveness

Damit zählt Java 21 zu den modernsten Pattern-Matching-Sprachen der JVM.

