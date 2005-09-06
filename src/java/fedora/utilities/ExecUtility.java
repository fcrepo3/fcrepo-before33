package fedora.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Utility class for executing commands and sending the command's output to a
 * given OutputStream.
 * 
 * @author Edwin Shin
 */
public class ExecUtility {
	public static Process exec(String cmd) {
		return exec(cmd, null, System.out, null);
	}

	public static Process exec(String cmd, OutputStream out) {
		return exec(cmd, null, out, null);
	}

	public static Process exec(String cmd, File dir) {
		return exec(cmd, dir, System.out, null);
	}

	public static Process exec(String cmd, File dir, OutputStream out,
			OutputStream err) {
		Process cp = null;
		try {
			if (dir == null) {
				cp = Runtime.getRuntime().exec(cmd, null);
			} else {
				cp = Runtime.getRuntime().exec(cmd, null, dir);
			}

			// Print stdio of cmd
			if (out != null) {
				PrintStream pout = new PrintStream(out);
				PrintStream perr = null;
				if (err != null) {
					if (out == err) {
						perr = pout;
					} else {
						perr = new PrintStream(err);
					}
				} else {
					perr = System.err;
				}
				String err_line;
				String in_line;
				BufferedReader input = new BufferedReader(
						new InputStreamReader(cp.getInputStream()));
				BufferedReader error = new BufferedReader(
						new InputStreamReader(cp.getErrorStream()));
				while (true) {
					try {
						int val = cp.exitValue();
						break; // process exited,
					} catch (IllegalThreadStateException e) {
						// process has not terminated check for output
						if (error.ready()) {
							err_line = error.readLine();
							perr.println(err_line);
						} else if (input.ready()) {
							in_line = input.readLine();
							pout.println(in_line);
						} else {
							try {
								Thread.sleep(10);
							} catch (InterruptedException ie) {
								// don't worry, be happy
							}
						}
					}
				}
				input.close();
				error.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cp;
	}

	public static Process execCommandLineUtility(String cmd) {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			cmd = "cmd.exe /C " + cmd;
		}
		return exec(cmd, null, System.out, null);
	}

	public static Process execCommandLineUtilityWError(String cmd) {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			cmd = "cmd.exe /C " + cmd;
		}
		return exec(cmd, null, System.out, System.err);
	}

	public static Process execCommandLineUtility(String cmd, OutputStream out,
			OutputStream err) {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			cmd = "cmd.exe /C " + cmd;
		}
		return exec(cmd, null, out, err);
	}

	public static Process altExec(String command) {
		int result;
		// prepare buffers for process output and error streams
		StringBuffer err = new StringBuffer();
		StringBuffer out = new StringBuffer();

		try {
			Process proc = Runtime.getRuntime().exec(command);
			//create thread for reading inputStream (process' stdout)
			StreamReaderThread outThread = new StreamReaderThread(proc
					.getInputStream(), out);
			//create thread for reading errorStream (process' stderr)
			StreamReaderThread errThread = new StreamReaderThread(proc
					.getErrorStream(), err);
			//start both threads
			outThread.start();
			errThread.start();
			//wait for process to end
			result = proc.waitFor();
			//finish reading whatever's left in the buffers
			outThread.join();
			errThread.join();

			if (result != 0) {
				System.out.println("Process " + command
						+ " returned non-zero value:" + result);
				System.out.println("Process output:\n" + out.toString());
				System.out.println("Process error:\n" + err.toString());
			} else {
				System.out.println("Process " + command
						+ " executed successfully");
				System.out.println("Process output:\n" + out.toString());
				System.out.println("Process error:\n" + err.toString());
			}
		} catch (Exception e) {
			System.out.println("Error executing " + command);
			e.printStackTrace();
			//throw e;
		}
		return null;
	}

}
