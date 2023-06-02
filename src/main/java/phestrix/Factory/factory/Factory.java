package phestrix.Factory.factory;

import phestrix.Factory.Controller.CarStockController;
import phestrix.Factory.Dealer.Dealer;
import phestrix.Factory.Events.EventHandler;
import phestrix.Factory.Events.EventManager;
import phestrix.Factory.Stock.AccessoryStock;
import phestrix.Factory.Stock.BodyStock;
import phestrix.Factory.Stock.CarStock;
import phestrix.Factory.Stock.EngineStock;
import phestrix.Factory.Supplier.AccessorySupplier;
import phestrix.Factory.Supplier.BodySupplier;
import phestrix.Factory.Supplier.EngineSupplier;
import phestrix.ThreadPool.ThreadPool.WorkerSectionPool;
import phestrix.Util.Bundle;
import phestrix.Util.Log;
import phestrix.Util.Timer;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Factory {
    private boolean initialized = false;
    private EventManager eventManager;
    private CarStock carStock;
    private AccessoryStock accessoryStock;
    private BodyStock bodyStock;
    private EngineStock engineStock;

    private final int accessorySupplierCount;
    private final int workerCount;
    private final int dealerCount;
    private final Bundle<Integer> supplierDelay;
    private final Bundle<Integer> workerDelay;
    private final Bundle<Integer> dealerDelay;
    private final Bundle<Boolean> loggingEnabled;
    private final Bundle<Boolean> printName;
    private final Bundle<String> name;
    private final int accessoryStockLimit;
    private final int engineStockLimit;
    private final int bodyStockLimit;
    private final int carStockLimit;
    private WorkerSectionPool sectionPool;
    private CarStockController carStockController;
    private ArrayList<Thread> threadPool;
    private int threadPriority;

    public Factory(int accessoryStockLimit, int accessorySupplierCount, int workerCount, int dealerCount,
                   int supplierDelay, int workerDelay, int dealerDelay, int engineStockLimit, int bodyStockLimit,
                   int carStockLimit, boolean loggingEnabled, String name) {
        eventManager = new EventManager();

        if (supplierDelay == -1) {
            supplierDelay = (int) (randomDelay() * 1000);
        }
        if (dealerDelay == -1) {
            dealerDelay = (int) (randomDelay() * 1000);
        }
        if (workerDelay == -1) {
            workerDelay = (int) (randomDelay() * 1000);
        }

        this.accessoryStockLimit = accessoryStockLimit;
        this.accessorySupplierCount = accessorySupplierCount;
        this.workerCount = workerCount;
        this.dealerCount = dealerCount;
        this.engineStockLimit = engineStockLimit;
        this.bodyStockLimit = bodyStockLimit;
        this.carStockLimit = carStockLimit;
        this.name = new Bundle<>(Objects.requireNonNullElseGet(name, () -> "factory.Factory-" + this.hashCode()));
        this.printName = new Bundle<>(true);
        this.supplierDelay = new Bundle<>(supplierDelay);
        this.workerDelay = new Bundle<>(workerDelay);
        this.dealerDelay = new Bundle<>(dealerDelay);
        this.loggingEnabled = new Bundle<>(loggingEnabled);


    }

    public static double randomDelay() {
        return 2 * ThreadLocalRandom.current().nextDouble();
    }


    public synchronized boolean isInitialized() {
        return initialized;
    }

    public synchronized void setFactoryName(String set) {
        name.setValue(set);
    }

    public synchronized Bundle<Boolean> canPrintName() {
        return printName;
    }

    public synchronized void setPrintFactoryNameToLog(boolean set) {
        printName.setValue(set);
    }

    public synchronized EventManager getEventManager() {
        return eventManager;
    }

    public synchronized CarStock getCarStock() {
        return carStock;
    }

    public synchronized AccessoryStock getAccessoryStock() {
        return accessoryStock;
    }

    public synchronized BodyStock getBodyStock() {
        return bodyStock;
    }

    public synchronized EngineStock getEngineStock() {
        return engineStock;
    }

    public synchronized int getAccessorySupplierCount() {
        return accessorySupplierCount;
    }

    public synchronized int getWorkerCount() {
        return workerCount;
    }

    public synchronized int getDealerCount() {
        return dealerCount;
    }

    public synchronized Bundle<Integer> getSupplierDelay() {
        return supplierDelay;
    }

    public synchronized Bundle<Integer> getWorkerDelay() {
        return workerDelay;
    }

    public synchronized Bundle<Integer> getDealerDelay() {
        return dealerDelay;
    }

    public synchronized Bundle<String> getName() {
        return name;
    }

    public synchronized void enableLogging() throws Exception {
        loggingEnabled.setValue(true);
        Log.init();
        Log.enableInfoLogging();
    }

    public synchronized Bundle<Boolean> isLoggingEnabled() {
        if (loggingEnabled.getValue() && !Log.isLoggingEnabled()) {
            try {
                enableLogging();
            } catch (Throwable ignored) {
                loggingEnabled.setValue(false);
            }
        }
        return loggingEnabled;
    }

    private void startThreads() {
        carStockController.start();
        sectionPool.start();
        for (var thread : threadPool)
            thread.start();
    }

    private void stopThreads() {
        sectionPool.destroy();
        carStockController.interrupt();
        for (var thread : threadPool)
            thread.interrupt();
    }

    public void init(int threadPriority) throws Exception {
        if (initialized) {
            return;
        }
        carStock = new CarStock(eventManager, carStockLimit);
        accessoryStock = new AccessoryStock(eventManager, accessoryStockLimit);
        bodyStock = new BodyStock(eventManager, bodyStockLimit);
        engineStock = new EngineStock(eventManager, engineStockLimit);
        sectionPool = new WorkerSectionPool(this, threadPriority);
        carStockController = new CarStockController(sectionPool.getPool(), carStock, threadPriority);
        threadPool = new ArrayList<>();
        for (int i = 0; i < accessorySupplierCount; ++i) {
            threadPool.add(new AccessorySupplier(this, threadPriority));
        }
        threadPool.add(new EngineSupplier(this, threadPriority));
        threadPool.add(new BodySupplier(this, threadPriority));

        for (int i = 0; i < dealerCount; ++i) {
            threadPool.add(new Dealer(this, i, threadPriority));
        }

        this.threadPriority = threadPriority;
        initialized = true;
        if (loggingEnabled.getValue()) {
            enableLogging();
        }
        eventManager.enablePerformingEvents();
        Timer.start();
        startThreads();
    }

    public synchronized void deinit(boolean destoyEventListeners) {
        if (!initialized) {
            return;
        }

        initialized = false;

        carStock = null;
        accessoryStock = null;
        bodyStock = null;
        engineStock = null;
        eventManager.disablePerformingEvents();

        stopThreads();
        threadPool.clear();
        carStockController = null;
        sectionPool = null;
        threadPool = null;
        if (destoyEventListeners) {
            eventManager = null;
        }
        System.gc();
        Timer.stop();
    }

    public synchronized void restart(int threadPriority, Runnable worker) throws Exception {
        if (!initialized) {
            return;
        }
        deinit(false);
        if (threadPriority != -1) {
            this.threadPriority = threadPriority;
        }
        if (worker != null) {
            worker.run();
        }
        init(this.threadPriority);
    }

    public synchronized void setDealerDelay(int dealerDelay){
        if(dealerDelay == -1){
            this.dealerDelay.setValue((int)randomDelay() * 1000);
        }
    }

    public synchronized void setWorkerDelay(int workerDelay){
        if(workerDelay == -1){
            this.dealerDelay.setValue((int)randomDelay() * 1000);
        }
    }

    public synchronized void setSupplierDelay(int supplierDelay){
        if(supplierDelay == -1){
            this.dealerDelay.setValue((int)randomDelay() * 1000);
        }
    }

    public synchronized void setOnEngineDelivered(EventHandler handler)
    {
        if(handler == null)
            return;
        eventManager.setEventHandlerHashMap(EventManager.ENGINE_DELIVERED_EVENT, handler);
    }

    public synchronized void setOnAccessoryDelivered(EventHandler handler)
    {
        if(handler == null)
            return;
        eventManager.setEventHandlerHashMap(EventManager.ACCESSORY_DELIVERED_EVENT, handler);
    }

    public synchronized void setOnBodyworkDelivered(EventHandler handler)
    {
        if(handler == null)
            return;
        eventManager.setEventHandlerHashMap(EventManager.BODY_DELIVERED_EVENT, handler);
    }

    public synchronized void setOnCarMade(EventHandler handler)
    {
        if(handler == null)
            return;
        eventManager.setEventHandlerHashMap(EventManager.CAR_MADE_EVENT, handler);
    }

    public synchronized void setOnCarSend(EventHandler handler)
    {
        if(handler == null)
            return;
        eventManager.setEventHandlerHashMap(EventManager.CAR_SEND_EVENT, handler);
    }

    public synchronized void setOnWorkerStartJob(EventHandler handler)
    {
        if(handler == null)
            return;
        eventManager.setEventHandlerHashMap(EventManager.WORKER_STARTED_LOB_EVENT, handler);
    }

    public synchronized void setOnWorkerDidJob(EventHandler handler)
    {
        if(handler == null)
            return;
        eventManager.setEventHandlerHashMap(EventManager.WORKERS_JOB_DONE, handler);
    }

    public synchronized void disableLogging(){
        loggingEnabled.setValue(false);
    }
}
