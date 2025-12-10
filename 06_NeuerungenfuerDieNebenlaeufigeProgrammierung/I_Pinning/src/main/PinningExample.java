package main;

import java.util.concurrent.locks.ReentrantLock;

public class PinningExample {

    static final Object LOCK_OBJECT = new Object();
    static final ReentrantLock LOCK = new ReentrantLock();

    /*public static void main(String[] args) throws Exception {

        for (int i = 0; i < 3; i++) {
            int id = i;
            Thread.startVirtualThread(() -> {
                synchronized (LOCK_OBJECT) {
                    try {
                        System.out.println("Thread " + id + " hält LOCK");
                        Thread.sleep(2000); // ❌ blockiert + synchronized = PINNING
                        System.out.println("Thread " + id + " fertig");
                    } catch (InterruptedException ignored) {}
                }
            });
        }

        Thread.sleep(7000);
    }
*/
    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 3; i++) {
            int id = i;
            Thread.startVirtualThread(() -> {
                LOCK.lock();
                try {
                    System.out.println("Thread " + id + " hält LOCK");
                    Thread.sleep(2000); // ✅ KEIN Pinning
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
