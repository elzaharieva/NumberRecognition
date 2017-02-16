package bg.uni.sofia.fmi.number.recognition.launcher;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import bg.uni.sofia.fmi.number.recognition.entry.ImageEntry;
import bg.uni.sofia.fmi.number.recognition.utils.FileUtils;
import bg.uni.sofia.fmi.number.recognition.utils.ImageUtils;
import net.vivin.neural.NeuralNetwork;

public class Launcher {
	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		File file = new File("C:\\Users\\Kaloyan Spiridonov\\Desktop\\number.jpg");
		BufferedImage img = ImageIO.read(file);
		List<BufferedImage> digits = ImageUtils.splitToDigits(img);

		for (int i = 0; i < digits.size(); i++) {
			BufferedImage newImage = ImageUtils.prepareDigit(digits.get(i));
			newImage = blackWhiteSwap(newImage);
//			System.out.println("------------------");
//			System.out.println(Arrays.toString(imageToArray(newImage)));
//			System.out.println("------------------");
			
			FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Kaloyan Spiridonov\\Desktop\\neuralNetwork.net");
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
			NeuralNetwork neuralNetwork;
			neuralNetwork = (NeuralNetwork) objectInputStream.readObject();
			objectInputStream.close();
			
			neuralNetwork.setInputs(imageToArray(newImage));
            double[] receivedOutput = neuralNetwork.getOutput();

            double max = receivedOutput[0];
            double recognizedDigit = 0;
            for(int j = 0; j < receivedOutput.length; j++) {
                if(receivedOutput[j] > max) {
                    max = receivedOutput[j];
                    recognizedDigit = j;
                }
            }

            System.out.println("Recognized: " +  recognizedDigit + ". Corresponding output value was " + max);

			ImageUtils.save(newImage, "jpg", new File("C:\\Users\\Kaloyan Spiridonov\\Desktop\\digit" + i + ".jpg"));
		}

	}

	private static BufferedImage blackWhiteSwap(BufferedImage image) {
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				if (image.getRGB(i, j) == Color.BLACK.getRGB()) {
					image.setRGB(i, j, Color.white.getRGB());
				} else {
					image.setRGB(i, j, Color.BLACK.getRGB());
				}
			}
		}
		return image;
	}

	private static double[] imageToArray(BufferedImage image) {
		double[] arrayImage = new double[784];
		int k = 0;
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				double color = image.getRGB(j, i) & 0xFF;
				if (color > 127) {
					arrayImage[k] = 1;
				} else {
					arrayImage[k] = 0;
				}
				k++;
			}
		}
		return arrayImage;
	}

	// public static void main(String[] args) {
	// Path trainingFile = Paths.get("resources", "train.csv");
	// List<String> lines = FileUtils.readFile(trainingFile);
	// List<ImageEntry> images = new ArrayList<ImageEntry>();
	// lines.forEach(line -> {
	// ImageEntry image = toImageEntry(line);
	// images.add(image);
	// });
	// System.out.println(images);
	// }
	//
	// private static ImageEntry toImageEntry(String line) {
	// String[] elements = line.split(",");
	// ImageEntry image = new ImageEntry();
	// for (int i = 1; i < elements.length; i++) {
	// image.add(Integer.parseInt(elements[i]));
	// }
	// return image;
	// }
}
