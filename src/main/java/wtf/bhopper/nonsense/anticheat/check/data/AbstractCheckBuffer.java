package wtf.bhopper.nonsense.anticheat.check.data;

public class AbstractCheckBuffer implements ICheckData {

    float buffer;

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
