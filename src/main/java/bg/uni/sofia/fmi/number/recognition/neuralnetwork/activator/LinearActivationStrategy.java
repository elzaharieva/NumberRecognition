package bg.uni.sofia.fmi.number.recognition.neuralnetwork.activator;

import java.io.Serializable;

public class LinearActivationStrategy implements ActivationStrategy, Serializable {
    public double activate(double weightedSum) {
        return weightedSum;
    }

    public double derivative(double weightedSum) {
        return 1;
    }

    public ActivationStrategy copy() {
        return new LinearActivationStrategy();
    }
}
