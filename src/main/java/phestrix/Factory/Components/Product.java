package phestrix.Factory.Components;

public class Product {
    private static int Count;
    protected int uniqueID;

    public Product(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    public static synchronized int getCount() {
        return Count++;
    }

    public synchronized int getUniqueID() {
        return uniqueID;
    }

    public static synchronized void resetID() {
        Count = 0;
    }
}
