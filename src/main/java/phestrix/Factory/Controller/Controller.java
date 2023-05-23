package phestrix.Factory.Controller;


import phestrix.Factory.Stock.CarStock;
import phestrix.ThreadPool.ThreadPool.FactoryTask;
import phestrix.ThreadPool.ThreadPool.TaskPool;

public class Controller implements Runnable {
    private final TaskPool pool;
    private final CarStock stock;

    public Controller(CarStock stock, TaskPool pool) {
        this.pool = pool;
        this.stock = stock;
    }

    private int getCountOfMissingCars() {
        return stock.getLimit() - stock.getCurrentCount();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (pool.getCountOfTasks() < getCountOfMissingCars()) {
                    pool.pushTask(new FactoryTask(getCountOfMissingCars() - pool.getCountOfTasks()));
                } else {
                    synchronized (stock) {
                        if (pool.getCountOfTasks() >= getCountOfMissingCars()) {
                            stock.wait();
                        }
                    }
                }
            } catch (Throwable ignored) {
                break;
            }
        }
    }
}
