package phestrix.Factory.Stock;

import phestrix.Factory.Components.Engine;
import phestrix.Factory.Events.EventManager;

public class EngineStock extends Stock<Engine> {
    public EngineStock(EventManager manager, int limit) {
        super(manager, limit);
    }
}
