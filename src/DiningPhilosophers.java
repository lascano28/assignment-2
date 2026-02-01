public class DiningPhilosophers {

    public static void main(String[] args) throws Exception {

        final Philosopher[] philosophers = new Philosopher[5];
        Object[] forks = new Object[philosophers.length];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        Thread[] threads = new Thread[philosophers.length]; // ← STORE THREADS!

        for (int i = 0; i < philosophers.length; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % forks.length];

            if (i == philosophers.length - 1) {
                philosophers[i] = new Philosopher(rightFork, leftFork, i); // ← PASS ID
            } else {
                philosophers[i] = new Philosopher(leftFork, rightFork, i); // ← PASS ID
            }

            threads[i] = new Thread(philosophers[i], "Philosopher-" + i); // ← STORE THREAD
            threads[i].start();
        }

        // ← NEW: Simulation control
        System.out.println("=== Dining Philosophers simulation started ===\n");
        Thread.sleep(10000); // Run for 10 seconds

        System.out.println("\n=== Stopping simulation ===\n");
        for (Thread t : threads) {
            t.interrupt(); // Tell threads to stop
        }

        for (Thread t : threads) {
            t.join(); // Wait for threads to finish
        }

        System.out.println("\n=== Simulation complete ===");

    }
}

