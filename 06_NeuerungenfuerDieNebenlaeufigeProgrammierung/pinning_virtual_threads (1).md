# Pinning bei Virtual Threads – Ursachen, Erkennung, Vermeidung

Dieses Kapitel erklärt **verständlich und praxisnah**,  
was **Pinning** bei Virtual Threads ist, **warum es entsteht**,  
**wie man es erkennt** und **wie man es vermeidet**.

Gültig für **Java 21+ (Project Loom)**.

---

## 1. Was ist Pinning?

**Pinning** bedeutet:

> Ein **virtueller Thread** kann seinen **Carrier-Thread (OS-Thread)**  
> **nicht freigeben**, obwohl er blockiert.

Normalerweise gilt bei Virtual Threads:

- blockierender Aufruf (IO, `sleep`, Lock)
- virtueller Thread wird **geparkt**
- OS-Thread führt **andere Virtual Threads** aus ✅

Bei Pinning:

- virtueller Thread blockiert
- **Carrier-Thread bleibt gebunden**
- Skalierungsvorteil geht verloren ❌

---

## 2. Warum ist Pinning problematisch?

Virtual Threads skalieren nur, wenn:

- viele virtuelle Threads
- auf **wenigen OS-Threads**
- effizient multiplexed werden

Pinning verhindert genau das.

### Folgen von Pinning

- blockierte OS-Threads
- sinkende Parallelität
- steigende Latenz
- Virtual Threads verhalten sich wie klassische Threads

➡️ Im schlimmsten Fall: **kein Vorteil gegenüber Plattform-Threads**

---

## 3. Hauptursache: `synchronized`

### ❌ Problematisches Beispiel (Pinning)

```java
public class PinningExample {

    static final Object LOCK = new Object();

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 3; i++) {
            int id = i;
            Thread.startVirtualThread(() -> {
                synchronized (LOCK) {
                    try {
                        System.out.println("Thread " + id + " hält LOCK");
                        Thread.sleep(2000); // ❌ Pinning
                        System.out.println("Thread " + id + " fertig");
                    } catch (InterruptedException ignored) {}
                }
            });
        }

        Thread.sleep(7000);
    }
}
```

### Warum passiert Pinning hier?

- `synchronized` nutzt **intrinsische Monitore**
- Monitore sind historisch **an OS-Threads gebunden**
- JVM darf Monitor & Stack nicht trennen
- blockierender Aufruf → Carrier bleibt gebunden

---

## 4. Vermeidung: Loom-aware Locks ✅

### ✅ Besser: `ReentrantLock`

```java
import java.util.concurrent.locks.ReentrantLock;

public class NoPinningExample {

    static final ReentrantLock LOCK = new ReentrantLock();

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 3; i++) {
            int id = i;
            Thread.startVirtualThread(() -> {
                LOCK.lock();
                try {
                    System.out.println("Thread " + id + " hält LOCK");
                    Thread.sleep(2000); // ✅ kein Pinning
                    System.out.println("Thread " + id + " fertig");
                } catch (InterruptedException ignored) {
                } finally {
                    LOCK.unlock();
                }
            });
        }

        Thread.sleep(7000);
    }
}
```

### Warum kein Pinning?

- `ReentrantLock` ist **Loom-aware**
- virtueller Thread wird korrekt geparkt
- Carrier-Thread wird freigegeben ✅

---

## 5. Weitere typische Pinning-Ursachen

| Ursache | Pinning? | Hinweis |
|------|--------|--------|
| `synchronized` + IO | ❌ | Hauptproblem |
| `synchronized` + `sleep()` | ❌ | sehr häufig |
| JNI-Aufrufe | ❌ | JVM kann nicht parken |
| Native Locks | ❌ | OS-gebunden |
| CPU-only Code | ✅ | unkritisch |

---

## 6. Wie erkennt man Pinning?

### 6.1 JVM-Flag (Java 21)

```bash
-Djdk.tracePinnedThreads=full
```

Beispiel:

```bash
java -Djdk.tracePinnedThreads=full MyApp
```

Typische Ausgabe:

```text
VirtualThread[#23] pinned on monitor ...
```

✅ sehr hilfreich in Tests & Staging  
❌ nicht für Produktivbetrieb gedacht

---

## 7. Best Practices zur Vermeidung ✅

### ✅ Do

- kurze kritische Abschnitte
- `ReentrantLock`, `Semaphore`, `StampedLock`
- blockierende IO **außerhalb** von Locks
- Virtual Threads für IO-lastige Workloads

### ❌ Don’t

- `synchronized` + blockierende Aufrufe
- lange Sleeps oder IO in Monitoren
- JNI-Aufrufe in Virtual Threads (wenn vermeidbar)
- unbewusstes Locking in Framework-Code

---

## 8. Wann `synchronized` trotzdem ok ist

✅ sehr kurze, **CPU-only** Critical Sections  
✅ kein IO, kein `sleep`, kein Warten  
✅ geringe Contention  

Beispiel:

```java
synchronized (lock) {
    counter++;
}
```

➡️ hier tritt **kein Pinning** auf

---

## 9. Mentales Modell

> **Virtual Threads skalieren nur,  
> wenn sie ihren Carrier freigeben können.**

Oder:

> **Pinning macht aus Virtual Threads wieder klassische Threads.**

---

## 10. Merksätze

- ❌ `synchronized` + blockieren = Pinning
- ✅ moderne Locks sind Loom-aware
- ✅ IO gehört **nicht** in Monitore
- ✅ Pinning früh erkennen (JVM-Flags!)

---

Dieses Kapitel eignet sich als:

- eigenständiges Loom-Kapitel
- Warn- & Best-Practice-Abschnitt in Tutorials
- Referenz für Code-Reviews bei Virtual Threads
