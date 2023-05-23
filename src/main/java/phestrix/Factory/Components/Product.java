package phestrix.Factory.Components;

public class Product {
    private static int ID;
    protected int uniqueID;

    public Product(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    public static synchronized int getID() {
        return ID++;
    }

    public synchronized int getUniqueID() {
        return uniqueID;
    }

    public static synchronized void resetID() {
        ID = 0;
    }
}
