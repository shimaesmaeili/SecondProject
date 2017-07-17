import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logging {
	private static BufferedWriter logFile;

	public static void log(String fileName, String message) throws IOException {
		logFile = new BufferedWriter(new FileWriter(fileName, true));
		logFile.write(message);
		logFile.close();
	}
}
