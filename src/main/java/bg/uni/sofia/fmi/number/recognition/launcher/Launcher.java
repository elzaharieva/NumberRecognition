package bg.uni.sofia.fmi.number.recognition.launcher;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import bg.uni.sofia.fmi.number.recognition.entry.ImageEntry;
import bg.uni.sofia.fmi.number.recognition.utils.FileUtils;
import bg.uni.sofia.fmi.number.recognition.utils.ImageUtils;

public class Launcher {
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\Kaloyan Spiridonov\\Desktop\\number.jpg");
		BufferedImage img = ImageIO.read(file);
		List<BufferedImage> digits = ImageUtils.splitToDigits(img);
		
		for (int i = 0; i < digits.size(); i++) {
			BufferedImage newImage = ImageUtils.prepareDigit(digits.get(i));
			ImageUtils.save(newImage, "jpg", new File("C:\\Users\\Kaloyan Spiridonov\\Desktop\\digit"+i+".jpg"));
		}
		
	}
//	public static void main(String[] args) {
//		Path trainingFile = Paths.get("resources", "train.csv");
//		List<String> lines = FileUtils.readFile(trainingFile);
//		List<ImageEntry> images = new ArrayList<ImageEntry>();
//		lines.forEach(line -> {
//			ImageEntry image = toImageEntry(line);
//			images.add(image);
//		});
//		System.out.println(images);
//	}
//
//	private static ImageEntry toImageEntry(String line) {
//		String[] elements = line.split(",");
//		ImageEntry image = new ImageEntry();
//		for (int i = 1; i < elements.length; i++) {
//			image.add(Integer.parseInt(elements[i]));
//		}
//		return image;
//	}
}
