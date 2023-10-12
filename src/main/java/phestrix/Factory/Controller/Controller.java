package phestrix.Factory.Controller;


import phestrix.Factory.Stock.CarStock;
import phestrix.ThreadPool.ThreadPool.FactoryTask;
import phestrix.ThreadPool.ThreadPool.TaskPool;

import java.util.concurrent.LinkedBlockingQueue;

public class Controller implements Runnable {
    private final TaskPool pool;
    private final CarStock stock;
    private final LinkedBlockingQueue<String> listOfComponents;

    public Controller(CarStock stock, TaskPool pool) {
        this.pool = pool;
        this.stock = stock;
        listOfComponents = new LinkedBlockingQueue<>();


        listOfComponents.add("Engine");
        listOfComponents.add("Body");
        listOfComponents.add("Accessory");


    }

    private int getCountOfMissingCars() {
        return stock.getLimit() - stock.getCurrentCount();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int deb1 = pool.getCountOfTasks();
                int deb2 = getCountOfMissingCars();
                int deb3 = deb2 - deb1;
                if (pool.getCountOfTasks() < getCountOfMissingCars()) {
                    pool.pushTask(new FactoryTask(getCountOfMissingCars() - pool.getCountOfTasks(), listOfComponents));
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
