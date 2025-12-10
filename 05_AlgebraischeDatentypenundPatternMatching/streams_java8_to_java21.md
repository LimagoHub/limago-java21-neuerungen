# Streams in Java – Entwicklung von Java 8 bis Java 21

Dieses Kapitel beschreibt die **Entwicklung der Java Stream API** von ihrer
Einführung in **Java 8** bis zum aktuellen Stand in **Java 21**.
Der Fokus liegt auf **neuen Methoden**, **semantischen Änderungen** und
**Best Practices im modernen Java**.

---

## 1. Einführung: Streams in Java 8

### Motivation

Java 8 führte Streams ein, um:

- deklarative Datenverarbeitung zu ermöglichen
- funktionale Konzepte (Lambda, Method References) nutzbar zu machen
- parallele Verarbeitung zu vereinfachen

Streams sind:
- **lazy**
- **einmalig konsumierbar**
- **nicht speichernd**
- **nicht threadsicher**, aber thread-freundlich

---

## 2. Java 8 – Die Basis

### Zentrale Konzepte

- `Stream<T>`
- `IntStream`, `LongStream`, `DoubleStream`
- Intermediate Operations:
  - `map`, `filter`, `flatMap`, `sorted`, `distinct`
- Terminal Operations:
  - `forEach`, `reduce`, `collect`, `findFirst`, `anyMatch`
- Parallelisierung:
  - `stream.parallel()`

### Beispiel

```java
List<String> names =
    users.stream()
         .filter(User::active)
         .map(User::name)
         .collect(Collectors.toList());
```

---

## 3. Java 9 – Kleine, aber wichtige Ergänzungen

### Neue Methoden

- `takeWhile`
- `dropWhile`
- `iterate(seed, hasNext, next)`
- `Stream.ofNullable`

### Beispiel: `takeWhile`

```java
stream.takeWhile(x -> x < 10).forEach(System.out::println);
```

➡️ Erlaubt **frühes Abbrechen** bei sortierten oder logisch geordneten Daten.

---

## 4. Java 10 & 11 – API-Stabilisierung

### Java 10
- keine Stream-Änderungen

### Java 11
- keine neuen Stream-Methoden
- Streams gelten ab hier als **API-stabil**

➡️ Fokus verlagerte sich auf Sprachfeatures (var, HTTP Client, etc.)

---

## 5. Java 12–15 – Keine Änderungen

In diesen Versionen gab es **keine neuen Methoden oder Verhaltensänderungen**
in der Stream API.

➡️ Streams wurden bewusst **nicht weiter aufgebläht**.

---

## 6. Java 16 – `mapMulti` (wichtigste Neuerung)

### Problem vor Java 16

```java
stream
    .map(x -> List.of(x, x * 2))
    .flatMap(List::stream);
```

❌ unnötige Collections  
❌ zusätzliche Objekte  
❌ schlechtere Performance  

---

### Lösung: `mapMulti`

```java
stream.mapMulti((value, consumer) -> {
    consumer.accept(value);
    consumer.accept(value * 2);
});
```

### Vorteile

- keine temporären Collections
- weniger Allocation
- besser für Performance-kritische Pfade

Verfügbar für:
- `Stream<T>`
- `IntStream`, `LongStream`, `DoubleStream`

---

## 7. Java 17 – LTS & Sprachintegration

### Keine neuen Stream-Methoden

Aber:

- bessere Kombination mit:
  - Records
  - Pattern Matching
  - Sealed Classes

### Beispiel

```java
stream
    .filter(o -> o instanceof User u && u.active())
    .map(User::name)
    .toList();
```

---

## 8. Java 18 – `Stream.toList()` ✅

### Vorher

```java
List<String> list =
    stream.collect(Collectors.toList());
```

### Seit Java 18

```java
List<String> list = stream.toList();
```

### Eigenschaften von `toList()`

- unmodifiable
- garantiert nicht `null`
- klar spezifiziert
- oft effizienter als `Collectors.toList()`

⚠️ Unterschied:

```java
list.add("x"); // UnsupportedOperationException
```

---

## 9. Java 19–21 – Keine neuen Stream-Methoden

Seit Java 18:

- ❌ keine neuen Methoden
- ❌ keine semantischen Änderungen

### Warum?

Streams gelten als:
- vollständig
- ausgereift
- stabil

Der Fokus von Java 19+ liegt auf:
- Virtual Threads
- Structured Concurrency
- Scoped Values

---

## 10. Streams & Virtual Threads (Einordnung)

### Wichtige Klarstellung

- `stream.parallel()` nutzt **ForkJoinPool**
- Streams laufen **nicht automatisch auf Virtual Threads**

➡️ Streams sind ideal für:
- CPU-bound Workloads
- In-Memory-Verarbeitung

➡️ Virtual Threads sind ideal für:
- IO-bound Workloads
- Request/Response-Logik

---

## 11. Best Practices (Java 21)

### ✅ Verwenden

- `mapMulti` statt `flatMap` bei 1:n-Abbildungen
- `toList()` statt `Collectors.toList()`
- Streams für Berechnung & Transformation

### ❌ Vermeiden

- Streams für blockierendes IO
- zu komplexe Lambda-Ketten
- Side Effects in `map` / `filter`

---

## 12. Zusammenfassung

### Neue Stream-Features seit Java 8

| Java | Neuerung |
|----|---------|
| 9 | `takeWhile`, `dropWhile`, `ofNullable` |
| 16 | `mapMulti` |
| 18 | `Stream.toList()` |
| 19–21 | keine |

### Kernaussage

> **Streams sind bewusst stabil – neue Features kamen gezielt, nicht inflationär.**

---

## 13. Merksätze

- **Streams sind für CPU, nicht für IO**
- **`mapMulti` ist die moderne Alternative zu `flatMap`**
- **`toList()` ist der neue Standard**
- **Streams + Virtual Threads sind kein Ersatz füreinander**

---

Ende des Kapitels.
