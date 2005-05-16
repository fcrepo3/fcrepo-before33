package fedora.server.resourceIndex;

import java.io.File;

import fedora.utilities.ExecUtility;

public class TestKowariInstance {
	private static final String CMD = "java -Djava.endorsed.dirs=../lib fedora.server.resourceIndex.KowariInstance";
	private static final File DIR = new File("/home/eddie/workspace/fedora/build");
	
	public static void main(String[] args) throws Exception {	
		System.out.println("Loading initial db...");
		Process cp = ExecUtility.exec(CMD + " true", DIR);
		System.out.println("Loaded initial db.");
		for (int i = 0; i < 25; i++) {
			System.out.println("run: " + (i + 1));
			cp = ExecUtility.exec(CMD, DIR);
		}
		System.out.println("Finished.");
	}
}
