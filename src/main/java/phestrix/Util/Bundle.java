package phestrix.Util;

public class Bundle<T> {
    private T value;

    public Bundle(T val) {
        value = val;
    }

    public synchronized T getValue() {
        return value;
    }

    public synchronized void setValue(T value) {
        this.value = value;
    }
}
