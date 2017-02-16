package bg.uni.sofia.fmi.number.recognition.servlet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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

import bg.uni.sofia.fmi.number.recognition.utils.ImageUtils;
import net.vivin.neural.NeuralNetwork;

@WebServlet("/upload")
@MultipartConfig
public class UploadImageServlet extends HttpServlet{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    
		try {
			Part filePart = req.getPart("file"); 
		    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		    InputStream fileContent = filePart.getInputStream();
		    BufferedImage image = ImageIO.read(fileContent);
		   
		    List<BufferedImage> digits = ImageUtils.splitToDigits(image);
		    FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Elena\\Desktop\\neuralNetwork.net");
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
			NeuralNetwork neuralNetwork;
			neuralNetwork = (NeuralNetwork) objectInputStream.readObject();
			objectInputStream.close();
			
			for (int i = 0; i < digits.size(); i++) {
		
				BufferedImage newImage = ImageUtils.prepareDigit(digits.get(i));
				newImage = blackWhiteSwap(newImage);
				
				neuralNetwork.setInputs(imageToArray(newImage));
//				double[] inputs = imageToArray(newImage);
//					//{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,13,122,190,255,253,236,44,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,112,181,252,252,252,253,252,252,95,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,29,221,252,252,236,158,104,252,252,204,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,156,106,84,48,0,13,252,252,204,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,13,252,252,175,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,47,252,252,84,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,132,252,252,84,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,132,252,252,229,188,23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,99,109,242,252,252,252,252,60,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,41,224,251,252,253,252,235,109,11,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,41,224,241,241,255,253,96,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,184,252,96,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,132,252,96,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,156,252,96,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,253,252,96,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,253,252,96,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,253,252,96,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,149,252,96,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,132,252,205,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,18,252,158,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
//				
//				
				double[] inputs = imageToArray(newImage);
				BufferedImage blImage = new BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB);
				int k =0;
				for (int j = 0; j < 28; j++) {
					for (int j2 = 0; j2 < 28; j2++) {
						Color color = new Color((int) inputs[k]);
						blImage.setRGB(j2, j, color.getRGB());
						k++;
					}					
				}
				ImageUtils.save(blImage, "jpg", new File("C:\\Users\\Elena\\Desktop\\bla.jpg"));

				//neuralNetwork.setInputs(inputs);
		            double[] receivedOutput = neuralNetwork.getOutput();

		            double max = receivedOutput[0];
		            double recognizedDigit = 0;
		            for(int j = 0; j < receivedOutput.length; j++) {
		                if(receivedOutput[j] > max) {
		                    max = receivedOutput[j];
		                    recognizedDigit = j;
		                }
		            }

		            resp.getWriter().println("Recognized: " +  recognizedDigit + ". Corresponding output value was " + max);

				
				
			
				ImageUtils.save(newImage, "jpg", new File("C:\\Users\\Elena\\Desktop\\digit"+i+".jpg"));
			}
		    
		    resp.setContentType("text/plain");
		    resp.getWriter().println("Hello - "+fileName);
		    resp.getWriter().close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private BufferedImage blackWhiteSwap(BufferedImage image) {
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				if(image.getRGB(i, j)==Color.BLACK.getRGB()) {
					image.setRGB(i, j, Color.white.getRGB());
				} else {
					image.setRGB(i, j, Color.BLACK.getRGB());
				}
			}
		}
		return image;
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
