package wtf.bhopper.nonsense.anticheat.check.data;

public abstract class AbstractCheckBuffer implements ICheckData {

    private float buffer;

    @Override
    public float incrementBuffer(float amount) {
        return this.buffer += amount;
    }

    @Override
    public float decrementBuffer(float amount) {
        return this.buffer -= amount;
    }

    @Override
    public float getBuffer() {
        return this.buffer;
    }
}
