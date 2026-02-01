# Dining Philosophers Problem - Design Documentation

## Overview

This project implements a solution to the classic Dining Philosophers problem using Java threads. The simulation runs 5 philosophers who alternate between thinking and eating, sharing forks between them.

## Design Rationale

### Representing the Philosophers

Each philosopher is represented by a `Philosopher` class that implements `Runnable`. Each philosopher has:
- **An ID number** (0-4) to identify them in the output
- **Two fork references** (left and right) that they need to acquire before eating
- **A meal counter** that tracks how many times they've successfully eaten

Philosophers run on separate threads, allowing them to think and eat independently and concurrently. Each philosopher's thread executes an infinite loop where they think, pick up forks, eat, and put down forks.

### Representing the Table, Forks, and Spaghetti

- **The Table**: Implicitly represented by the circular arrangement of philosophers and forks in the `DiningPhilosophers` main class. The use of modulo arithmetic `(i + 1) % forks.length` creates the circular seating arrangement.

- **The Forks**: Represented as simple `Object` instances. There are exactly 5 forks, one placed between each pair of adjacent philosophers. These objects serve as locks (monitors) for thread synchronization. A philosopher must acquire the lock on a fork object before they can use it.

- **The Spaghetti**: Not explicitly represented. We assume there's always spaghetti available - the challenge is getting access to the forks, not the food itself.

### Representing the Thinking and Eating Phases

The philosopher's lifecycle alternates between two phases:

- **Thinking Phase**: The philosopher is not holding any forks. This is represented by code that runs *outside* the synchronized blocks. During this time, the philosopher doesn't compete for resources.

- **Eating Phase**: The philosopher holds both forks. This is represented by the code inside the *nested* synchronized blocks (lines 27-40 in `Philosopher.java`). A philosopher can only eat when they've successfully acquired locks on both their left and right fork.

Both phases include a random sleep duration (0-1000ms) to simulate the variable time spent thinking or eating.

## Deadlock Prevention Strategy

### The Asymmetric Fork Ordering Solution

The algorithm prevents deadlocks by breaking the circular wait condition. Here's how it works:

- **Philosophers 0-3**: Pick up their left fork first, then their right fork
- **Philosopher 4** (the last one): Picks up their right fork first, then their left fork

This asymmetry is implemented in lines 18-22 of `DiningPhilosophers.java`.

### Why This Prevents Deadlocks

A deadlock occurs when all philosophers pick up one fork and wait forever for the second fork. In the standard approach where everyone picks up their left fork first:
1. Philosopher 0 grabs fork 0
2. Philosopher 1 grabs fork 1
3. Philosopher 2 grabs fork 2
4. Philosopher 3 grabs fork 3
5. Philosopher 4 grabs fork 4
6. Now everyone is waiting for their right fork, which is held by their neighbor → **deadlock**

By reversing the order for philosopher 4, we ensure this circular wait cannot happen. At least one philosopher will always be trying to acquire forks in a different order, preventing the entire circle from forming.

**Deadlocks are completely impossible** with this solution, not just improbable.

## Starvation Analysis

While deadlocks are impossible, **starvation is still theoretically possible**, though very unlikely.

Java's `synchronized` keyword doesn't guarantee fairness. When a lock is released, any waiting thread might acquire it - not necessarily the one that's been waiting longest. This means that a philosopher could wait indefintely while others eat repeatedly or if 2 philosophers are alternating then a third might struggle to get both forks. However, this is very unlikely because of the random sleep durations that vary the timing, as well as JVM's thread scheduler that provides reasonably fair scheduling

The output shows all philosophers successfully eating multiple times during the simulation, confirming that starvation doesn't occur in practice.

## How to Run

Compile:
```bash
javac src/*.java
```

Run:
```bash
java -cp src DiningPhilosophers
```

The simulation runs for 10 seconds and displays output showing each philosopher's actions. At the end, each philosopher reports how many meals they ate.

## Sample Output

```
=== Dining Philosophers simulation started ===

Philosopher 0 - Thinking
Philosopher 2 - Thinking
Philosopher 1 - Thinking
Philosopher 0 - Picked up left fork
Philosopher 3 - Thinking
Philosopher 0 - Picked up right fork - eating
...
```

## Concurrency Mechanisms Used

- **Threads**: Each philosopher runs on their own thread (`Thread` class)
- **Synchronization**: Fork access is controlled using `synchronized` blocks
- **Monitor Locks**: Each fork object serves as a monitor for mutual exclusion
- **Thread Interruption**: Used for graceful shutdown of the simulation
