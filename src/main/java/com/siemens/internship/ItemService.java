package com.siemens.internship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@EnableAsync // incorrect use of @Async without it
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private List<Item> processedItems = new ArrayList<>();
    private final AtomicInteger processedCount = new AtomicInteger(0);


    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     *
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     */

    /**
     I have modified the code such that now supports async operations with @EnableAsync and returns
     a CompletableFuture for better asynchronous workflow handling.
     Instead of a simple counter it uses AtomicInteger to track processed items across threads.
     Meanwhile there were some processing logic mistakes that have been changed to create individual futures for each item,
     collect them, and then wait for all completions before returning results.
     */

    @Async
    public CompletableFuture< List<Item> > processItemsAsync() {

        List<Long> itemIds = itemRepository.findAllIds();
        List<CompletableFuture<Item>> futures = new ArrayList<>(); //

        for (Long id : itemIds) {
            final long itemId = id;

            CompletableFuture<Item> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(100);

                    Item item = itemRepository.findById(id).orElse(null);
                    if (item != null) {
                        item.setStatus("PROCESSED");
                        Item savedItem = itemRepository.save(item); // save the updated item

                        processedCount.incrementAndGet();
                        processedItems.add(savedItem); // add to processed list
                        return savedItem;
                    }
                    return null;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Interrupted processing item: " + itemId);
                } catch (Exception e) {
                    System.err.println("Failed to process item " + itemId + ": " + e.getMessage());
                }
                return null;
            }, executor);

            futures.add(future);
        }
    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            // we finish the tasks and collect all the correctly processed items and return them
            .thenApply(v -> futures.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
    }

}

