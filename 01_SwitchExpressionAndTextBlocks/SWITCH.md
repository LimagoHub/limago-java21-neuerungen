Entwicklung der switch expressions von Java 11 bis Java 21

Die switch-Syntax hat sich von Java 11 bis Java 21 erheblich weiterentwickelt:
von einer rein imperativen Kontrollstruktur hin zu einem modernen, ausdrucksstarken Pattern-Matching-Mechanismus.

Im Folgenden findest du die Entwicklung chronologisch, inklusive Beispielen und Kernänderungen — vollständig im Markdown-Format.

Java 11 und älter – klassischer switch (nur Statement)

switch kann nur ein Statement sein, nie ein Ausdruck.

Kein Rückgabewert möglich.

Fallthrough ist Standard → break zwingend notwendig.

Mehrere Case-Labels nicht in einer Zeile nutzbar.

Beispiel (Java 11):

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

Java 12 – switch expressions (Preview #1, JEP 325)

Java 12 führt die switch-Expression testweise ein:

🆕 Wichtige Neuerungen

switch kann Expression oder Statement sein.

Neue Syntax: case A, B -> ohne Fallthrough.

Ergebniswert kann direkt zugewiesen werden:

int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY -> 7;
    default -> 8;
};


In Block-Armen durfte man damals break value verwenden (nur in diesem Preview):

int r = switch (day) {
    case WEDNESDAY -> {
        int len = day.toString().length();
        break len;  // nur in Java 12!
    }
    default -> 0;
};


Nur mit --enable-preview nutzbar.

Java 13 – switch expressions (Preview #2, JEP 354)

Java 13 ersetzt break value durch yield, eine semantisch klarere Form.

🆕 Wesentliche Änderung

yield gibt den Wert einer switch-Expression aus einem Block zurück.

Beispiel:

int result = switch (day) {
    case MONDAY -> 0;
    case TUESDAY -> 1;
    default -> {
        int temp = compute(day);
        yield temp;   // ersetzt break value
    }
};

Java 14 – switch expressions werden final (JEP 361)

Ab Java 14 ist die Funktionalität Standardbestandteil der Sprache.

✨ Was ist jetzt endgültig festgelegt?

switch kann Expression oder Statement sein.

Pfeil-Syntax case X -> ist voll unterstützt.

Block-Arme verwenden immer yield, nie break value.

switch-Expressions müssen exhaustiv sein (müssen alle Fälle abdecken).

Beispiel (gültig ab Java 14, heute Standard):

int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY -> 7;
    case WEDNESDAY -> {
        int len = day.toString().length();
        yield len;
    }
    default -> 8;
};


Java 14 schließt die grundlegende Modernisierung des switch ab.
Spätere Versionen erweitern switch hauptsächlich durch Pattern Matching.

Java 17–20 – Vorbereitung des Pattern Matching für switch (Preview-Phasen)

In diesen Versionen bleiben die switch expressions stabil, aber Java fügt neue Fähigkeiten hinzu:

🧩 Neue Features in Previews

Typ-Patterns in switch (z. B. case String s ->)

Guards (when-Klauseln)

Umgang mit null in Pattern-Switches

Diese Phasen sind rein vorbereitend für Java 21.

Java 21 – Pattern Matching for switch wird final (JEP 441)

Mit Java 21 wird das switch-System vollständig generalisiert:

switch kann jetzt über Typen, Records und komplexe Patterns matchen.

Ideal in Kombination mit sealed Klassen und Records.

Beispiel (Java 21, final):
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


Mit Records:

record Point(int x, int y) {}

String desc = switch (p) {
    case Point(int x, int y) when x == 0 && y == 0 -> "Origin";
    case Point(int x, int y) -> "Point(" + x + "," + y + ")";
    default -> "Other";
};