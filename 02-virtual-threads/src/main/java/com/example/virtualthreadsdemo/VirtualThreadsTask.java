package com.example.virtualthreadsdemo;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class VirtualThreadsTask {

    private static final int TASKS = 1000;
    private static final int SLEEP_MS = 1000;
    private static final int POOL_SIZE = 64;

    public static void main(String[] args) throws Exception {
        System.out.println("Tasks=" + TASKS + ", sleepMs=" + SLEEP_MS + ", fixedPoolSize=" + POOL_SIZE);

        long fixedTime = runWithFixedThreadPool(TASKS, SLEEP_MS, POOL_SIZE);
        long vtTime    = runWithVirtualThreads(TASKS, SLEEP_MS);

        System.out.println("\n== Results ==");
        System.out.printf("FixedThreadPool: %d ms (≈ %.1f tasks/s)%n",
                fixedTime, TASKS / (fixedTime / 1000.0));
        System.out.printf("VirtualThreads : %d ms (≈ %.1f tasks/s)%n",
                vtTime, TASKS / (vtTime / 1000.0));
    }

    private static long runWithFixedThreadPool(int tasks, int sleepMs, int poolSize) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        try {
            List<Future<?>> futures = new ArrayList<>(tasks);
            Instant start = Instant.now();

            for (int i = 0; i < tasks; i++) {
                futures.add(pool.submit(ioTask(sleepMs)));
            }
            for (Future<?> f : futures) {
                f.get();
            }

            long ms = Duration.between(start, Instant.now()).toMillis();
            System.out.printf("FixedThreadPool finished in %d ms%n", ms);
            return ms;
        } finally {
            pool.shutdown();
        }
    }

    private static long runWithVirtualThreads(int tasks, int sleepMs) throws Exception {
        try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>(tasks);
            Instant start = Instant.now();

            for (int i = 0; i < tasks; i++) {
                futures.add(exec.submit(ioTask(sleepMs)));
            }
            for (Future<?> f : futures) {
                f.get();
            }

            long ms = Duration.between(start, Instant.now()).toMillis();
            System.out.printf("VirtualThreads finished in %d ms%n", ms);
            return ms;
        }
    }

    private static Runnable ioTask(int sleepMs) {
        return () -> {
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        };
    }
}