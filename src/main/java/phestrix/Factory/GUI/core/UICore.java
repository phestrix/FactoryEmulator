package phestrix.Factory.GUI.core;

import phestrix.Factory.Components.Car;
import phestrix.Factory.Components.Product;
import phestrix.Factory.GUI.FXMLS.MainWindowController;
import phestrix.Factory.GUI.Util.Dialogs;
import phestrix.Factory.GUI.Util.UIBundle;
import phestrix.ThreadPool.ThreadPool.UIThread;
import phestrix.Util.Timer;
import phestrix.Factory.factory.Factory;
import javafx.application.Platform;
import lombok.Getter;

@Getter
public class UICore {
    private static Factory factory = null;
    private static MainWindowController controller = null;
    private static UIThread uiThread;

    public static synchronized Factory getFactory()
    {
        return factory;
    }

    public static synchronized void initCore(MainWindowController controller)
    {
        UICore.controller = controller;
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    }

    public static synchronized void enableFactoryProcess(Factory factory)
    {
        if(UICore.factory != null && UICore.factory.isInitialized())
            UICore.factory.deinit(true);

        UICore.factory = factory;
        try
        {
            UICore.factory.init(Thread.MIN_PRIORITY);
        } catch(Exception e)
        {
            Dialogs.showExceptionDialog(controller.getRootStage(), e, "Can't initialize logging!");
        }
        setEventHandlers();

        UIBundle.resetAll();
        Product.resetID();
        UIBundle.initDealerLogDataBundle(factory.getDealerCount());
        uiThread = new UIThread(factory, controller);
        uiThread.start();

        setTimerEventHandler();

        //controller.clearFields();
        controller.setDealerCount(UICore.factory.getDealerCount());
        controller.setWorkerCount(UICore.factory.getWorkerCount());
        controller.setSupplierCount(UICore.factory.getAccessorySupplierCount());
        controller.setStatus(MainWindowController.FactoryStatus.RUNNING);
        controller.setPDSliderVal(UICore.factory.getWorkerDelay().getValue());
        controller.setSDSliderVal(UICore.factory.getSupplierDelay().getValue());
        controller.setDDSliderVal(UICore.factory.getDealerDelay().getValue());
        controller.setCBLogging(UICore.factory.isLoggingEnabled().getValue());

        controller.resetLog();
        controller.enableAll();
    }

    public static synchronized void enableFactoryProcess(int accessorySupplierCount, int producerCount, int dealerCount, int supplierDelay, int producerDelay, int dealerDelay,
                                                         int accessoryStoreLimit, int engineStoreLimit, int bodyworkStoreLimit, int carStoreLimit, boolean loggingEnabled)
    {
        enableFactoryProcess(new Factory(accessorySupplierCount, producerCount, dealerCount, supplierDelay, producerDelay, dealerDelay,
                accessoryStoreLimit, engineStoreLimit, bodyworkStoreLimit, carStoreLimit, loggingEnabled, "Factory Application-" + ProcessHandle.current().pid()));
    }

    public static synchronized void restartFactory()
    {
        if(factory == null || !factory.isInitialized())
            return;

        uiThread.waitOnRestart();

        try
        {
            factory.restart(-1, () -> {
                UIBundle.resetAll();
                Product.resetID();
                setTimerEventHandler();
            });
        } catch(Exception e)
        {
            Dialogs.showExceptionDialog(controller.getRootStage(), e, "Can't initialize logging!");
        }

        uiThread.continueWork();

        //controller.clearFields();
        controller.setCBLogging(false);
        controller.setSDSliderVal(factory.getSupplierDelay().getValue());
        controller.setPDSliderVal(factory.getWorkerDelay().getValue());
        controller.setDDSliderVal(factory.getDealerDelay().getValue());
        controller.enableAll();
        controller.resetLog();
    }

    private static void setTimerEventHandler()
    {
        Timer.subscribeEvent(() ->
        {
            if(!Timer.isRunning())
                return;
            String time = Timer.getTime(Timer.HOURS | Timer.MINUTES | Timer.SECONDS);
            Platform.runLater(() ->
                    controller.setWorkingTime(time)
            );
        }, 1000);
    }

    public static synchronized boolean factoryIsDisabled()
    {
        return factory == null;
    }

    public static synchronized void disableFactory()
    {
        if(factory != null)
        {
            factory.deinit(true);
            factory = null;
        }
        if(uiThread != null)
            uiThread.interrupt();
    }

    private static synchronized void setEventHandlers()
    {
        factory.setOnAccessoryDelivered((o) -> UIBundle.incAccessoryDelivered());
        factory.setOnBodyworkDelivered((o) -> UIBundle.incBodyDelivered());
        factory.setOnEngineDelivered((o) -> UIBundle.incEngineDelivered());
        factory.setOnCarMade((o) -> UIBundle.incCarMadeCount());
        factory.setOnWorkerStartJob((o) -> UIBundle.incFactoryWorkingProducers());
        factory.setOnWorkerDidJob((o) -> UIBundle.decFactoryWorkingProducers());

        factory.setOnCarSend((o) ->
        {
            Object[] array = (Object[]) o;
            if(array[0] == null || array[1] == null || array[2] == null)
                return;
            if(((Car) array[2]).getBody() == null || ((Car) array[2]).getEngine() == null || ((Car) array[2]).getAccessory() == null)
                return;

            UIBundle.incCarSendCount();
            UIBundle.addLogData((Integer) array[1], array[0], array[2]);
        });
    }
}
