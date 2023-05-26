package phestrix.ThreadPool.ThreadPool;


import javafx.application.Platform;
import phestrix.Factory.Components.Car;
import phestrix.Factory.GUI.FXMLS.MainWindowController;
import phestrix.Factory.GUI.Util.UIBundle;
import phestrix.Factory.factory.Factory;
import phestrix.Util.Bundle;
import phestrix.Util.Timer;

import static phestrix.ThreadPool.ThreadUtil.ThreadChecker.assertThreadInterrupted;


public class UIThread {
    private static final Bundle<Boolean> DO_NOT_CLOSE = new Bundle<>(false);
    private static final long UPDATE_DELAY = 10;

    private final Thread uiThread;


    public UIThread(Factory factory, MainWindowController controller) {
            Runnable uiTask = () -> {

                while (!Thread.currentThread().isInterrupted()) {
                    if (DO_NOT_CLOSE.getValue()) {
                        synchronized (DO_NOT_CLOSE) {
                            try {
                                DO_NOT_CLOSE.wait();

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    }

                    if (!factory.isInitialized()) {
                        break;
                    }

                    try {
                        for (int i = 0; i < factory.getDealerCount(); ++i) {
                            Object[] arr = (Object[]) UIBundle.getDealerLodData(i);
                            if (arr == null) {
                                continue;
                            }

                            String time = (String) arr[0];
                            Car car = (Car) arr[1];
                            assertThreadInterrupted();
                            String str = time + " - Dealer " + i + ": Auto: " + car.getUniqueID() + "; (Body: " +
                                    car.getBody().getUniqueID() + "; Engine: " +
                                    car.getEngine().getUniqueID() + "; Accessory: " +
                                    car.getAccessory().getUniqueID() + ")";

                            Platform.runLater(() ->
                            {
                                if (Timer.isRunning())
                                    controller.printLog(str);
                            });
                        }

                        final int tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7, tmp8, tmp9;
                        final String tmp10;

                        tmp0 = UIBundle.getAccessoryDelivered();

                        assertThreadInterrupted();

                        tmp1 = factory.getAccessoryStock().getCurrentCount();

                        assertThreadInterrupted();

                        tmp2 = UIBundle.getBodyDelivered();

                        assertThreadInterrupted();

                        tmp3 = UIBundle.getEngineDelivered();

                        assertThreadInterrupted();

                        tmp4 = UIBundle.getCarMadeCount();

                        assertThreadInterrupted();

                        tmp5 = UIBundle.getCarSendCount();

                        assertThreadInterrupted();

                        tmp6 = factory.getAccessoryStock().getCurrentCount();

                        assertThreadInterrupted();

                        tmp7 = factory.getEngineStock().getCurrentCount();

                        assertThreadInterrupted();

                        tmp8 = factory.getBodyStock().getCurrentCount();

                        assertThreadInterrupted();

                        tmp9 = factory.getCarStock().getCurrentCount();

                        assertThreadInterrupted();

                        tmp10 = Math.round((UIBundle.getFactoryWorkingProducers()) / factory.getWorkerCount() * 100) + "%";

                        Platform.runLater(() ->
                        {
                            controller.setAccessoryDelivered(tmp0);
                            controller.setAccessoryStockCount(tmp1);
                            controller.setBodyworkDelivered(tmp2);
                            controller.setEngineDelivered(tmp3);
                            controller.setCarsMade(tmp4);
                            controller.setCarsSend(tmp5);
                            controller.setAccessoryStockCount(tmp6);
                            controller.setEngineStockCount(tmp7);
                            controller.setBodyStockCount(tmp8);
                            controller.setCarStockCount(tmp9);
                            controller.setFactoryLoad(tmp10);

                        });

                        Thread.sleep(UPDATE_DELAY);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            };

        uiThread = new Thread(uiTask, "UIThread");
    }

    public void waitOnRestart() {
        DO_NOT_CLOSE.setValue(true);
    }

    public void continueWork() {
        DO_NOT_CLOSE.setValue(false);
        synchronized (DO_NOT_CLOSE) {
            DO_NOT_CLOSE.notify();
        }
    }

    public void start() {
        uiThread.start();

    }

    public void interrupt() {
        uiThread.interrupt();
    }
}
