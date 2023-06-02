package phestrix.Factory.Stock;

import phestrix.Factory.Components.Accessory;
import phestrix.Factory.Components.Body;
import phestrix.Factory.Components.Car;
import phestrix.Factory.Components.Engine;
import phestrix.Factory.Events.EventManager;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


public class Stock<T> {
    private final Queue<T> components;
    private final EventManager eventManager;

    private final int limit;

    public Stock(EventManager manager, int limit) {
        components = new ArrayBlockingQueue<>(limit);
        eventManager = manager;
        this.limit = limit;

    }

    private synchronized boolean isFull() {
        return components.size() == limit;
    }

    public synchronized int getLimit() {
        return limit;
    }

    public synchronized int getCurrentCount() {
        return components.size();
    }

    public synchronized void putComponent(T object) {
        while (isFull()) {
            try {
                this.wait();
            } catch (Throwable ignored) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        if (object instanceof Accessory)
            eventManager.fireEvent(EventManager.ACCESSORY_DELIVERED_EVENT, null);
        else if (object instanceof Engine)
            eventManager.fireEvent(EventManager.ENGINE_DELIVERED_EVENT, null);
        else if (object instanceof Body)
            eventManager.fireEvent(EventManager.BODY_DELIVERED_EVENT, null);
        else if (object instanceof Car)
            eventManager.fireEvent(EventManager.CAR_SUPPLIED_TO_STORE_EVENT, object);

        components.offer(object);
        this.notify();
    }

    public synchronized T getComponent() {
        while (components.size() == 0)
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }

        T object = components.poll();
        eventManager.fireEvent(EventManager.COMPONENT_SEND_FROM_STORE, object);
        this.notifyAll();

        return object;
    }
}
