package fedora.client.objecteditor;

import java.io.*;
import java.util.*;

import org.apache.axis.types.NonNegativeInteger;

import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;

import fedora.client.Administrator;
import fedora.client.objecteditor.types.DatastreamInputSpec;

/**
 * Some static utility methods that might be needed across several classes
 * in this package.
 */
public abstract class Util {

    /**
     * Get a map of pid-to-label of behavior mechanisms that implement
     * the behavior defined by the indicated bdef.
     */
    public static Map getBMechLabelMap(String bDefPID) 
            throws IOException {
        try {
        HashMap labelMap=new HashMap();
        FieldSearchQuery query=new FieldSearchQuery();
        Condition[] conditions=new Condition[2];
        conditions[0]=new Condition();
        conditions[0].setProperty("fType");
        conditions[0].setOperator(ComparisonOperator.fromValue("eq"));
        conditions[0].setValue("M");
        conditions[1]=new Condition();
        conditions[1].setProperty("bDef");
        conditions[1].setOperator(ComparisonOperator.fromValue("has"));
        conditions[1].setValue(bDefPID);
        query.setConditions(conditions);
        String[] fields=new String[] {"pid", "label"};
        FieldSearchResult result=Administrator.APIA.findObjects(
                    fields, new NonNegativeInteger("20"), query);
        ObjectFields[] resultList=result.getResultList();
        for (int i=0; i<resultList.length; i++) {
            labelMap.put(resultList[i].getPid(), resultList[i].getLabel());
        }
        return labelMap;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static Map getInputSpecMap(Set bMechPIDs) 
            throws IOException {
        HashMap specMap=new HashMap();
        Iterator iter=bMechPIDs.iterator();
        while (iter.hasNext()) {
            String pid=(String) iter.next();
            specMap.put(pid, getInputSpec(pid));
        }
        return specMap;
    }

    public static DatastreamInputSpec getInputSpec(String bMechPID) 
            throws IOException {
        HashMap hash=new HashMap();
        hash.put("itemID", "DSINPUTSPEC");
        return DatastreamInputSpec.parse(
                Administrator.DOWNLOADER.getDissemination(
                        bMechPID,
                        "fedora-system:3",
                        "getItem",
                        hash,
                        null)
                );

    }

    /**
     * Get the indicated fields of the indicated object from the repository.
     */
    public static ObjectFields getObjectFields(String pid, String[] fields)
            throws IOException {
        FieldSearchQuery query=new FieldSearchQuery();
        Condition[] conditions=new Condition[1];
        conditions[0]=new Condition();
        conditions[0].setProperty("pid");
        conditions[0].setOperator(ComparisonOperator.fromValue("eq"));
        conditions[0].setValue(pid);
        query.setConditions(conditions);
        FieldSearchResult result=Administrator.APIA.findObjects(
                    fields, new NonNegativeInteger("1"), query);
        ObjectFields[] resultList=result.getResultList();
        if (resultList==null || resultList.length==0) {
            throw new IOException("Object not found in repository");
        }
        return resultList[0];
    }

}