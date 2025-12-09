package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

public class LockOverviewExample {

    // Gemeinsamer Zustand für Beispiele
    static int plainCounter = 0;                     // unsynchronisiert
    static int synchronizedCounter = 0;
    static final Object syncLock = new Object();

    static final ReentrantLock reentrantLock = new ReentrantLock();
    static int reentrantCounter = 0;

    static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    static int rwSharedValue = 0;

    static final StampedLock stampedLock = new StampedLock();
    static int stampedValue = 0;

    static final AtomicInteger atomicCounter = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {

        // ------------------------------------------------------------
        // 1) Kein Lock (nur als Negativ-Beispiel)
        // ------------------------------------------------------------
        // Vorteile:
        //  - einfach, keine Overheads
        // Nachteile:
        //  - bei mehreren Threads: Race Conditions, Datenkorruption
        //  - nie in concurrent Code mit gemeinsamem Zustand verwenden
        plainCounter++; // nur demonstrativ, NICHT thread-safe

        // ------------------------------------------------------------
        // 2) synchronized (intrinsischer Monitor-Lock)
        // ------------------------------------------------------------
        // Vorteile:
        //  - einfach zu lesen/schreiben
        //  - vom Compiler/JVM optimiert
        //  - reentrant (Thread kann denselben Lock erneut betreten)
        // Nachteile:
        //  - keine tryLock-/timeout-Möglichkeiten
        //  - kann bei virtuellen Threads zu Pinning führen,
        //    wenn innerhalb des synchronized-Blocks blockiert wird
        synchronized (syncLock) {
            synchronizedCounter++;
        }

        // Auch möglich:
        // public synchronized void foo() { ... }

        // ------------------------------------------------------------
        // 3) ReentrantLock
        // ------------------------------------------------------------
        // Vorteile:
        //  - mehr Kontrolle als synchronized (tryLock, fairness, Conditions)
        //  - explizit sperr-/entsperrbar
        // Nachteile:
        //  - mehr Schreibaufwand (immer unlock im finally!)
        //  - ebenfalls potenziell Pinning mit virtuellen Threads
        reentrantLock.lock();
        try {
            reentrantCounter++;
        } finally {
            reentrantLock.unlock();
        }

        // tryLock-Beispiel – vermeidet Blockierung:
        if (reentrantLock.tryLock(10, TimeUnit.MILLISECONDS)) {
            try {
                reentrantCounter++;
            } finally {
                reentrantLock.unlock();
            }
        }

        // ------------------------------------------------------------
        // 4) ReadWriteLock (ReentrantReadWriteLock)
        // ------------------------------------------------------------
        // Vorteile:
        //  - viele Leser können parallel lesen
        //  - Schreiber erhalten exklusiven Zugriff
        // Nachteile:
        //  - komplexer als ein einfacher Lock
        //  - bei vielen Schreibern weniger Vorteil
        //  - kann zu Writer-Starvation führen (ohne Fairness)
        rwLock.readLock().lock();
        try {
            int snapshot = rwSharedValue; // paralleles Lesen erlaubt
        } finally {
            rwLock.readLock().unlock();
        }

        rwLock.writeLock().lock();
        try {
            rwSharedValue++;
        } finally {
            rwLock.writeLock().unlock();
        }

        // ------------------------------------------------------------
        // 5) StampedLock
        // ------------------------------------------------------------
        // Vorteile:
        //  - sehr performant für viele Leser (optimistic read)
        //  - weniger Contention als klassische ReadWriteLocks
        // Nachteile:
        //  - NICHT reentrant
        //  - API komplexer, Fehler leichter möglich
        //  - mit Vorsicht bei virtuellen Threads (Pinning möglich)
        long stamp = stampedLock.readLock();
        try {
            int v = stampedValue;
        } finally {
            stampedLock.unlockRead(stamp);
        }

        // Optimistic Read:
        long optStamp = stampedLock.tryOptimisticRead();
        int v1 = stampedValue;
        if (!stampedLock.validate(optStamp)) {
            // Fallback auf "normalen" Read-Lock
            stamp = stampedLock.readLock();
            try {
                v1 = stampedValue;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }

        // Write Lock:
        long writeStamp = stampedLock.writeLock();
        try {
            stampedValue++;
        } finally {
            stampedLock.unlockWrite(writeStamp);
        }

        // ------------------------------------------------------------
        // 6) volatile (kein Lock, aber Sichtbarkeits-Garantie)
        // ------------------------------------------------------------
        // Vorteile:
        //  - einfache Sichtbarkeitsgarantie zwischen Threads
        // Nachteile:
        //  - keine atomischen Compound-Operationen
        //  - kein gegenseitiger Ausschluss
        //
        // -> Deshalb hier nur erwähnenswert, aber kein echtes „Lock“.
        //
        // Beispiel (nur Illustration, nicht komplett):
        // private static volatile boolean running = true;
        // Thread 1: while (running) { ... }
        // Thread 2: running = false;

        // ------------------------------------------------------------
        // 7) Atomic-Klassen (AtomicInteger & Co.)
        // ------------------------------------------------------------
        // Vorteile:
        //  - lockfreie, atomare Operationen (CAS)
        //  - sehr schnell und gut skalierbar bei einfachen Operationen
        // Nachteile:
        //  - nur für einfache Updates geeignet
        //  - komplexere invarianten-Operationen werden unübersichtlich
        atomicCounter.incrementAndGet();
        int current = atomicCounter.get();

        // ------------------------------------------------------------
        // 8) Semaphore
        // ------------------------------------------------------------
        // Vorteile:
        //  - begrenzt parallelen Zugriff (z.B. max 3 gleichzeitig)
        //  - kann fair konfiguriert werden
        // Nachteile:
        //  - etwas niedrigeres Abstraktionsniveau
        //  - Fehler bei acquire/release führen leicht zu Problemen
        Semaphore semaphore = new Semaphore(3 /* permits */, false /* unfair (schneller) */);

        if (semaphore.tryAcquire()) {
            try {
                // max. 3 Threads gleichzeitig hier
            } finally {
                semaphore.release();
            }
        }

        // ------------------------------------------------------------
        // 9) CountDownLatch
        // ------------------------------------------------------------
        // Vorteile:
        //  - einfaches Mechanismus: "warte, bis N Dinge fertig sind"
        // Nachteile:
        //  - einmalig verwendbar (Latch kann nicht resetten)
        int numberOfWorkers = 3;
        CountDownLatch latch = new CountDownLatch(numberOfWorkers);

        ExecutorService pool = Executors.newFixedThreadPool(numberOfWorkers);
        for (int i = 0; i < numberOfWorkers; i++) {
            pool.submit(() -> {
                try {
                    // ... tue irgendetwas ...
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // wartet, bis alle Worker countDown() aufgerufen haben
        pool.shutdown();

        // ------------------------------------------------------------
        // 10) CyclicBarrier
        // ------------------------------------------------------------
        // Vorteile:
        //  - mehrere Threads treffen sich an einer "Barriere"
        //  - kann mehrfach verwendet werden (cyclic)
        // Nachteile:
        //  - etwas komplexer im Verständnis
        int parties = 3;
        CyclicBarrier barrier = new CyclicBarrier(parties,
                () -> System.out.println("Alle sind am Startpunkt angekommen"));

        ExecutorService pool2 = Executors.newFixedThreadPool(parties);
        for (int i = 0; i < parties; i++) {
            pool2.submit(() -> {
                try {
                    // Phase 1
                    barrier.await(); // wartet auf andere

                    // Phase 2
                    barrier.await(); // noch eine Synchronisationsphase
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        pool2.shutdown();

        // ------------------------------------------------------------
        // Hinweis zu virtuellen Threads:
        // ------------------------------------------------------------
        // Alle oben gezeigten Mechanismen funktionieren grundsätzlich
        // auch mit virtuellen Threads (Loom).
        //
        // ABER:
        //  - Lange blockierende Abschnitte innerhalb von synchronized
        //    oder bestimmten Lock-Implementierungen können zu "Pinning"
        //    führen (virtueller Thread blockiert seinen Carrier-Thread).
        //
        // Best Practices mit virtuellen Threads:
        //  - kritische Abschnitte kurz halten
        //  - bevorzugt immutable Daten- und lockfreie Strukturen nutzen
        //  - bei IO: nicht innerhalb von Locks blockieren
        //  - StructuredTaskScope und ScopedValue als moderne Mittel
        //    für Zusammenspiel von Concurrency und Kontext verwenden.

        System.out.println("Beispielprogramm beendet.");
    }
}
