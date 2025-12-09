# Virtuelle Threads in Java (Project Loom)

Dieses Kapitel bietet eine umfassende Einführung in **virtuelle Threads** (Project Loom) und berücksichtigt zentrale Aspekte wie Motivation, Lebenszyklus, ExecutorService, Structured Concurrency, Locks, Contention, Starvation und Pinning.

---

## 1. Motivation: Warum virtuelle Threads?

Klassische Java-Threads sind **1:1 an Betriebssystem-Threads gebunden**.  
Das führt bei vielen gleichzeitigen, meist IO-blockierenden Aufgaben zu:

- hohem Speicherverbrauch (Stack pro Thread)
- begrenzter Skalierbarkeit
- komplexem asynchronem Code (Callbacks, Reactive APIs)

**Virtuelle Threads** lösen dieses Problem:

- extrem leichtgewichtig
- Millionen Threads pro JVM möglich
- blockierende Aufrufe blockieren *keine* OS-Threads
- synchroner, gut lesbarer Code bleibt erhalten

---

## 2. Was sind virtuelle Threads?

Virtuelle Threads sind Java-Threads, die:

- von der JVM statt vom OS geplant werden
- auf **Carrier Threads** (OS-Threads) ausgeführt werden
- bei Blockierung automatisch **geparkt** werden
- später auf einem beliebigen Carrier fortgesetzt werden können

```java
Thread.startVirtualThread(() -> {
    Thread.sleep(1000);
    System.out.println("Hello from virtual thread");
});
```

---

## 3. Lebenszyklus virtueller Threads

- virtuelle Threads sind **keine Daemon-Threads**
- sie laufen weiter, auch wenn `main` endet
- die JVM beendet sich erst, wenn alle virtuellen Threads beendet sind
- Synchronisation erfolgt wie bei klassischen Threads (`join`, Locks, etc.)

Empfohlene Verwaltung:

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> doWork());
}
```

---

## 4. ExecutorService mit virtuellen Threads

```java
ExecutorService executor =
        Executors.newVirtualThreadPerTaskExecutor();
```

Eigenschaften:

- jeder Task bekommt einen eigenen virtuellen Thread
- kein Pool, keine Begrenzung
- ideal für IO-lastige Workloads
- `submit()` liefert ein `Future`

Best Practice:

- immer mit try-with-resources verwenden
- für CPU-bound Tasks weiterhin klassische Thread-Pools nutzen

---

## 5. Structured Concurrency & StructuredTaskScope

`StructuredTaskScope` (Preview in Java 21) ermöglicht **strukturierte Nebenläufigkeit**:

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var a = scope.fork(() -> taskA());
    var b = scope.fork(() -> taskB());

    scope.join();
    scope.throwIfFailed();

    return a.get() + b.get();
}
```

Vorteile:

- Tasks sind an einen Scope gebunden
- automatische Fehlerbehandlung
- kein „verlorener“ Hintergrund-Thread
- deutlich übersichtlicher als ExecutorService

### fork() vs submit()

| submit() | fork() |
|--------|-------|
| unstrukturierte Tasks | strukturierte Tasks |
| manuelles Shutdown | automatisches Aufräumen |
| Fehler pro Future | Fehler zentral im Scope |

---

## 6. Locks und Synchronisation

Virtuelle Threads ändern **nicht** das Java Memory Model.

Alle bekannten Mechanismen gelten weiterhin:

- `synchronized`
- `ReentrantLock`
- `ReadWriteLock`
- `StampedLock`
- `volatile`
- Atomics (`AtomicInteger`, …)

Empfehlung für virtuelle Threads:

- kritische Abschnitte sehr kurz halten
- keine Blockierung innerhalb von Locks
- atomare oder lockfreie Strukturen bevorzugen

---

## 7. Contention

**Contention** bedeutet, dass mehrere Threads um dieselbe Ressource konkurrieren.

- hohe Contention → viel Warten, wenig Fortschritt
- viele virtuelle Threads machen Contention sichtbarer
- mehr Threads ≠ mehr Performance

Strategien zur Reduktion:

- weniger geteilten Zustand
- feinere Locks
- lockfreie Datenstrukturen
- Arbeit außerhalb von Locks erledigen

---

## 8. Starvation

**Starvation** liegt vor, wenn ein Thread dauerhaft keinen Fortschritt macht.

Ursachen:

- unfaire Locks
- sehr viele kurze Tasks
- Reader/Writer-Ungleichgewicht

Beispiel:

```java
new ReentrantLock(true); // fair
```

Fairness verhindert Starvation, kostet aber Performance.

---

## 9. Pinning (sehr wichtig!)

**Pinning** tritt auf, wenn ein virtueller Thread nicht geparkt werden kann und
dadurch einen Carrier Thread blockiert.

Hauptursachen:

1. `synchronized` + blockierende Operationen
2. native Methoden (JNI)
3. lange gehaltene Locks

Beispiel (schlecht):

```java
synchronized (lock) {
    Thread.sleep(1000); // pinnt!
}
```

Folgen:

- Verlust der Skalierbarkeit
- Verhalten ähnlich klassischer Threads

Erkennung:

```text
Virtual thread pinning detected
```

Vermeidung:

- keine Blockierung innerhalb von Locks
- kurze kritische Abschnitte
- moderne Concurrency-APIs nutzen

---

## 10. Scoped Values (Preview)

Scoped Values sind eine Alternative zu `ThreadLocal`:

```java
static final ScopedValue<String> USER =
        ScopedValue.newInstance();

ScopedValue.where(USER, "Alice").run(() -> {
    System.out.println(USER.get());
});
```

Vorteile:

- immutable
- automatisch aufgeräumt
- perfekt für virtuelle Threads
- sicher bei parallelem Zugriff

---

## 11. Fazit

Virtuelle Threads ermöglichen:

- massive Skalierbarkeit
- einfachen, synchronen Code
- bessere Nutzung moderner Hardware

Aber:

- Locks, Contention, Starvation und Pinning bleiben relevant
- falsche Synchronisation kann Vorteile zunichtemachen

**Best Practices:**

- IO-bound Tasks → virtuelle Threads
- CPU-bound Tasks → klassische Pools
- strukturierte Concurrency bevorzugen
- Locks kurz halten oder vermeiden
- Pinning aktiv überwachen

---

Dieses Kapitel kann als Grundlage für produktiven Einsatz
virtueller Threads in Java 21 und später dienen.
