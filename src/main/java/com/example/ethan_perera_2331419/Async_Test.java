package com.example.ethan_perera_2331419;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class Async_Test {
    public static void main(String[] args) {
        // Create an array of CompletableFutures, one for each number
        CompletableFuture<?>[] futures = IntStream.range(1, 11)
                .mapToObj(number -> CompletableFuture.runAsync(() -> {
                    System.out.println("Number: " + number + " printed on thread: " + Thread.currentThread().getName());
                }))
                .toArray(CompletableFuture[]::new);

        // Wait for all tasks to complete (optional)
        CompletableFuture.allOf(futures).join();

        System.out.println("All numbers printed asynchronously.");
    }
}

