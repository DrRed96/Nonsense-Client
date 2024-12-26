package wtf.bhopper.nonsense.anticheat.check.data;

public interface ICheckData {

    default float incrementBuffer() {
        return incrementBuffer(1.0F);
    }

    float incrementBuffer(float amount);

    float decrementBuffer(float amount);

    float getBuffer();

}
