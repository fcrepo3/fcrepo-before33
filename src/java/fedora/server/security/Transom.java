package fedora.server.security;

import java.io.File;

public class Transom { 	
	
	private Boolean allowSurrogate = null;
	private File surrogatePolicyDirectory = null;
	
	private Transom() {

	}

	static final Transom singleton = new Transom();
	
	public static final Transom getInstance() {
		return singleton;
	}
	
	public Boolean getAllowSurrogate() {
		return allowSurrogate;
	}

	public File getSurrogatePolicyDirectory() {
		return surrogatePolicyDirectory;
	}
	
	public void setAllowSurrogate(boolean allowSurrogate) {
		if (this.allowSurrogate == null) {
			if (allowSurrogate) {
				this.allowSurrogate = Boolean.TRUE;
			} else {
				this.allowSurrogate = Boolean.FALSE;				
			}
		}
	}
	
	public void setSurrogatePolicyDirectory(File surrogatePolicyDirectory) {
		if (this.surrogatePolicyDirectory == null) {
			this.surrogatePolicyDirectory = surrogatePolicyDirectory;
		}
	}

}




