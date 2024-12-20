package wtf.bhopper.nonsense.util.misc;

@FunctionalInterface
public interface ErrorCallback {
    void onError(Exception error);
}
