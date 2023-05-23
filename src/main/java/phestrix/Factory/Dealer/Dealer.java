package phestrix.Factory.Dealer;

import phestrix.Factory.factory.Factory;

public class Dealer extends Thread{

    public Dealer(Factory factory, int id, int priority) {
        super(new DealerRunnable(factory, id), "Factory/Dealer");
        setPriority(priority);
    }
}
