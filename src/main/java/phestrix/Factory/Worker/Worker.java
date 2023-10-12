package phestrix.Factory.Worker;

import phestrix.Factory.Components.*;
import phestrix.Factory.Events.EventManager;
import phestrix.ThreadPool.ThreadPool.TaskPool;
import phestrix.Util.Bundle;
import phestrix.Factory.factory.Factory;

import java.util.ArrayList;

import static phestrix.ThreadPool.ThreadUtil.ThreadChecker.assertThreadInterrupted;

public class Worker implements Runnable {
    private final TaskPool tasks;
    private final Factory factory;
    private final Bundle<Integer> delay;

    public Worker(TaskPool pool, Factory factory) {
        tasks = pool;
        this.factory = factory;
        delay = factory.getWorkerDelay();
    }

    @Override
    public void run() {
        ArrayList<Product> components = new ArrayList<>();
        Car car;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (var product : tasks.getTask().getListOfComponents()) {
                    switch (product) {
                        case "Engine" -> components.add(0, factory.getEngineStock().getComponent());
                        case "Body" -> components.add(1, factory.getBodyStock().getComponent());
                        case "Accessory" -> components.add(2, factory.getAccessoryStock().getComponent());
                    }
                }

                factory.getEventManager().fireEvent(EventManager.WORKER_STARTED_LOB_EVENT, null);
                if (delay.getValue() != 0) {
                    Thread.sleep(delay.getValue());
                }

                car = new Car(Product.getCount(), (Engine) components.get(0), (Body) components.get(1), (Accessory) components.get(2));

                tasks.getTask().getCarJob();
                if (tasks.getTask().isDone()) {
                    tasks.removeTask();
                }

                factory.getCarStock().putComponent(car);
                synchronized (factory.getCarStock()) {
                    factory.getCarStock().notifyAll();
                }
                assertThreadInterrupted();

                factory.getEventManager().fireEvent(EventManager.WORKERS_JOB_DONE, null);
                factory.getEventManager().fireEvent(EventManager.CAR_MADE_EVENT, car);
            } catch (Throwable ignored) {
                break;
            }
        }
    }
}
