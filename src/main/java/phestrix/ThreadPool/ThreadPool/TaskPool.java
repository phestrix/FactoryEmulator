package phestrix.ThreadPool.ThreadPool;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskPool {
    private final Queue<FactoryTask> tasks;

    public TaskPool() {
        tasks = new LinkedBlockingQueue<>(5000);
    }

    public synchronized void pushTask(FactoryTask task) {
        tasks.offer(task);
        notifyAll();
    }

    public synchronized boolean isEmpty() {
        return tasks.size() == 0;
    }

    public synchronized FactoryTask getTask() {
        while (isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }

        }
        return tasks.peek();
    }

    public synchronized void removeTask() {
        if (isEmpty()) {
            return;
        }
        tasks.poll();
    }

    public synchronized int getCountOfTasks() {
        int result = 0;
        for (FactoryTask task : tasks) {
            result += task.getCountOfCars();
        }
        return result;
    }
}
