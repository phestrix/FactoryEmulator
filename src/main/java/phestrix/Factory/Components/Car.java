package phestrix.Factory.Components;

public class Car extends Product {
    private final Engine engine;
    private final Body body;
    private final Accessory accessory;

    public Car(int uniqueID, Engine engine, Body body, Accessory accessory) {
        super(uniqueID);
        this.engine = engine;
        this.body = body;
        this.accessory = accessory;
    }

    public Engine getEngine() {
        return engine;
    }

    public Body getBody() {
        return body;
    }

    public Accessory getAccessory() {
        return accessory;
    }
}
