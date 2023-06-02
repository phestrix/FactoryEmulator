package phestrix.Factory.Supplier;

import phestrix.Factory.Components.Product;
import phestrix.Factory.Stock.Stock;
import phestrix.Util.Bundle;
import phestrix.Factory.factory.Factory;

import static phestrix.ThreadPool.ThreadUtil.ThreadChecker.assertThreadInterrupted;

public class Supplier<T extends Product> implements Runnable {
    private final Stock<T> stock;

    private final Class<T> type;

    private final Bundle<Integer> delay;

    public Supplier(Factory factory, Stock<T> stock, Class<T> typeClass) {
        this.stock = stock;
        type = typeClass;
        delay = factory.getSupplierDelay();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (delay.getValue() != 0) {
                    Thread.sleep(delay.getValue());
                    stock.putComponent(type.getConstructor(Integer.TYPE).newInstance(Product.getCount()));
                    assertThreadInterrupted();
                }
            } catch (Throwable ignored) {
                break;
            }
        }
    }
}
