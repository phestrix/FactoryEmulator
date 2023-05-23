package phestrix.Factory.Stock;

import phestrix.Factory.Components.Accessory;
import phestrix.Factory.Events.EventManager;

public class AccessoryStock extends Stock<Accessory> {
    public AccessoryStock(EventManager manager, int limit) {
        super(manager, limit);
    }
}
