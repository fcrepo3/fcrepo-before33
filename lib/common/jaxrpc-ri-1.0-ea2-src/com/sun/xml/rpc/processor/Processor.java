// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Processor.java

package com.sun.xml.rpc.processor;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor:
//            ProcessorException, ProcessorAction

public class Processor {

    private Properties _options;
    private Configuration _configuration;
    private List _actions;
    private Model _model;
    private boolean _printStackTrace;

    public Processor(Configuration configuration, Properties options) {
        _configuration = configuration;
        _options = options;
        _actions = new ArrayList();
        _printStackTrace = Boolean.valueOf(_options.getProperty("printStackTrace")).booleanValue();
    }

    public void add(ProcessorAction action) {
        _actions.add(action);
    }

    public Model getModel() {
        return _model;
    }

    public void run() {
        runModeler();
        if(_model != null)
            runActions();
    }

    public void runModeler() {
        try {
            ModelInfo modelInfo = _configuration.getModelInfo();
            if(modelInfo == null)
                throw new ProcessorException("processor.missing.model");
            _model = modelInfo.buildModel(_options);
        }
        catch(JAXRPCExceptionBase e) {
            if(_printStackTrace)
                e.printStackTrace();
            _configuration.getEnvironment().error(e);
        }
        catch(Exception e) {
            if(_printStackTrace)
                e.printStackTrace();
            _configuration.getEnvironment().error(new LocalizableExceptionAdapter(e));
        }
    }

    public void runActions() {
        try {
            if(_model == null)
                return;
            ProcessorAction action;
            for(Iterator iter = _actions.iterator(); iter.hasNext(); action.perform(_model, _configuration, _options))
                action = (ProcessorAction)iter.next();

        }
        catch(JAXRPCExceptionBase e) {
            if(_printStackTrace)
                e.printStackTrace();
            _configuration.getEnvironment().error(e);
        }
        catch(Exception e) {
            if(_printStackTrace)
                e.printStackTrace();
            _configuration.getEnvironment().error(new LocalizableExceptionAdapter(e));
        }
    }
}
