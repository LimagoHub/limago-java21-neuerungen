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
