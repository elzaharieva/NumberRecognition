package bg.uni.sofia.fmi.number.recognition.launcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bg.uni.sofia.fmi.number.recognition.neuralnetwork.Backpropagator;
import bg.uni.sofia.fmi.number.recognition.neuralnetwork.Layer;
import bg.uni.sofia.fmi.number.recognition.neuralnetwork.NeuralNetwork;
import bg.uni.sofia.fmi.number.recognition.neuralnetwork.Neuron;
import bg.uni.sofia.fmi.number.recognition.neuralnetwork.activator.LinearActivationStrategy;
import bg.uni.sofia.fmi.number.recognition.neuralnetwork.activator.SigmoidActivationStrategy;
import bg.uni.sofia.fmi.number.recognition.neuralnetwork.digit.generator.DigitTrainingDataGenerator;
import bg.uni.sofia.fmi.number.recognition.neuralnetwork.generator.DigitImageLoadingService;
import bg.uni.sofia.fmi.number.recognition.neuralnetwork.generator.TrainingData;

public class DigitRecognizingNeuralNetwork {
	private final static int IMAGE_SIZE = 784;
	private final static int HIDDEN_LAYER_NEURONS = 540;

	public static void main(String[] args) throws IOException, ClassNotFoundException {

//		DigitImageLoadingService trainingService = new DigitImageLoadingService(
//				System.getProperty("user.dir")+"\\target\\classes\\train-labels-idx1-ubyte.dat",
//				System.getProperty("user.dir")+"\\target\\classes\\train-images-idx3-ubyte.dat");
//		NeuralNetwork neuralNetwork = new NeuralNetwork("Digit Recognizing Neural Network");
//
//		Neuron inputBias = new Neuron(new LinearActivationStrategy());
//		inputBias.setOutput(1);
//
//		Layer inputLayer = new Layer(null, inputBias);
//
//		for (int i = 0; i < IMAGE_SIZE; i++) {
//			Neuron neuron = new Neuron(new SigmoidActivationStrategy());
//			neuron.setOutput(0);
//			inputLayer.addNeuron(neuron);
//		}
//
//		Neuron hiddenBias = new Neuron(new LinearActivationStrategy());
//		hiddenBias.setOutput(1);
//
//		Layer hiddenLayer = new Layer(inputLayer, hiddenBias);
//
//		long numberOfHiddenNeurons = HIDDEN_LAYER_NEURONS;
//
//		for (int i = 0; i < numberOfHiddenNeurons; i++) {
//			Neuron neuron = new Neuron(new SigmoidActivationStrategy());
//			neuron.setOutput(0);
//			hiddenLayer.addNeuron(neuron);
//		}
//
//		Layer outputLayer = new Layer(hiddenLayer);
//
//		// 10 output neurons - 1 for each digit
//		for (int i = 0; i < 10; i++) {
//			Neuron neuron = new Neuron(new SigmoidActivationStrategy());
//			neuron.setOutput(0);
//			outputLayer.addNeuron(neuron);
//		}
//
//		neuralNetwork.addLayer(inputLayer);
//		neuralNetwork.addLayer(hiddenLayer);
//		neuralNetwork.addLayer(outputLayer);
//
//		DigitTrainingDataGenerator trainingDataGenerator = new DigitTrainingDataGenerator(
//				trainingService.loadDigitImages());
//		Backpropagator backpropagator = new Backpropagator(neuralNetwork, 0.1, 0.9, 0);
//		backpropagator.train(trainingDataGenerator, 0.005);
//		neuralNetwork.persist();
		
		System.out.println(test());

	}
	
	private static double test() throws ClassNotFoundException, IOException{
		NeuralNetwork neuralNetwork = deserializeNeuralNetwork();
		//testing
		double[][] inputs = getInputs(System.getProperty("user.dir")+"\\target\\classes\\test.csv");
		double[][] outputs = getExpectedOutputs(System.getProperty("user.dir")+"\\target\\classes\\test.csv");
		TrainingData testData = new TrainingData(inputs, outputs);
		int known=0;
		for (int i = 0; i < testData.getInputs().length; i++) {
			double[] input = testData.getInputs()[i];
			double[] output = testData.getOutputs()[i];

			int digit = 0;
			boolean found = false;
			while (digit < 10 && !found) {
				found = (output[digit] == 1);
				digit++;
			}

			neuralNetwork.setInputs(input);
			double[] receivedOutput = neuralNetwork.getOutput();

			double max = receivedOutput[0];
			double recognizedDigit = 0;
			for (int j = 0; j < receivedOutput.length; j++) {
				if (receivedOutput[j] > max) {
					max = receivedOutput[j];
					recognizedDigit = j;
				}
			}
			if((digit-1)==recognizedDigit){
				known++;
			}
			System.out.println(
					"Recognized " + (digit - 1) + " as " + recognizedDigit + ". Corresponding output value was " + max);
		}
		return ((double)known/(double)testData.getInputs().length)*100;
	}
	
	public static double[][] getExpectedOutputs(String fileName) {
		List<Double> results = new ArrayList<>();
		Stream<String> lines = null;
		try {
			lines = Files.lines(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		lines.forEach(l -> {
			results.add(Double.valueOf(l.split(",")[0]));
		});

		double[][] expextedOutputs = new double[results.size()][10];
		for (int i = 0; i < results.size(); i++) {
			double[] arr = new double[10];
			Arrays.fill(arr, 0);
			arr[results.get(i).intValue()] = 1;
			expextedOutputs[i] = arr;
		}
		return expextedOutputs;
	}

	public static double[][] getInputs(String fileName) {
		Stream<String> lines = null;
		try {
			lines = Files.lines(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int counter = 0;
		List<String> info = lines.collect(Collectors.toList());
		double[][] output = new double[info.size()][784];
		for (String l : info) {
			String[] row = l.split(",");
			double[] fileRow = new double[784];
			for (int i = 1; i < row.length; i++) {
				double temp = Double.valueOf(row[i]);
				fileRow[i - 1] = temp;
			}
			output[counter] = fileRow;
			counter++;
		}
		return output;
	}
	private static NeuralNetwork deserializeNeuralNetwork() throws ClassNotFoundException, IOException{
		FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir")+"\\target\\classes\\neuralNetwork.net");
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
		NeuralNetwork neuralNetwork = (NeuralNetwork) objectInputStream.readObject();
		objectInputStream.close();
		return neuralNetwork;
	}
}
