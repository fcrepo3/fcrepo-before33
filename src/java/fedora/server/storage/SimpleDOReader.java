package fedora.server.storage;

import fedora.server.Context;
import fedora.server.Logging;
import fedora.server.StdoutLogging;
import fedora.server.errors.DisseminatorNotFoundException;
import fedora.server.errors.MethodNotFoundException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.translation.DOTranslator;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.DisseminationBindingInfo;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.DSBinding;
import fedora.server.storage.types.DSBindingAugmented;
import fedora.server.storage.types.DSBindingMap;
import fedora.server.storage.types.DSBindingMapAugmented;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.ObjectMethodsDef;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;

/**
 * A DOReader backed by a DigitalObject.
 *
 * @author cwilper@cs.cornell.edu
 */
public class SimpleDOReader
        extends StdoutLogging
        implements DOReader {

    protected DigitalObject m_obj;
    private Context m_context;
    private RepositoryReader m_repoReader;
    private DOTranslator m_translator;
    private String m_shortExportFormat;
    private String m_longExportFormat;
    private String m_encoding;

    private SimpleDateFormat m_formatter=
            new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

    public SimpleDOReader(Context context, RepositoryReader repoReader,
            DOTranslator translator, String shortExportFormat,
            String longExportFormat, String currentFormat,
            String encoding, InputStream serializedObject, Logging logTarget)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        super(logTarget);
        m_context=context;
        m_repoReader=repoReader;
        m_translator=translator;
        m_shortExportFormat=shortExportFormat;
        m_longExportFormat=longExportFormat;
        m_encoding=encoding;
        m_obj=new BasicDigitalObject();
        m_translator.deserialize(serializedObject, m_obj, currentFormat, encoding);
    }

    /**
     * Alternate constructor for when a DigitalObject is already available
     * for some reason.
     */
    public SimpleDOReader(Context context, RepositoryReader repoReader,
            DOTranslator translator, String shortExportFormat,
            String longExportFormat, String encoding, DigitalObject obj,
            Logging logTarget) {
        super(logTarget);
        m_context=context;
        m_repoReader=repoReader;
        m_translator=translator;
        m_shortExportFormat=shortExportFormat;
        m_longExportFormat=longExportFormat;
        m_encoding=encoding;
        m_obj=obj;
    }

    public String getFedoraObjectType() {
        int t=m_obj.getFedoraObjectType();
        if (t==DigitalObject.FEDORA_OBJECT) {
            return "O";
        } else {
            if (t==DigitalObject.FEDORA_BMECH_OBJECT) {
                return "M";
            } else {
                return "D";
            }
        }
    }

    public String getContentModelId() {
        return m_obj.getContentModelId();
    }

    public Date getCreateDate() {
        return m_obj.getCreateDate();
    }

    public Date getLastModDate() {
        return m_obj.getLastModDate();
    }

    public String getLockingUser() {
        return m_obj.getLockingUser();
    }

    public InputStream GetObjectXML()
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        m_translator.serialize(m_obj, bytes, m_shortExportFormat, "UTF-8");
        return new ByteArrayInputStream(bytes.toByteArray());
    }

    public InputStream ExportObject()
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        m_translator.serialize(m_obj, bytes, m_longExportFormat, "UTF-8");
        return new ByteArrayInputStream(bytes.toByteArray());
    }

    public String GetObjectPID() {
        return m_obj.getPid();
    }

    public String GetObjectLabel() {
        return m_obj.getLabel();
    }

    public String GetObjectState() {
        if (m_obj.getState()==null) return "A"; // shouldn't happen, but if it does don't die
        return m_obj.getState();
    }

    public String[] ListDatastreamIDs(String state) {
        Iterator iter=m_obj.datastreamIdIterator();
        ArrayList al=new ArrayList();
        while (iter.hasNext()) {
            String dsId=(String) iter.next();
            if (state==null) {
                al.add(dsId);
            } else {
                // below should never return null -- already know id exists,
                // and am asking for any the latest existing one.
                Datastream ds=GetDatastream(dsId, null);
                if (ds.DSState.equals(state)) {
                    al.add(dsId);
                }
            }
        }
        iter=al.iterator();
        String[] out=new String[al.size()];
        int i=0;
        while (iter.hasNext()) {
            out[i]=(String) iter.next();
            i++;
        }
        return out;
    }

    // returns null if can't find
    public Datastream GetDatastream(String datastreamID, Date versDateTime) {
        List allVersions=m_obj.datastreams(datastreamID);
        if (allVersions.size()==0) {
            return null;
        }
        // get the one with the closest creation date
        // without going over
        Iterator dsIter=allVersions.iterator();
        Datastream closestWithoutGoingOver=null;
        Datastream latestCreated=null;
        long bestTimeDifference=-1;
        long latestCreateTime=-1;
        long vTime=-1;
        if (versDateTime!=null) {
            vTime=versDateTime.getTime();
        }
        while (dsIter.hasNext()) {
            Datastream ds=(Datastream) dsIter.next();
            if (versDateTime==null) {
                if (ds.DSCreateDT.getTime() > latestCreateTime) {
                    latestCreateTime=ds.DSCreateDT.getTime();
                    latestCreated=ds;
                }
            } else {
                long diff=vTime-ds.DSCreateDT.getTime();
                if (diff >= 0) {
                    if ( (diff < bestTimeDifference)
                            || (bestTimeDifference==-1) ) {
                        bestTimeDifference=diff;
                        closestWithoutGoingOver=ds;
                    }
                }
            }
        }
        if (versDateTime==null) {
            return latestCreated;
        } else {
            return closestWithoutGoingOver;
        }
    }

    public Datastream[] GetDatastreams(Date versDateTime) {
        String[] ids=ListDatastreamIDs(null);
        ArrayList al=new ArrayList();
        for (int i=0; i<ids.length; i++) {
           Datastream ds=GetDatastream(ids[i], versDateTime);
           if (ds!=null) {
               al.add(ds);
           }
        }
        Datastream[] out=new Datastream[al.size()];
        Iterator iter=al.iterator();
        int i=0;
        while (iter.hasNext()) {
            out[i]=(Datastream) iter.next();
            i++;
        }
        return out;
    }

    public String[] ListDisseminatorIDs(String state) {
        Iterator iter=m_obj.disseminatorIdIterator();
        ArrayList al=new ArrayList();
        while (iter.hasNext()) {
            String dissId=(String) iter.next();
            if (state==null) {
                al.add(dissId);
            } else {
                Disseminator diss=GetDisseminator(dissId, null);
                if (diss.dissState.equals(state)) {
                    al.add(dissId);
                }
            }
        }
        iter=al.iterator();
        String[] out=new String[al.size()];
        int i=0;
        while (iter.hasNext()) {
            out[i]=(String) iter.next();
            i++;
        }
        return out;
    }

    public Disseminator GetDisseminator(String disseminatorID, Date versDateTime) {
        List allVersions=m_obj.disseminators(disseminatorID);
        if (allVersions.size()==0) {
            return null;
        }
        // get the one with the closest creation date
        // without going over
        Iterator dissIter=allVersions.iterator();
        Disseminator closestWithoutGoingOver=null;
        Disseminator latestCreated=null;
        long bestTimeDifference=-1;
        long latestCreateTime=-1;
        long vTime=-1;
        if (versDateTime!=null) {
            vTime=versDateTime.getTime();
        }
        while (dissIter.hasNext()) {
            Disseminator diss=(Disseminator) dissIter.next();
            if (versDateTime==null) {
                if (diss.dissCreateDT.getTime() > latestCreateTime) {
                    latestCreateTime=diss.dissCreateDT.getTime();
                    latestCreated=diss;
                }
            } else {
                long diff=vTime-diss.dissCreateDT.getTime();
                if (diff >= 0) {
                    if ( (diff < bestTimeDifference)
                            || (bestTimeDifference==-1) ) {
                        bestTimeDifference=diff;
                        closestWithoutGoingOver=diss;
                    }
                }
            }
        }
        if (versDateTime==null) {
            return latestCreated;
        } else {
            return closestWithoutGoingOver;
        }
    }

    public Disseminator[] GetDisseminators(Date versDateTime) {
        String[] ids=ListDisseminatorIDs(null);
        ArrayList al=new ArrayList();
        for (int i=0; i<ids.length; i++) {
           Disseminator diss=GetDisseminator(ids[i], versDateTime);
           if (diss!=null) {
               al.add(diss);
           }
        }
        Disseminator[] out=new Disseminator[al.size()];
        Iterator iter=al.iterator();
        int i=0;
        while (iter.hasNext()) {
            out[i]=(Disseminator) iter.next();
            i++;
        }
        return out;
    }

    public String[] GetBehaviorDefs(Date versDateTime) {
        Disseminator[] disses=GetDisseminators(versDateTime);
        String[] bDefIds=new String[disses.length];
        for (int i=0; i<disses.length; i++) {
            bDefIds[i]=disses[i].bDefID;
        }
        return bDefIds;
    }

    public MethodDef[] getObjectMethods(String bDefPID, Date versDateTime)
            throws MethodNotFoundException, ServerException {

        if ( bDefPID.equalsIgnoreCase("fedora-system:1") ||
             bDefPID.equalsIgnoreCase("fedora-system:3"))
        {
          throw new MethodNotFoundException("[getObjectMethods] The object, "
            + m_obj.getPid()
            + ", will not report on dynamic method definitions "
            + "at this time (fedora-system:1 and fedora-system:3.");
        }
        String mechPid=getBMechPid(bDefPID, versDateTime);
        if (mechPid==null) {
            return null;
        }
        MethodDef[] methods = m_repoReader.getBMechReader(m_context, mechPid).
                getServiceMethods(versDateTime);
        // Filter out parms that are internal to the mechanism and not part
        // of the abstract method definition.  We just want user parms.
        for (int i=0; i<methods.length; i++)
        {
          methods[i].methodParms = filterParms(methods[i]);
        }
        return methods;
    }

    public InputStream getObjectMethodsXML(String bDefPID, Date versDateTime)
            throws MethodNotFoundException, ServerException {

        if ( bDefPID.equalsIgnoreCase("fedora-system:1") ||
             bDefPID.equalsIgnoreCase("fedora-system:3"))
        {
          throw new MethodNotFoundException("[getObjectMethodsXML] The object, "
            + m_obj.getPid()
            + ", will not report on dynamic method definitions "
            + "at this time (fedora-system:1 and fedora-system:3.");
        }
        String mechPid=getBMechPid(bDefPID, versDateTime);
        if (mechPid==null) {
            return null;
        }
        return m_repoReader.getBMechReader(m_context, mechPid).
                getServiceMethodsXML(versDateTime);
    }

    /**
     * Get the parameters for a given method.  The parameters returned
     * will be those that pertain to the abstract method definition, meaning
     * they will only be user-supplied parms.  Mechanism-specific parms
     * (system default parms and datastream input parms) will be filtered out.
     * @param bDefPID
     * @param methodName
     * @param versDateTime
     * @return
     * @throws DisseminatorNotFoundException
     * @throws MethodNotFoundException
     * @throws ServerException
     */
    public MethodParmDef[] getObjectMethodParms(String bDefPID,
            String methodName, Date versDateTime)
            throws MethodNotFoundException, ServerException {

        if ( bDefPID.equalsIgnoreCase("fedora-system:1") ||
             bDefPID.equalsIgnoreCase("fedora-system:3"))
        {
          throw new MethodNotFoundException("[getObjectMethodParms] The object, "
            + m_obj.getPid()
            + ", will not report on dynamic method definitions "
            + "at this time (fedora-system:1 and fedora-system:3.");
        }
        // The parms are expressed in the abstract method definitions
        // in the behavior mechanism object. Note that the mechanism object
        // is used here as if it were a behavior definition object.
        String mechPid=getBMechPid(bDefPID, versDateTime);
        if (mechPid==null) {
            return null;
        }
        MethodDef[] methods = m_repoReader.getBMechReader(m_context, mechPid).
                getServiceMethods(versDateTime);
        for (int i=0; i<methods.length; i++)
        {
          if (methods[i].methodName.equalsIgnoreCase(methodName))
          {
            return filterParms(methods[i]);
          }
        }
        throw new MethodNotFoundException("The object, " + m_obj.getPid()
                    + ", does not have a method named '" + methodName);
    }

    /**
     * Filter out mechanism-specific parms (system default parms and datastream
     * input parms) so that what is returned is only method parms that reflect
     * abstract method definitions.  Abstract method definitions only
     * expose user-supplied parms.
     * @param method
     * @return
     */
     private MethodParmDef[] filterParms(MethodDef method)
     {
        ArrayList filteredParms = new ArrayList();
        MethodParmDef[] parms = method.methodParms;
        for (int i=0; i<parms.length; i++)
        {
          if (parms[i].parmType.equalsIgnoreCase(MethodParmDef.USER_INPUT))
          {
            filteredParms.add(parms[i]);
          }
        }
        return (MethodParmDef[])filteredParms.toArray(new MethodParmDef[0]);
     }

    /**
     * Gets the bmech id for the disseminator subscribing to the bdef.
     *
     * @return null if it's the bootstrap bdef
     * @throws DisseminatorNotFoundException if no matching disseminator
     *         is found in the object.
     */
    private String getBMechPid(String bDefPID, Date versDateTime)
            throws DisseminatorNotFoundException {
        if (bDefPID.equals("fedora-system:1")) {
            return null;
        }
        Disseminator[] disses=GetDisseminators(versDateTime);
        String bMechPid=null;
        for (int i=0; i<disses.length; i++) {
            if (disses[i].bDefID.equals(bDefPID)) {
               bMechPid=disses[i].bMechID;
            }
        }
        if (bMechPid==null) {
            throw new DisseminatorNotFoundException("The object, "
                    + m_obj.getPid() + ", does not have a disseminator"
                    + " with bdef " + bDefPID + " at "
                    + getWhenString(versDateTime));
        }
        return bMechPid;
    }

    protected String getWhenString(Date versDateTime) {
        if (versDateTime!=null) {
            return m_formatter.format(versDateTime);
        } else {
            return "the current time";
        }
    }

    public DSBindingMapAugmented[] GetDSBindingMaps(Date versDateTime)
          throws ObjectIntegrityException, ServerException {
        Disseminator[] disses=GetDisseminators(versDateTime);
        DSBindingMapAugmented[] augMaps=new DSBindingMapAugmented[disses.length];
        for (int i=0; i<disses.length; i++) {
            DSBindingMapAugmented augMap=new DSBindingMapAugmented();
            augMap.dsBindMapID=disses[i].dsBindMap.dsBindMapID;
            augMap.dsBindMapLabel=disses[i].dsBindMap.dsBindMapLabel;
            augMap.dsBindMechanismPID=disses[i].dsBindMap.dsBindMechanismPID;
            DSBinding[] bindings=disses[i].dsBindMap.dsBindings;
            DSBindingAugmented[] augBindings=new DSBindingAugmented[bindings.length];
            for (int j=0; j<bindings.length; j++) {
                DSBindingAugmented augBinding=new DSBindingAugmented();
                augBinding.bindKeyName=bindings[j].bindKeyName;
                augBinding.bindLabel=bindings[j].bindLabel;
                augBinding.datastreamID=bindings[j].datastreamID;
                augBinding.seqNo=bindings[j].seqNo;
                // add values from the appropriate version of the datastream
                Datastream ds=GetDatastream(bindings[j].datastreamID, versDateTime);
                if (ds==null) {
                    String whenString=getWhenString(versDateTime);
                    throw new ObjectIntegrityException("The object, "
                            + m_obj.getPid() + ", does not have a datastream"
                            + " with id " + bindings[j].datastreamID
                            + " at " + whenString
                            + ", so the datastream binding map used by "
                            + "disseminator " + disses[i].dissID + " at "
                            + whenString + " is invalid.");
                }
                augBinding.DSVersionID=ds.DSVersionID;
                augBinding.DSControlGrp=ds.DSControlGrp;
                augBinding.DSLabel=ds.DSLabel;
                augBinding.DSMIME=ds.DSMIME;
                augBinding.DSLocation=ds.DSLocation;
                augBindings[j]=augBinding;
            }
            augMap.dsBindingsAugmented=augBindings;
            augMaps[i]=augMap;
        }
        return augMaps;
    }

    public DisseminationBindingInfo[] getDisseminationBindingInfo(String bDefPID,
          String methodName, Date versDateTime) {
        return null;
    }

    public ObjectMethodsDef[] getObjectMethods(Date versDateTime) {
        return null;
    }

}
