package phestrix.ThreadPool.ThreadUtil;

public class ThreadChecker {
    public static void assertThreadInterrupted(){
        if(Thread.currentThread().isInterrupted()){
            throw new NullPointerException();
        }
    }
}
