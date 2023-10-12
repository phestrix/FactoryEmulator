package phestrix.ThreadPool.ThreadPool;

import java.util.concurrent.LinkedBlockingQueue;

public class FactoryTask {
    private int countOfCars;
    private final LinkedBlockingQueue<String> listOfComponents;

    public FactoryTask(int countOfCars, LinkedBlockingQueue<String> list) {
        this.countOfCars = countOfCars;
        listOfComponents = list;
    }

    public synchronized void getCarJob() {
        if (countOfCars <= 0) {
            return;
        }
        --countOfCars;
    }

    public LinkedBlockingQueue<String> getListOfComponents() {
        return listOfComponents;
    }

    public synchronized boolean isDone() {
        return countOfCars <= 0;
    }

    public synchronized int getCountOfCars() {
        return countOfCars;
    }
}
