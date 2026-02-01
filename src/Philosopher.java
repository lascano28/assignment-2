public class Philosopher implements Runnable {

    private final Object leftFork;
    private final Object rightFork;
    private int philosopherId;
    private int mealsEaten = 0;

    public Philosopher(Object leftFork, Object rightFork, int id) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.philosopherId = id;
    }

    private void doAction(String action) throws InterruptedException {
        System.out.println(
                "Philosopher " + philosopherId + " - " + action);
        Thread.sleep(((int) (Math.random() * 1000)));
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // thinking
                doAction("Thinking");

                synchronized (leftFork) {
                    doAction("Picked up left fork");

                    synchronized (rightFork) {
                        // eating
                        doAction("Picked up right fork - eating");

                        mealsEaten++;

                        doAction("Put down right fork");
                    }

                    doAction("Put down left fork. Back to thinking");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Philosopher " + philosopherId +
                    " is leaving the table. Meals eaten: " + mealsEaten);
            return;
        }
    }

}