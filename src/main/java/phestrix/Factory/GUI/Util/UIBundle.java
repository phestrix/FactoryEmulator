package phestrix.Factory.GUI.Util;


import java.util.Arrays;

public class UIBundle {
    public static final int MAX_LOG_SIZE = 26;

    private static Object[] logStack;
    private static double factoryWorkingProducers;
    private static int carMadeCount;
    private static int carSendCount;

    private static int engineDelivered;
    private static int bodyDelivered;
    private static int accessoryDelivered;

    public static synchronized void initDealerLogDataBundle(int dealerSize)
    {
        logStack = new Object[dealerSize];
    }

    public static synchronized void addLogData(int ID, Object... arr)
    {
        if(logStack.length == MAX_LOG_SIZE)
            return;

        logStack[ID] = arr;
    }

    public static synchronized void incFactoryWorkingProducers()
    {
        factoryWorkingProducers++;
    }

    public static synchronized void decFactoryWorkingProducers()
    {
        factoryWorkingProducers--;
    }

    public static synchronized void incCarMadeCount()
    {
        carMadeCount++;
    }

    public static synchronized void incCarSendCount()
    {

        carSendCount++;

    }

    public static synchronized void incEngineDelivered()
    {
        engineDelivered++;
    }

    public static synchronized void incBodyDelivered()
    {
        bodyDelivered++;
    }

    public static synchronized void incAccessoryDelivered()
    {
        accessoryDelivered++;
    }

    public static synchronized double getFactoryWorkingProducers()
    {
        return factoryWorkingProducers;
    }

    public static synchronized int getCarMadeCount()
    {
        return carMadeCount;
    }

    public static synchronized int getCarSendCount()
    {
        return carSendCount;
    }

    public static synchronized int getEngineDelivered()
    {
        return engineDelivered;
    }

    public static synchronized int getBodyDelivered()
    {
        return bodyDelivered;
    }

    public static synchronized int getAccessoryDelivered()
    {
        return accessoryDelivered;
    }

    public static synchronized Object getDealerLodData(int ID)
    {
        Object obj = logStack[ID];
        logStack[ID] = null;
        return obj;
    }

    public static synchronized void resetAll()
    {
        factoryWorkingProducers = 0;
        carSendCount = 0;
        carMadeCount = 0;
        accessoryDelivered = 0;
        bodyDelivered = 0;
        engineDelivered = 0;
        if(logStack != null)
            Arrays.fill(logStack, null);
    }
}
