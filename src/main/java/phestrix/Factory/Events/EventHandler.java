package phestrix.Factory.Events;

@FunctionalInterface
public interface EventHandler {
    void perform(Object event);
}
