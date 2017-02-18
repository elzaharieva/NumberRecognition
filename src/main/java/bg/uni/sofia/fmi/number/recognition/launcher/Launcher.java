package bg.uni.sofia.fmi.number.recognition.launcher;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import bg.uni.sofia.fmi.number.recognition.neuralnetwork.NeuralNetwork;
import bg.uni.sofia.fmi.number.recognition.utils.ImageUtils;

public class Launcher {
	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	private final static String PATH_TO_FILE = "C:\\Users\\Elena\\Desktop";
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		NeuralNetwork neuralNetwork = deserializeNeuralNetwork();
		
		File file = new File(PATH_TO_FILE + "\\number.jpg");
		BufferedImage img = ImageIO.read(file);
		List<BufferedImage> digits = ImageUtils.splitToDigits(img);
		for (int i = 0; i < digits.size(); i++) {
			BufferedImage newImage = ImageUtils.prepareDigit(digits.get(i));
			neuralNetwork.setInputs(otsu(imageToArray(newImage)));
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

			ImageUtils.save(newImage, "jpg", new File(PATH_TO_FILE + "\\digit" + i + ".jpg"));
		}

	}

	private static double[] imageToArray(BufferedImage image) {
		double[] arrayImage = new double[784];
		int k = 0;
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				double color = image.getRGB(j, i)&0xFF;
				arrayImage[k]=color;
				k++;
			}
		}
		return arrayImage;
	}

	private static NeuralNetwork deserializeNeuralNetwork() throws ClassNotFoundException, IOException{
		FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir")+"\\target\\classes\\neuralNetwork.net");
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
		NeuralNetwork neuralNetwork = (NeuralNetwork) objectInputStream.readObject();
		objectInputStream.close();
		return neuralNetwork;
	}
	
    //Uses Otsu's Threshold algorithm to convert from grayscale to black and white
    private static double[] otsu(double[] data) {
        int[] histogram = new int[256];

        for(double datum : data) {
            histogram[(int) datum]++;
        }

        double sum = 0;
        for(int i = 0; i < histogram.length; i++) {
            sum += i * histogram[i];
        }

        double sumB = 0;
        int wB = 0;
        int wF = 0;

        double maxVariance = 0;
        int threshold = 0;

        int i = 0;
        boolean found = false;

        while(i < histogram.length && !found) {
            wB += histogram[i];

            if(wB != 0) {
                wF = data.length - wB;

                if(wF != 0) {
                    sumB += (i * histogram[i]);

                    double mB = sumB / wB;
                    double mF = (sum - sumB) / wF;

                    double varianceBetween = wB * Math.pow((mB - mF), 2);

                    if(varianceBetween > maxVariance) {
                        maxVariance = varianceBetween;
                        threshold = i;
                    }
                }

                else {
                    found = true;
                }
            }

            i++;
        }

/*        System.out.println(label + ": threshold is " + threshold);

        for(i = 0; i < data.length; i++) {
            if(i % 28 == 0) {
                System.out.println("<br />");
            }

            System.out.print("<span style='color:rgb(" + (int) (255 - data[i]) + ", " + (int) (255 - data[i]) + ", " + (int) (255 - data[i]) + ")'>&#9608;</span>");
        } */

        for(i = 0; i < data.length; i++) {
            data[i] = data[i] <= threshold ? 0 : 1;
        }
/*
        if(label == 7 || label == 9) {
            for(i = 0; i < data.length; i++) {
                if(i % 28 == 0) {
                    System.out.println("");
                }

                System.out.print(data[i] == 1 ? "#" : " ");
            }
        }*/
        return data;
    }


}
