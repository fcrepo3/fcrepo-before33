package fedora.server.storage.types;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * <p><b>Title:</b> Disseminator.java</p>
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
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Disseminator
{

  private ArrayList m_auditRecordIdList;

  public String parentPID;

  public boolean isNew=false;

  public String dissID;

  public String dissLabel;

  public String dissVersionID;

  public String bDefID;

  public String bMechID;

  public String bDefLabel;

  public String bMechLabel;

  public String dsBindMapID;

  public DSBindingMap dsBindMap;

  public Date dissCreateDT;

  public String dissState;

  public Disseminator()
  {
    m_auditRecordIdList=new ArrayList();
  }

  public List auditRecordIdList()
  {
    return m_auditRecordIdList;
  }
}