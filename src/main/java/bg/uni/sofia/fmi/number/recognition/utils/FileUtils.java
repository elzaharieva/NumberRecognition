package bg.uni.sofia.fmi.number.recognition.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileUtils {

	/**
	 * Reading file
	 * 
	 * @param path
	 *          path to file
	 * @return list of lines
	 */
	public static List<String> readFile(Path path) {
		List<String> fileLines = new ArrayList<>();
		try (Stream<String> lines = Files.lines(path)) {
			fileLines = lines.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileLines;
	}
}
