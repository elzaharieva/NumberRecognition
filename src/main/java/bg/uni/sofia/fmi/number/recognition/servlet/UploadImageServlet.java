package bg.uni.sofia.fmi.number.recognition.servlet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import bg.uni.sofia.fmi.number.recognition.neuralnetwork.NeuralNetwork;
import bg.uni.sofia.fmi.number.recognition.utils.ImageUtils;

@WebServlet("/upload")
@MultipartConfig
public class UploadImageServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			Part filePart = req.getPart("file");
			InputStream fileContent = filePart.getInputStream();
			BufferedImage image = ImageIO.read(fileContent);

			List<BufferedImage> digits = ImageUtils.splitToDigits(image);
			NeuralNetwork neuralNetwork = deserializeNeuralNetwork();
			String finalDigit = "";
			for (int i = 0; i < digits.size(); i++) {
				BufferedImage newImage = ImageUtils.prepareDigit(digits.get(i));
				neuralNetwork.setInputs(otsu(imageToArray(newImage)));

				double[] receivedOutput = neuralNetwork.getOutput();

				double max = receivedOutput[0];
				double recognizedDigit = 0;
				for (int j = 0; j < receivedOutput.length; j++) {
					if (receivedOutput[j] > max) {
						max = receivedOutput[j];
						recognizedDigit = j;
					}
				}
				finalDigit=finalDigit+(int)recognizedDigit;
				

				//ImageUtils.save(newImage, "jpg", new File("C:\\Users\\Elena\\Desktop\\digit" + i + ".jpg"));
			}
			resp.getWriter().println("Recognized: "+finalDigit);
			resp.setContentType("text/plain");
			resp.getWriter().close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private BufferedImage blackWhiteSwap(BufferedImage image) {
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

	private static NeuralNetwork deserializeNeuralNetwork() throws ClassNotFoundException, IOException {
		FileInputStream fileInputStream = new FileInputStream(
				System.getProperty("user.dir") + "\\..\\webapps\\ROOT\\WEB-INF\\classes\\neuralNetwork.net");
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
		NeuralNetwork neuralNetwork = (NeuralNetwork) objectInputStream.readObject();
		objectInputStream.close();
		return neuralNetwork;
	}

	private double[] imageToArray(BufferedImage image) {
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
	
	 private double[] otsu(double[] data) {
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
