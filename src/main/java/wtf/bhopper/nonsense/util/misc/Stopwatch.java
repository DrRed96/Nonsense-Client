package wtf.bhopper.nonsense.util.misc;

public class Stopwatch {

    private long lastMS = System.currentTimeMillis();

    public void reset() {
        this.lastMS = System.currentTimeMillis();
    }

    public boolean hasReached(double delay) {
        return System.currentTimeMillis() - this.lastMS >= delay;
    }

    public boolean hasReached(long delay) {
        return System.currentTimeMillis() - this.lastMS >= delay;
    }

    public long passedTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public long getTime() {
        return this.lastMS;
    }

    public void setTime(long timeMS) {
        this.lastMS = timeMS;
    }

}
