package fedora.server.config.webxml;

import java.util.ArrayList;
import java.util.List;

public class WelcomeFileList {
	private List<String> welcomeFiles;
	
	public WelcomeFileList() {
		welcomeFiles = new ArrayList<String>();
	}
	
	public List<String> getWelcomeFiles() {
		return welcomeFiles;
	}
	
	public void addWelcomeFile(String welcomeFile) {
		welcomeFiles.add(welcomeFile);
	}
}
