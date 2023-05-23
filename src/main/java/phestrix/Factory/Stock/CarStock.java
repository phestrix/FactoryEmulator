package phestrix.Factory.Stock;

import phestrix.Factory.Components.Car;
import phestrix.Factory.Events.EventManager;

public class CarStock extends Stock<Car> {
    public CarStock(EventManager manager, int limit) {
        super(manager, limit);
    }
}
