# Pinning erkennen – Logs & JVM-Flags (Virtual Threads)

Dieses Kapitel zeigt **konkret und praxisnah**,  
wie du **Pinning bei Virtual Threads erkennst**,  
mithilfe von **JVM-Flags, Logs und typischen Symptomen**.

Gültig für **Java 21+ (Project Loom)**.

---

## 1. Warum Pinning-Erkennung wichtig ist

Pinning ist oft:

- **unsichtbar** im normalen Code
- erst unter Last problematisch
- schwer zu debuggen ohne JVM-Unterstützung

➡️ Deshalb ist **frühes Erkennen** entscheidend.

---

## 2. Das wichtigste JVM-Flag: `jdk.tracePinnedThreads`

### 2.1 Flag aktivieren

```bash
java -Djdk.tracePinnedThreads=full MyApp
```

Alternativ (weniger Details):

```bash
java -Djdk.tracePinnedThreads=short MyApp
```

### Modi

| Wert | Bedeutung |
|----|----------|
| `short` | kurze Hinweise (empfohlen für Überblick) |
| `full` | vollständiger Stacktrace (Debug/Test) |

---

## 3. Beispiel: Pinning bewusst erzeugen

```java
public class PinningDemo {

    static final Object LOCK = new Object();

    public static void main(String[] args) throws Exception {

        Thread.startVirtualThread(() -> {
            synchronized (LOCK) {
                try {
                    Thread.sleep(2000); // Pinning
                } catch (InterruptedException ignored) {}
            }
        });

        Thread.sleep(3000);
    }
}
```

Start mit:

```bash
java -Djdk.tracePinnedThreads=full PinningDemo
```

---

## 4. Typische Log-Ausgabe bei Pinning

### 4.1 Kurzform (`short`)

```text
VirtualThread[#21] pinned on monitor java.lang.Object@6d311334
```

Bedeutung:

- ein Virtual Thread ist gepinnt
- Ursache: Monitor (`synchronized`)
- betroffener Carrier-Thread blockiert

---

### 4.2 Vollständige Ausgabe (`full`)

```text
VirtualThread[#21] pinned on monitor java.lang.Object@6d311334
    at PinningDemo.lambda$main$0(PinningDemo.java:10)
    at java.lang.Thread.run(Thread.java:1583)
```

✅ Zeigt:
- betroffene Codezeile
- exakte Ursache
- Call Stack

---

## 5. Weitere Hinweise auf Pinning (ohne Flags)

Auch ohne Flag kannst du Pinning vermuten, wenn:

- Virtual Threads **nicht skalieren**
- hohe Latenz trotz geringer CPU-Last
- viele blockierte OS-Threads
- Thread-Dumps zeigen:
  - Virtual Threads im `BLOCKED`-Zustand
  - Carrier-Threads warten auf Monitore

➡️ **Indizien**, aber kein Beweis → Flag nutzen!

---

## 6. Thread Dumps richtig lesen

### Thread Dump erzeugen

```bash
jstack <pid>
```

### Typisches Muster bei Pinning

- viele `VirtualThread[#...]`
- Zustand: `BLOCKED`
- Stacktrace zeigt:
  - `synchronized`
  - native / JNI-Aufrufe

Beispiel (vereinfacht):

```text
"VirtualThread[#23]" BLOCKED
  at java.lang.Object.wait(Native Method)
  - waiting on <0x00000000> (a java.lang.Object)
```

---

## 7. Kombination mit Lasttests (Best Practice)

Pinning zeigt sich oft erst unter Last.

Empfohlener Ablauf:

1. Lasttest starten
2. JVM mit `-Djdk.tracePinnedThreads=short` starten
3. Logs beobachten
4. bei Treffern:
   - auf `full` wechseln
   - Code analysieren
   - Locks refactoren

---

## 8. Typische Ursachen, die Logs verraten

| Log-Hinweis | Ursache |
|-----------|--------|
| `pinned on monitor` | `synchronized` |
| `native method` | JNI / native IO |
| viele identische Stacks | Hot Lock |
| seltene Einträge | meist unkritisch |
| viele Einträge | akutes Problem |

---

## 9. Was tun, wenn Pinning gefunden wird?

### ✅ Maßnahmen

- `synchronized` → `ReentrantLock`
- blockierendes IO aus Locks herausziehen
- kritische Abschnitte verkürzen
- Libraries prüfen (Frameworks!)

### ❌ Nicht ignorieren

- Pinning skaliert **nicht**
- unter Last wird es schnell kritisch
- besonders gefährlich in Request-Handling

---

## 10. Merksätze

> **Pinning sieht man nicht – man misst es.**

> **Ohne `jdk.tracePinnedThreads` tappst du im Dunkeln.**

> **Ein gepinnter Virtual Thread ist ein verlorener Skalierungsvorteil.**

---

Dieses Kapitel eignet sich als:

- Debugging-Kapitel in Loom-Tutorials
- Checkliste für Lasttests
- Referenz für Produktionsprobleme mit Virtual Threads
