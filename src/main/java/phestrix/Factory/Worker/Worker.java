package phestrix.Factory.Worker;

import phestrix.Factory.Components.*;
import phestrix.Factory.Events.EventManager;
import phestrix.ThreadPool.ThreadPool.TaskPool;
import phestrix.Util.Bundle;
import phestrix.Factory.factory.Factory;

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
        Engine engine;
        Body body;
        Accessory accessory;
        Car car;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                synchronized (factory.getCarStock()) {
                    factory.getCarStock().notifyAll();
                }

                tasks.getTask().getCarJob();
                if (tasks.getTask().isDone()) {
                    tasks.removeTask();
                }

                engine = factory.getEngineStock().getComponent();
                assertThreadInterrupted();
                body = factory.getBodyStock().getComponent();
                assertThreadInterrupted();
                accessory = factory.getAccessoryStock().getComponent();

                factory.getEventManager().fireEvent(EventManager.PRODUCER_STARTED_DO_JOB_EVENT, null);
                if (delay.getValue() != 0) {
                    Thread.sleep(delay.getValue());
                }

                car = new Car(Product.getID(), engine, body, accessory);
                factory.getCarStock().putComponent(car);
                assertThreadInterrupted();

                factory.getEventManager().fireEvent(EventManager.PRODUCER_DID_JOB_EVENT, null);
                factory.getEventManager().fireEvent(EventManager.CAR_MADE_EVENT, car);
            } catch (Throwable ignored) {
                break;
            }
        }
    }
}
