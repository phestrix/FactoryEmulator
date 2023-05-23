package phestrix.Factory.Dealer;

import phestrix.Factory.Components.Car;
import phestrix.Factory.Events.EventManager;
import phestrix.Factory.Stock.CarStock;
import phestrix.Util.Bundle;
import phestrix.Util.Log;
import phestrix.Util.Timer;
import phestrix.Factory.factory.Factory;

import static phestrix.ThreadPool.ThreadUtil.ThreadChecker.assertThreadInterrupted;

public class DealerRunnable implements Runnable {
    private final int ID;
    private final Bundle<Integer> delay;
    private final Bundle<Boolean> isLoggingEnabled;
    private final Bundle<Boolean> canPrintName;
    private final Bundle<String> factoryName;

    private final CarStock carStock;
    private final EventManager eventManager;

    public DealerRunnable(Factory factory, int id) {
        ID = id;
        delay = factory.getDealerDelay();
        isLoggingEnabled = factory.isLoggingEnabled();
        canPrintName = factory.canPrintName();
        factoryName = factory.getName();
        carStock = factory.getCarStock();
        eventManager = factory.getEventManager();

    }

    @Override
    public void run() {
        String time;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (delay.getValue() != 0)
                    Thread.sleep(delay.getValue());
                Car car = carStock.getComponent();

                assertThreadInterrupted();

                time = Timer.getTime(Timer.ALL_PARAMETERS);

                eventManager.fireEvent(EventManager.CAR_SEND_EVENT, new Object[]{time, ID, car});

                if (isLoggingEnabled.getValue())
                    Log.info(time + (canPrintName.getValue() ? " [" + factoryName.getValue() + "]" : "") + " - Worker "
                            + ID + ": Auto: " + car.getUniqueID() + "; (Body: " +
                            car.getBody().getUniqueID() + "; Engine: " + car.getEngine().getUniqueID() + "; Accessory: "
                            + car.getAccessory().getUniqueID() + ")");
            } catch (Throwable ignored) {
                break;
            }
        }
    }
}
