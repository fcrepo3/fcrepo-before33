package fedora.client.batch.types;

import fedora.server.errors.StreamIOException;

import java.util.Calendar;

/**
 *
 * <p><b>Title:</b> Datastream.java</p>
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
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Datastream
{

  public boolean isHarvestable=false;

  public String DatastreamID;

  public String DSVersionID;

  public String DSLabel;

  public String DSMIME;

  public Calendar asOfDate;

  public String DSControlGrp;

  public String DSInfoType;

  public String DSState;

  public String DSFormatURI;

  public String DSLocation;

  public String mdClass;

  public String mdType;

  public String objectPID;

  public byte[] xmlContent;



}
