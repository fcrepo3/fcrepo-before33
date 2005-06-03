package fedora.utilities.policyEditor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/*
 * @author diglib
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/* A FileNode is a derivative of the File class - though we delegate to 
 * the File object rather than subclassing it. It is used to maintain a 
 * cache of a directory's children and therefore avoid repeated access 
 * to the underlying file system during rendering. 
 */

class FedoraNode 
{ 
    String  name;
    String shortname;
    Object parent;
    Vector children; 
    Object access[];
    String action;
    String resource;
    public final static String noException = "-- none --";
    public final static String seeParent = "    Access Defined by Parent";
    public final static String seeChildren = "    Access Defined explicitly for Children";
    public final static String seeParentShort = "seeParent";
    public final static String seeChildrenShort = "seeChildren";
    public final static String indentSpaces = "                                                                    ";
    public static FedoraSystemModel model = null;
    public static String sep = "";
    
    static 
    {
        sep = System.getProperty("line.separator");
    }
    
    public FedoraNode(Object parent, String nodename, String shortname, 
                      String action, String resource)
    {
        this.parent = parent;
        name = nodename; 
        this.shortname = shortname;
        children = new Vector();
        access = new Object[2];
        this.action = action;
        this.resource = resource;
        access[0] = "";
        access[1] = "";
    }
    
    /**
     * Returns the the string to be used to display this leaf in the JTree.
     */
    public void writePolicies(File dir, StatusDialog cur)
    {
        writePolicies(dir, 0, cur);  // allow Policies
        writePolicies(dir, 1, cur);  // overriding deny policies
    }
    
    public void writePolicies(File dir, int index, StatusDialog cur)
    {
        if (access[index].equals(seeChildren))
        {
            for (int i = 0; i < children.size(); i++)
            {
                FedoraNode child = (FedoraNode)children.elementAt(i);
                child.writePolicies(dir, index, cur);
            }            
        }
        else if (access[index].equals(seeParent))
        {
            System.out.println("Error: this shouldn't happen");
        }
        else if (access[index].toString().indexOf(noException) != -1)
        {
            // do nothing
        }
        else
        {
            String filename = getPolicyFilename(index);
            System.out.println("Writing Policy: " + filename);
            String action = getActions();
            if (action.length() == 0) return;
            {
                cur.addText(filename);
                try{
                    Thread.sleep(100);
                }
                catch (InterruptedException ie)
                {
                }
                writePolicy(dir, filename, getDescription(index), action, 
                            getResource(), getSubject(index), getRule(index)); 
            }
        }
    }
        
    
    String getPolicyFilename(int index)
    {
        String prefix = (index == 0) ? "Permit-" : "Deny-";
        String suffix = access[index].toString();
        String tmpName = prefix + shortname + "-" + suffix + ".xml";
        String newTmp = Utility.camelCasify(tmpName);
        return(newTmp);
    }
    
    String getDescription(int index)
    {
        String prefix = (index == 0) ? "Permit" : "Deny";
        String suffix = ((GroupRuleInfo)access[index]).getDescription();
        String tmpName = prefix + " " + name + " " + suffix;
        return(tmpName);
    }
    
    String getActions()
    {
        String actionStr = getAction();
        if (actionStr.length() > 0)
        {
            actionStr = "<Actions>" + "\n" + actionStr + "\n" + "</Actions>";
            actionStr = actionStr.replaceAll("\n", sep);
        }
        return(actionStr);
    }
    
    String getAction()
    {
        String actionStr = null;
        if (action.equals("-combinechildren-"))
        {
            actionStr = "";
            for (int i = 0; i < children.size(); i++)
            {
                FedoraNode child = (FedoraNode)children.elementAt(i);
                actionStr = actionStr + "\n" + child.getAction() + "\n";
            }            
        }
        else
        {
            actionStr = action;
        }
        return(actionStr);
    }
    
    String getResource()
    {
        String resourceStr = null;
        if (resource.equals("-combinechildren-"))
        {
            resourceStr = "";
            for (int i = 0; i < children.size(); i++)
            {
                FedoraNode child = (FedoraNode)children.elementAt(i);
                resourceStr = resourceStr + sep + child.getResource();
            }            
        }
        else
        {
            resourceStr = resource;
        }
        resourceStr = "<Resources>" + sep + resourceStr + sep + "</Resources>";
        return(resourceStr);
    }
    
    String getSubject(int index)
    {
        String subject = ((GroupRuleInfo)access[index]).getExpandedSubject();
        subject = subject.replaceAll("\n", sep);
        return(subject);
    }
    
    String getRule(int index)
    {
        String condition = ((GroupRuleInfo)access[index]).getExpandedCondition();
        String permitOrDeny = ((GroupRuleInfo)access[index]).getEffect();
        String rule = "  <Rule RuleId=\"1\" Effect=\"" + permitOrDeny + "\">\n" + condition + "\n  </Rule>\n";        
        rule = rule.replaceAll("\n", sep);
        return(rule);
    }
    
    public void writePolicy(File dir, String policyFilename, 
                            String description, String action, 
                            String resource, String subject, String rule)
    {
        File file = new File(dir, policyFilename);
        try {
            FileWriter fwriter = new FileWriter(file);
            PrintWriter writer = new PrintWriter(fwriter);
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<Policy xmlns=\"urn:oasis:names:tc:xacml:1.0:policy\"");
            writer.println("        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
            writer.println("        PolicyId=\""+policyFilename.substring(0, policyFilename.length()-4)+"\"");
            writer.println("        RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable\">");
            writer.println("  <Description>"+description+"</Description>");
            writer.println("  <Target>");
            writer.println(subject);
            writer.println(resource);
            writer.println(action);
            writer.println("  </Target>");
            writer.println(rule);
            writer.println("</Policy>");
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            System.out.println("Error: Unable to open file: " + policyFilename);
            return;
        }
    }
     
    public void storePolicySettings(PrintWriter out)
    {
 //       String indent = indentSpaces.substring(0, indentLevel*4);
        if (!access[0].equals(seeParent) || !access[1].equals(seeParent))
        {
            out.println("    <access shortname=\""+
                        shortname+"\" value=\""+getGroupShortname(0)+"\" value1=\"" +
                        getGroupShortname(1)+"\"/>");
            for (int i = 0; i < children.size(); i++)
            {
                FedoraNode child = (FedoraNode)children.elementAt(i);
                child.storePolicySettings(out);
            }
        }
 //       out.println(indent+"</resource>");
    }
    
    
//    private int getOffset(int index)
//    {
//        if (access[index].toString().equals(seeChildren))
//        {
//            return(1);
//        }
//        else if (access[index].toString().equals(seeParent) ||
//                 access[index].toString().length()== 0)
//        {
//            return(0);
//        }
//        else
//        {
//            int offset = GroupRuleInfo.findEntry(index == 0, access[index] );
//            return(offset + 2);
//        }        
//    }
    
    public String toString() 
    { 
        return name;
    }

    public FedoraNode getNode() 
    {
        return this; 
    }
    
    public Object getValue(int index) 
    {
        switch (index)
        {
           case 0: return(access[0].toString().equals("") ? seeParent : access[0]);
           case 1: return(access[1].toString().equals("") ? seeParent : access[1]);
        }
        return(Boolean.FALSE);
    }

    public void setValue(int index, Object value) 
    {
        switch (index)
        {
           case 0: HandleNewValue(this, index, value);  break;
           case 1: HandleNewValue(this, index, value);  break;
        }
    }

    public static void HandleNewValue(FedoraNode node, int index, Object value)
    {
        if (node.access[index].toString().equals(value.toString())) return;
        System.out.println("Setting node: "+node.name+ " to value: "+value);
        if (node.access[index].toString().equals(seeChildren))
        {
           node.access[index] = value;
           for (int i = 0; i < node.children.size(); i++)
           {
               FedoraNode child = (FedoraNode)node.children.elementAt(i);
               if (model != null)
               {
                   model.setValueAt(seeParent, child, index+1);
               }
           }
        }
        else if (value.toString().equals(seeChildren))
        {
           Object oldValue = node.access[index];
           if (model != null && (oldValue.toString().equals(seeParent) || oldValue.toString().equals("")))
           {
               model.setValueAt( seeChildren, (FedoraNode)node.parent, index+1);
               oldValue = node.access[index];
           }
           node.access[index] = value;
           for (int i = 0; i < node.children.size(); i++)
           {
               FedoraNode child = (FedoraNode)node.children.elementAt(i);
               if (child.access[index].toString().equals(seeParent) || 
                   child.access[index].toString().equals(""))
               {
                   if (model != null)
                   {
                       model.setValueAt(oldValue, child, index+1);
                   }
               }
           }
        }
        else if (node.access[index].toString().equals(seeParent) || 
                 node.access[index].toString().length() == 0)
        {
            if (node.parent != null)
            {
                if (model != null)
                {
                    model.setValueAt( seeChildren, (FedoraNode)node.parent, index+1);
                }
            }
            node.access[index] = value;
//            System.out.println("Setting node: "+node.name+ " to value: "+value);
        }
        else
        {
            node.access[index] = value;
//            System.out.println("Setting node: "+node.name+ " to value: "+value);            
        }
    }
    
    /**
     * Loads the children, caching the results in the children ivar.
     */
    protected Object[] getChildren() 
    {
        return children.toArray(); 
    }
    /**
     * @return Returns the shortname.
     */
    public String getShortname()
    {
        return shortname;
    }
    /**
     * @param shortname The shortname to set.
     */
    public void setShortname(String shortname)
    {
        this.shortname = shortname;
    }
    
    public String getGroupShortname(int index)
    {
        if (access[index] instanceof GroupRuleInfo)
        {
            String res = ((GroupRuleInfo)access[index]).getShortGroupname();
            return(res);
        }
        else if (access[index].equals(seeParent))
        {
            return(seeParentShort);
        }
        else if (access[index].equals(seeChildren))
        {
            return(seeChildrenShort);
        }
        return(null);
    }
}

