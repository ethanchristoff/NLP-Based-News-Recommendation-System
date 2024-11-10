package com.example.ethan_perera_2331419;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ConcurrentExecutor {

    private final ExecutorService executor;

    public ConcurrentExecutor(int threadCount) {
        this.executor = Executors.newFixedThreadPool(threadCount);
    }
    public void executeTask(Runnable task) {
        executor.submit(task);
    }
    public void shutdown() {
        executor.shutdown();
    }
}

public class concurrent_content {
    public void addNum(int num1, int num2, int taskNo) {
        long startTime = System.currentTimeMillis();  // Record start time

        int result = num1 + num2;
        System.out.printf("\nResult: %d for task No: %d\n",result,taskNo );

        long endTime = System.currentTimeMillis();  // Record end time
        long duration = endTime - startTime;  // Calculate time taken
        System.out.println("Time taken: " + duration + " ms for task number: " + taskNo);
    }

    public static void main(String[] args) {
        ConcurrentExecutor executor = new ConcurrentExecutor(3);

        concurrent_content content = new concurrent_content();

        Runnable task1 = () -> content.addNum(5, 10, 1);  // Expected result: 15
        Runnable task2 = () -> content.addNum(7, 8, 2);   // Expected result: 15
        Runnable task3 = () -> content.addNum(10, 20, 3); // Expected result: 30
        Runnable task4 = () -> content.addNum(3, 6, 4);   // Expected result: 9
        Runnable task5 = () -> content.addNum(2, 4, 5);   // Expected result: 6
        Runnable task6 = () -> content.addNum(1, 3, 6);  // Expected result: 4

        executor.executeTask(task1);
        executor.executeTask(task2);
        executor.executeTask(task3);
        executor.executeTask(task4);
        executor.executeTask(task5);
        executor.executeTask(task6);

        executor.shutdown();
    }
}