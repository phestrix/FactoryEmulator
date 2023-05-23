package phestrix.Factory.Stock;

import phestrix.Factory.Components.Body;
import phestrix.Factory.Events.EventManager;

public class BodyStock extends Stock<Body> {
    public BodyStock(EventManager manager, int limit) {
        super(manager, limit);
    }
}
