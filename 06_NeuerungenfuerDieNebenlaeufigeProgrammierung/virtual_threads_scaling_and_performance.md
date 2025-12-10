# Warum viele klassische Threads problematisch sind – und Virtual Threads skalieren

Dieses Kapitel erklärt **anschaulich und praxisnah**,  
warum eine hohe Anzahl klassischer Java-Threads problematisch ist  
und welche **Performance- und Skalierungsvorteile Virtual Threads** bieten.

Gültig ab **Java 21**.

---

## 1. Klassische Threads: Warum sie nicht skalieren

### 1.1 Klassische Java-Threads = OS-Threads

Ein klassischer Java-Thread (`new Thread(...)`) ist:

- 1:1 an einen **Betriebssystem-Thread** gebunden
- besitzt:
  - einen **nativen Stack** (typisch 1–2 MB)
  - OS-gesteuertes Scheduling
  - teure Kontextwechsel

### Konsequenz

| Anzahl Threads | Speicherbedarf (ca.) |
|---------------|---------------------|
| 1.000 | 1–2 GB |
| 10.000 | sehr wahrscheinlich OOM |
| 100.000 | praktisch unmöglich |

➡️ **Nicht die CPU**, sondern **Speicher und Scheduling** sind der Engpass.

---

## 1.2 Blocking macht es noch schlimmer

```java
Thread.sleep(1000);
```

Bei klassischen Threads bedeutet das:

- OS-Thread blockiert
- keine andere Arbeit möglich
- Ressourcen bleiben reserviert

➡️ Viele blockierende Threads = viele **ungenutzte OS-Threads**

---

## 2. Problembeispiel: Viele klassische Threads

⚠️ Dieses Beispiel dient nur der Illustration.

```java
public class PlatformThreadsProblem {

    public static void main(String[] args) {
        for (int i = 0; i < 50_000; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ignored) {}
            }).start();
        }
    }
}
```

### Typische Folgen

- `OutOfMemoryError`
- OS-Thread-Limit erreicht
- extrem langsames System

➡️ **Skaliert nicht.**

---

## 3. Virtual Threads: Das neue Modell

### 3.1 Was ist anders?

Virtual Threads:

- sind **nicht** 1:1 an OS-Threads gebunden
- besitzen:
  - sehr kleinen Stack (KB, dynamisch wachsend)
  - JVM-gesteuertes Scheduling
- blockieren **keinen OS-Thread** bei IO

➡️ Ein OS-Thread kann **tausende Virtual Threads** tragen.

---

## 3.2 Dasselbe Beispiel mit Virtual Threads ✅

```java
public class VirtualThreadsScale {

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 50_000; i++) {
            Thread.startVirtualThread(() -> {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ignored) {}
            });
        }

        Thread.sleep(15_000); // JVM am Leben halten
    }
}
```

✅ läuft stabil  
✅ geringer Speicherverbrauch  
✅ System bleibt responsiv

---

## 4. Realistischer Vergleich mit ExecutorService

### 4.1 Klassischer Thread-Pool

```java
ExecutorService executor =
        Executors.newFixedThreadPool(100);

for (int i = 0; i < 10_000; i++) {
    executor.submit(() -> {
        Thread.sleep(1000);
        return null;
    });
}
```

**Probleme:**

- künstliches Limit (100 Threads)
- lange Warteschlangen
- hohe Latenz
- schwer zu tunen

---

### 4.2 Virtual Threads Executor ✅

```java
try (ExecutorService executor =
         Executors.newVirtualThreadPerTaskExecutor()) {

    for (int i = 0; i < 10_000; i++) {
        executor.submit(() -> {
            Thread.sleep(1000);
            return null;
        });
    }
}
```

**Vorteile:**

- 10.000 parallele Tasks
- kein Thread-Pool-Tuning
- blockierender Code bleibt blockierend
- deutlich bessere Skalierung

---

## 5. Warum klassische Threads bei hoher Anzahl scheitern

### Hauptprobleme

- ❌ hoher Speicherverbrauch
- ❌ teure Kontextwechsel
- ❌ OS-Scheduler-Overhead
- ❌ Blockierung verschwendet Ressourcen
- ❌ Thread-Pool-Management komplex

---

## 6. Warum Virtual Threads skalieren

- ✅ extrem leichtgewichtig
- ✅ Stack wächst nur bei Bedarf
- ✅ JVM plant Threads effizient
- ✅ IO blockiert keinen OS-Thread
- ✅ einfaches Programmiermodell

---

## 7. Wann Virtual Threads keinen Vorteil bringen

- CPU-bound Workloads
- sehr kurze, extrem feingranulare Tasks
- reine Rechenpipelines (Streams, ForkJoin)

➡️ Virtual Threads sind kein Ersatz für Parallelisierung,  
sondern für **skalierendes IO**.

---

## 8. Merksätze

> **Klassische Threads sind teuer – benutze wenige.**

> **Virtual Threads sind billig – benutze viele.**

> **Virtual Threads machen blockierenden Code skalierbar.**

---

Dieses Kapitel eignet sich als:
- Einführung in Project Loom
- Performance-Kapitel in Java-21-Tutorials
- Argumentationsgrundlage für Architekturentscheidungen
