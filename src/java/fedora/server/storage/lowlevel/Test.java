package fedora.server.storage.lowlevel;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
class Test {
	private static final ILowlevelStorage lowlevelStorage = FileSystemLowlevelStorage.getInstance();
	private Test() {
	}
	
	private static final int bufferLength = 512;
	private static final void stream2streamCopy (InputStream in, OutputStream out) throws IOException {
		byte[] buffer= new byte[bufferLength];
		int bytesRead = 0;
		while ((bytesRead = in.read(buffer,0,bufferLength)) != -1) {
			out.write(buffer,0,bytesRead);
		}
	}
	
	public static void main(String[] argv) {
		System.out.println("\nbeginning Test " + (lowlevelStorage == null));
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line;
		try {
			while ( (line = in.readLine()) != null) {
				//System.err.println(line);
				String[] parts = line.split("\\s");
				if (parts.length < 1) {
					System.err.println("enter action (pid (path)), separated only by whitespace");
				} else {
				String action = parts[0];
				if (action.equals("audit")) {
					if (parts.length != 1) {
						System.err.println("enter audit");
					} else {
						try {
							lowlevelStorage.audit();
						} catch (Exception e) {
							System.out.println("error " + e.getMessage());
						}
					}
				} else {
					if (parts.length < 2) {
						System.err.println("enter action (pid (path)), separated only by whitespace");
					} else {
					String pid = parts[1];
					if (action.equals("remove")) {
						if (parts.length != 2) {
							System.err.println("enter remove pid");
						} else {
						try {
							lowlevelStorage.remove(pid);
						} catch (Exception e) {
							System.out.println("error: " + e.getMessage());
						}
						}
					} else {
						if (parts.length != 3) {
							System.err.println("enter action (pid (path)), separated only by whitespace");
						} else {
						File f = new File(parts[2]);
						if (action.equals("retrieve")) {
							FileOutputStream fos = new FileOutputStream(f);
							try {
								InputStream is = lowlevelStorage.retrieve(pid);
								stream2streamCopy (is, fos);
								is.close();
								fos.close();
							} catch (Exception e) {
								System.out.println("error " + e.getMessage());
							}
						} else if (action.equals("add") || action.equals("replace")) {
							FileInputStream fis = new FileInputStream(f);
							if (action.equals("add")) {
								try {
									lowlevelStorage.add(pid,fis);
								} catch (Exception e) {
									System.out.println("error " + e.getMessage());
								}
							} else if (action.equals("replace")) {
								try {
									lowlevelStorage.replace(pid,fis);
								} catch (Exception e) {
									System.out.println("error " + e.getMessage());
								}				
							}
						} else {
							System.err.println("action must be add, replace, retrieve, or remove");
						}
						}
					}
					}
					}
				}
				/*
				for (int i = 0; i < parts.length; i++) {
					System.err.println(parts[i]);
				}
				*/
			}
		} catch (IOException e0) {
			System.err.println("problem reading System.in");
		}
			
		System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!");
		byte[] bytes = new byte[1];
		bytes[0] = 32;
		try {
			lowlevelStorage.add("abc",new ByteArrayInputStream(bytes));
		} catch (Exception e) {
			System.out.println("1 " + e.getMessage());
		}
		try {
			lowlevelStorage.add("xyz",new ByteArrayInputStream(bytes));
		} catch (Exception e) {
			System.out.println("2 " + e.getMessage());
		}
		try {
			lowlevelStorage.add("xyz",new ByteArrayInputStream(bytes));
		} catch (Exception e) {
			System.out.println("3 " + e.getMessage());
		}
		try {
			lowlevelStorage.replace("xyz",new ByteArrayInputStream(bytes));
		} catch (Exception e) {
			System.out.println("4 " + e.getMessage());
		}				
		try {
			InputStream outputStream = lowlevelStorage.retrieve("abc");
		} catch (Exception e) {
			System.out.println("5 " + e.getMessage());
		}
		try {
			lowlevelStorage.remove("abc");
		} catch (Exception e) {
			System.out.println("6 " + e.getMessage());
		}
		try {
			lowlevelStorage.audit();
		} catch (Exception e) {
			System.out.println("7 " + e.getMessage());
		}

	}
}
