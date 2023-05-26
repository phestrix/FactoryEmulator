package phestrix.Factory.Supplier;

import phestrix.Factory.Components.Engine;
import phestrix.Factory.factory.Factory;

public class EngineSupplier extends Thread {
    public EngineSupplier(Factory factory, int priority) {
        super(new Supplier<>(factory, factory.getEngineStock(), Engine.class), "Factory/Supplier");
        setPriority(priority);
    }
}
