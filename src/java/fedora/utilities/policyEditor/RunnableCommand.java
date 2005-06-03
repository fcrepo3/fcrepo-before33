package fedora.utilities.policyEditor;
/**
*
* <p><b>Title:</b> RunnableCommand.java</p>
* <p><b>Description:</b> </p>
*
* -----------------------------------------------------------------------------
*
* <p><b>Copyright: </b>
*
* <p>The entire file consists of original code.  Copyright &copy; 2004-2005 by The
* Rector and Visitors of the University of Virginia.
* All rights reserved.</p>
*
* -----------------------------------------------------------------------------
*
* @author Robert Haschart at The University of Virginia
* @version $Id$
*/

public class RunnableCommand implements Runnable 
{
	protected Object parm1 = null;
	protected Object parm2 = null;
	protected Object parm3 = null;
	protected Object parm4 = null;
	protected Object result = null;
	
	public RunnableCommand() {}
	public RunnableCommand(Object p1) { parm1 = p1; }
	public RunnableCommand(Object p1, Object p2) { parm1 = p1; parm2 = p2; }
	public RunnableCommand(Object p1, Object p2, Object p3) 
	{ parm1 = p1; parm2 = p2; parm3 = p3; }
	public RunnableCommand(Object p1, Object p2, Object p3, Object p4) 
	{ parm1 = p1; parm2 = p2; parm3 = p3; parm4 = p4;}
	
	public Object getResult()  				{ return result; }
	public void setResult(String string) 	{ result = string; }
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
	}
}
