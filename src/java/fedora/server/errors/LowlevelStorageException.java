package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> LowLevelStorageException.java</p>
 * <p><b>Description:</b> </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author wdn5e@virginia.edu
 * @version 1.0
 */
public class LowlevelStorageException extends StorageException {
	public LowlevelStorageException(boolean serverCaused, String bundleName, String code, String[] values,
			String[] details, Throwable cause) {
		super(null, code, null, null, cause);
		if (serverCaused) {
			setWasServer();
		}
	}
	public LowlevelStorageException(boolean serverCaused, String message, Throwable cause) {
		this(serverCaused, null, message, null, null, cause);
	}
	public LowlevelStorageException(boolean serverCaused, String message) {
		this(serverCaused, message, null);
	}

	public String getMessage() {
		Throwable e = getCause();
		String temp = super.getMessage();
		if (e != null) {
			temp += ("\t" + e.getMessage());
		}
		return temp;
	}

}




