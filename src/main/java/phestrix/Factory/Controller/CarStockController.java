package phestrix.Factory.Controller;


import phestrix.Factory.Stock.CarStock;
import phestrix.ThreadPool.ThreadPool.TaskPool;

public class CarStockController extends Thread{
    public CarStockController(TaskPool pool, CarStock stock, int priority){
        super(new Controller(stock, pool), "Factory/Components/Controller");
        setPriority(priority);
    }
}
