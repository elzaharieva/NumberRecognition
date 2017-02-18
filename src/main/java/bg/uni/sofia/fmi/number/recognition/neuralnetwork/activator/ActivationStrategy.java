package bg.uni.sofia.fmi.number.recognition.neuralnetwork.activator;

public interface ActivationStrategy {
    double activate(double weightedSum);
    double derivative(double weightedSum);
    ActivationStrategy copy();
}
