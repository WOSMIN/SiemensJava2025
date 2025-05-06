## Siemens Java Internship - Code Refactoring Project

I have modified the code such that now supports async operations with @EnableAsync and returns a CompletableFuture for better asynchronous workflow handling. Instead of a simple counter it uses AtomicInteger to track processed items across threads.Meanwhile there were some processing logic mistakes that have been changed to create individual futures for each item, collect them, and then wait for all completions before returning results. 

## Requests
# You will have to:
1. Fix all logical errors while maintaining the same functionality
2. Implement proper error handling and validation
3. Be well-documented with clear, concise comments
4. Write test functions with as much coverage as possible
5. Make sure that the Status Codes used in Controller are correct
6. Find a way to implement an email validation
7. Refactor the **processItemsAsync** function
    The **processItemsAsync** function is supposed to:
      1. Asynchronously process EVERY item (retrieve from database, update status, and save)
      2. Track which items were processed
      3. Return a list when all items have been processed
      4. Provide an accurate list of all successfully processed items


