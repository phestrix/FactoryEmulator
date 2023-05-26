package phestrix.Factory.Supplier;

import phestrix.Factory.Components.Accessory;
import phestrix.Factory.factory.Factory;

public class AccessorySupplier extends Thread {
    public AccessorySupplier(Factory factory, int priority) {
        super(new Supplier<>(factory, factory.getAccessoryStock(), Accessory.class), "Factory/Supplier");
        setPriority(priority);
    }
}
