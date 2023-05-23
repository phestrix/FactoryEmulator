package phestrix.ThreadPool.ThreadPool;

import phestrix.Factory.Worker.Worker;
import phestrix.Factory.factory.Factory;

import java.util.ArrayList;

import phestrix.ThreadPool.ThreadPool.TaskPool;

public class WorkerSectionPool {
    private TaskPool pool;
    private ArrayList<Thread> workers;
    private boolean initialized;

    public WorkerSectionPool(Factory factory, int priority) {
        workers = new ArrayList<>();
        pool = new TaskPool();
        for (int i = 0; i < factory.getWorkerCount(); ++i) {
            Thread thread = new Thread(new Worker(pool, factory), "Worker");
            thread.setPriority(priority);
            workers.add(thread);
        }
        initialized = true;
    }

    public void start() {
        if (!initialized) {
            return;
        }
        for (Thread thread : workers) {
            thread.start();
        }
    }

    public synchronized TaskPool getPool() {
        return pool;
    }

    public synchronized void destroy() {
        if (!initialized) {
            return;
        }
        for (Thread thread : workers) {
            thread.interrupt();
        }
        workers.clear();
        pool = null;
        workers = null;
        initialized = false;

    }

}
