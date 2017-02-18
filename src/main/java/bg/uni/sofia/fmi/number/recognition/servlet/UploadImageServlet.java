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
				newImage = blackWhiteSwap(newImage);

				neuralNetwork.setInputs(imageToArray(newImage));
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
}
