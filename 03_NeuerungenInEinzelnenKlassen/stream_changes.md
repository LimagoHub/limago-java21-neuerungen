# Stream-Neuerungen von Java 11 bis Java 21

Dieses Dokument gibt einen strukturierten Überblick über **direkte und indirekte Neuerungen der Java Stream API** von **Java 11 bis Java 21**.

---

## 1. Überblick

Die Stream-API selbst ist seit Java 8 bewusst stabil gehalten worden.  
Zwischen Java 11 und Java 21 gab es **nur wenige neue öffentliche Methoden**, dafür aber **wichtige Erweiterungen im Umfeld**, die Streams deutlich verbessern.

---

## 2. Direkte Neuerungen an der Stream-API

### Java 11

#### `Predicate.not(...)`

Hilfsmethode zur besseren Lesbarkeit von Filter-Ausdrücken:

```java
list.stream()
    .filter(Predicate.not(String::isBlank))
    .forEach(System.out::println);
```

**Vorteile**
- bessere Lesbarkeit
- weniger Negations-Lambdas

---

#### `Optional.stream()`

Erlaubt saubere Stream-Komposition:

```java
Stream<Optional<String>> s = ...

s.flatMap(Optional::stream)
 .forEach(System.out::println);
```

➡️ Macht `Optional` voll stream-fähig.

---

### Java 16

#### `Stream.toList()`

```java
List<String> result = stream.toList();
```

**Unterschied zu `Collectors.toList()`**

| toList() | Collectors.toList() |
|--------|---------------------|
| unveränderlich | meist veränderlich |
| klar spezifiziert | Implementierung offen |
| potenziell optimiert | weniger optimierbar |

➡️ Wichtigste Stream-Neuerung seit Java 8.

---

## 3. Indirekte, aber relevante Neuerungen

### 3.1 Sequenced Collections (Java 21)

Neue Interfaces:
- `SequencedCollection`
- `SequencedSet`
- `SequencedMap`

Streams profitieren von klar definierter **Encounter Order**:

```java
sequencedCollection.stream()
                   .forEach(System.out::println);
```

Neue Methoden:
- `getFirst() / getLast()`
- `removeFirst() / removeLast()`
- `reversed()`

---

### 3.2 Verbesserte NullPointerExceptions (Java 14+)

Beispiel:

```java
list.stream()
    .map(x -> x.foo().bar().baz())
    .toList();
```

Fehlermeldungen zeigen nun exakt:
```text
Cannot invoke "baz()" because "x.foo().bar()" is null
```

➡️ Deutlich bessere Debuggability von Stream-Pipelines.

---

### 3.3 Pattern Matching & Records (Java 16–21)

Verbesserte Lesbarkeit in `filter` und `map`:

```java
stream
  .filter(o -> o instanceof Person p && p.age() > 18)
  .map(Person::name)
  .toList();
```

---

### 3.4 JVM-Optimierungen

- bessere Inlining-Strategien
- effizientere Lambda-Ausführung
- verbesserte Parallel-Stream-Performance

➡️ Streams laufen heute spürbar schneller als unter Java 11.

---

## 4. Streams & Parallelität

### Parallel Streams

```java
stream.parallel().forEach(...)
```

⚠️ Nutzen weiterhin den `ForkJoinPool.commonPool`.

### Virtual Threads (Java 21)

Streams sind **nicht automatisch virtual-thread-aware**.

Empfehlung:

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    list.forEach(e ->
        executor.submit(() -> process(e)));
}
```

---

## 5. Zusammenfassung

### Neue Stream-Methoden (11–21)

| Version | Neuerung |
|------|---------|
| Java 11 | `Predicate.not()` |
| Java 11 | `Optional.stream()` |
| Java 16 | `Stream.toList()` |

### Wichtige Umfeld-Verbesserungen

- Sequenced Collections
- bessere NullPointerExceptions
- Pattern Matching
- Records
- JVM-Optimierungen

---

## 6. Fazit

- Die Stream-API ist bewusst stabil geblieben
- Wenige, aber sehr hochwertige Erweiterungen
- Streams sind heute:
  - schneller
  - besser debuggbar
  - klarer in der Semantik
  - hervorragend integriert in moderne Java-Features

Dieses Dokument eignet sich als Kapitel für ein Java‑17–21‑Tutorial oder als README-Baustein.
