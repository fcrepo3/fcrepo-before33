package fedora.server.storage.lowlevel;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.File;
import fedora.server.errors.LowlevelStorageException;

/**
 *
 * <p><b>Title:</b> TimestampPathAlgorithm.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
class TimestampPathAlgorithm extends PathAlgorithm {

	public TimestampPathAlgorithm (String storeBase) {
		super(storeBase);
	}

	public String format (String pid) throws LowlevelStorageException {
		GregorianCalendar calendar = new GregorianCalendar();
		String year = Integer.toString(calendar.get(Calendar.YEAR));
		String month = leftPadded(1+ calendar.get(Calendar.MONTH),2);
		String dayOfMonth = leftPadded(calendar.get(Calendar.DAY_OF_MONTH),2);
		String hourOfDay = leftPadded(calendar.get(Calendar.HOUR_OF_DAY),2);
		String minute = leftPadded(calendar.get(Calendar.MINUTE),2);
		String second = leftPadded(calendar.get(Calendar.SECOND),2);
		//String sep = configuration.getSeparator();
		return getStoreBase() + sep + year + sep + month + dayOfMonth + sep + hourOfDay +
			sep + minute /*+ sep + second*/ + sep + pid;
	}
	private static final String[] padding = {"", "0", "00", "000"};
	private final String leftPadded (int i, int n) throws LowlevelStorageException {
		if ((n > 3) || (n < 0) || (i < 0) || (i > 999)) {
			throw new LowlevelStorageException(true,getClass().getName() + ": faulty date padding");
		}
	        int m = (i > 99) ? 3 : (i > 9) ? 2 : 1;
		int p = n - m;
		return padding[p] + Integer.toString(i);
	}

}
