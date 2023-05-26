package phestrix.Factory.Supplier;

import phestrix.Factory.Components.Body;
import phestrix.Factory.factory.Factory;

public class BodySupplier extends Thread {
    public BodySupplier(Factory factory, int priority) {
        super(new Supplier<>(factory, factory.getBodyStock(), Body.class), "Factory/Supplier");
        setPriority(priority);
    }
}
