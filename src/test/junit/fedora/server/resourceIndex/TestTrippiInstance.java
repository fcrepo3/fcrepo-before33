package fedora.server.resourceIndex;

import java.io.*;

public class TestTrippiInstance {
	private static final String CMD = "java -Djava.endorsed.dirs=../lib fedora.server.resourceIndex.TrippiInstance";
	private static final File DIR = new File("d:\\home\\es\\workspace\\fedora-2.0\\bin");
	
	public static void main(String[] args) throws Exception {	
		System.out.println("Starting first run...");
		Process cp = exec(CMD + " true", DIR);
		System.out.println("First run complete.");
		System.out.println("Loaded initial db.");
		for (int i = 0; i < 10; i++) {
			System.out.println("run: " + (i + 1));
			cp = exec(CMD, DIR);
		}
		System.out.println("Finished.");
	}
	
	public static Process exec(String cmd, File dir) {
		Process cp = null;
		try {
			cp = Runtime.getRuntime().exec(cmd, null, dir);
	
			// Print stdio of cmd
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(cp
					.getInputStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cp;
	}
}
