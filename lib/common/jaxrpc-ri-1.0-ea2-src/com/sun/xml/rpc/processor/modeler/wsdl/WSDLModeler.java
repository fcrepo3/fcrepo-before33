// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   WSDLModeler.java

package com.sun.xml.rpc.processor.modeler.wsdl;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.processor.model.ModelObject;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.java.JavaArrayType;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaMethod;
import com.sun.xml.rpc.processor.model.java.JavaParameter;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeOwningType;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestUnorderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.ModelerConstants;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.StringUtils;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.Binding;
import com.sun.xml.rpc.wsdl.document.BindingFault;
import com.sun.xml.rpc.wsdl.document.BindingInput;
import com.sun.xml.rpc.wsdl.document.BindingOperation;
import com.sun.xml.rpc.wsdl.document.BindingOutput;
import com.sun.xml.rpc.wsdl.document.Definitions;
import com.sun.xml.rpc.wsdl.document.Documentation;
import com.sun.xml.rpc.wsdl.document.Fault;
import com.sun.xml.rpc.wsdl.document.Input;
import com.sun.xml.rpc.wsdl.document.Kinds;
import com.sun.xml.rpc.wsdl.document.Message;
import com.sun.xml.rpc.wsdl.document.MessagePart;
import com.sun.xml.rpc.wsdl.document.Operation;
import com.sun.xml.rpc.wsdl.document.OperationStyle;
import com.sun.xml.rpc.wsdl.document.Output;
import com.sun.xml.rpc.wsdl.document.Port;
import com.sun.xml.rpc.wsdl.document.PortType;
import com.sun.xml.rpc.wsdl.document.Service;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.document.schema.SchemaKinds;
import com.sun.xml.rpc.wsdl.document.soap.SOAPAddress;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBinding;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBody;
import com.sun.xml.rpc.wsdl.document.soap.SOAPFault;
import com.sun.xml.rpc.wsdl.document.soap.SOAPHeader;
import com.sun.xml.rpc.wsdl.document.soap.SOAPOperation;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;
import com.sun.xml.rpc.wsdl.framework.Defining;
import com.sun.xml.rpc.wsdl.framework.Extensible;
import com.sun.xml.rpc.wsdl.framework.Extension;
import com.sun.xml.rpc.wsdl.framework.GlobalEntity;
import com.sun.xml.rpc.wsdl.framework.GloballyKnown;
import com.sun.xml.rpc.wsdl.framework.NoSuchEntityException;
import com.sun.xml.rpc.wsdl.framework.ParseException;
import com.sun.xml.rpc.wsdl.framework.ValidationException;
import com.sun.xml.rpc.wsdl.parser.SOAPEntityReferenceValidator;
import com.sun.xml.rpc.wsdl.parser.WSDLParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.rpc.namespace.QName;
import org.xml.sax.InputSource;

// Referenced classes of package com.sun.xml.rpc.processor.modeler.wsdl:
//            SchemaAnalyzer

public class WSDLModeler
    implements Modeler {

    private static final String OPERATION_HAS_VOID_RETURN_TYPE = "operationHasVoidReturnType";
    private static final String WSDL_DOCUMENTATION = "wsdlDocumentation";
    private WSDLModelInfo _modelInfo;
    private Properties _options;
    private SchemaAnalyzer _analyzer;
    private LocalizableMessageFactory _messageFactory;

    public WSDLModeler(WSDLModelInfo modelInfo, Properties options) {
        _modelInfo = modelInfo;
        _options = options;
        _messageFactory = new LocalizableMessageFactory("com.sun.xml.rpc.resources.modeler");
    }

    public Model buildModel() {
        Model model = new Model(new QName(null, _modelInfo.getName()));
        try {
            WSDLParser parser = new WSDLParser();
            InputSource inputSource = new InputSource(_modelInfo.getLocation());
            WSDLDocument document = parser.parse(inputSource);
            document.validateLocally();
            boolean validateWSDL = Boolean.valueOf(_options.getProperty("validationWSDL")).booleanValue();
            if(validateWSDL)
                document.validate(new SOAPEntityReferenceValidator());
            _analyzer = new SchemaAnalyzer(document, _modelInfo, _options);
            model.setTargetNamespaceURI(document.getDefinitions().getTargetNamespaceURI());
            setDocumentationIfPresent(model, document.getDefinitions().getDocumentation());
            boolean hasServices = false;
            Set serviceNames = new HashSet();
            Set portNames = new HashSet();
            for(Iterator iter = document.getDefinitions().services(); iter.hasNext();) {
                hasServices = true;
                Service service = (Service)iter.next();
                serviceNames.add(service.getName());
                Port port;
                for(Iterator iter2 = service.ports(); iter2.hasNext(); portNames.add(port.getName()))
                    port = (Port)iter2.next();

            }

            if(hasServices) {
                for(Iterator iter = document.getDefinitions().services(); iter.hasNext();) {
                    processService((Service)iter.next(), model, document, serviceNames, portNames);
                    hasServices = true;
                }

            } else {
                warn("wsdlmodeler.warning.noServiceDefinitionsFound");
            }
        }
        catch(ModelException e) {
            throw new ModelerException(e);
        }
        catch(ParseException e) {
            throw new ModelerException(e);
        }
        catch(ValidationException e) {
            throw new ModelerException(e);
        }
        finally {
            _analyzer = null;
        }
        return model;
    }

    protected void processService(Service wsdlService, Model model, WSDLDocument document, Set serviceNames, Set portNames) {
        String suffix = portNames.contains(wsdlService.getName()) ? "_Service" : "";
        String serviceInterface;
        if(_modelInfo.getJavaPackageName() != null && !_modelInfo.getJavaPackageName().equals(""))
            serviceInterface = _modelInfo.getJavaPackageName() + "." + Names.validJavaClassName(wsdlService.getName()) + suffix;
        else
            serviceInterface = Names.validJavaClassName(wsdlService.getName()) + suffix;
        com.sun.xml.rpc.processor.model.Service service = new com.sun.xml.rpc.processor.model.Service(getQNameOf(wsdlService), new JavaInterface(serviceInterface, serviceInterface + "Impl"));
        setDocumentationIfPresent(service, wsdlService.getDocumentation());
        boolean hasPorts = false;
        for(Iterator iter = wsdlService.ports(); iter.hasNext();) {
            boolean processed = processPort((Port)iter.next(), service, document, serviceNames, portNames);
            hasPorts = hasPorts || processed;
        }

        model.addService(service);
        if(!hasPorts)
            warn("wsdlmodeler.warning.noPortsInService", wsdlService.getName());
    }

    protected boolean processPort(Port wsdlPort, com.sun.xml.rpc.processor.model.Service service, WSDLDocument document, Set serviceNames, Set portNames) {
        try {
            String suffix = serviceNames.contains(wsdlPort.getName()) ? "_Port" : "";
            com.sun.xml.rpc.processor.model.Port port = new com.sun.xml.rpc.processor.model.Port(getQNameOf(wsdlPort));
            setDocumentationIfPresent(port, wsdlPort.getDocumentation());
            SOAPAddress soapAddress = (SOAPAddress)getExtensionOfType(wsdlPort, com.sun.xml.rpc.wsdl.document.soap.SOAPAddress.class);
            if(soapAddress == null) {
                warn("wsdlmodeler.warning.ignoringNonSOAPPort.noAddress", wsdlPort.getName());
                return false;
            }
            port.setAddress(soapAddress.getLocation());
            Binding binding = wsdlPort.resolveBinding(document);
            PortType portType = binding.resolvePortType(document);
            SOAPBinding soapBinding = (SOAPBinding)getExtensionOfType(binding, com.sun.xml.rpc.wsdl.document.soap.SOAPBinding.class);
            if(soapBinding == null) {
                warn("wsdlmodeler.warning.ignoringNonSOAPPort", wsdlPort.getName());
                return false;
            }
            if(soapBinding.getTransport() == null || !soapBinding.getTransport().equals("http://schemas.xmlsoap.org/soap/http")) {
                warn("wsdlmodeler.warning.ignoringSOAPBinding.nonHTTPTransport", wsdlPort.getName());
                return false;
            }
            boolean hasOverloadedOperations = false;
            Set operationNames = new HashSet();
            Operation operation;
            for(Iterator iter = portType.operations(); iter.hasNext(); operationNames.add(operation.getName())) {
                operation = (Operation)iter.next();
                if(!operationNames.contains(operation.getName()))
                    continue;
                hasOverloadedOperations = true;
                break;
            }

            Map headers = new HashMap();
            boolean hasOperations = false;
            for(Iterator iter = binding.operations(); iter.hasNext();) {
                BindingOperation bindingOperation = (BindingOperation)iter.next();
                Operation portTypeOperation = null;
                Set operations = portType.getOperationsNamed(bindingOperation.getName());
                if(operations.size() == 0)
                    throw new ModelerException("wsdlmodeler.invalid.bindingOperation.notInPortType", new Object[] {
                        bindingOperation.getName(), binding.getName()
                    });
                if(operations.size() == 1) {
                    portTypeOperation = (Operation)operations.iterator().next();
                } else {
                    boolean found = false;
                    String expectedInputName = bindingOperation.getInput().getName();
                    String expectedOutputName = bindingOperation.getOutput().getName();
                    for(Iterator iter2 = operations.iterator(); iter2.hasNext();) {
                        Operation candidateOperation = (Operation)iter2.next();
                        if(expectedInputName == null)
                            throw new ModelerException("wsdlmodeler.invalid.bindingOperation.missingInputName", new Object[] {
                                bindingOperation.getName(), binding.getName()
                            });
                        if(expectedOutputName == null)
                            throw new ModelerException("wsdlmodeler.invalid.bindingOperation.missingOutputName", new Object[] {
                                bindingOperation.getName(), binding.getName()
                            });
                        if(expectedInputName.equals(candidateOperation.getInput().getName()) && expectedOutputName.equals(candidateOperation.getOutput().getName())) {
                            if(found)
                                throw new ModelerException("wsdlmodeler.invalid.bindingOperation.multipleMatchingOperations", new Object[] {
                                    bindingOperation.getName(), binding.getName()
                                });
                            found = true;
                            portTypeOperation = candidateOperation;
                        }
                    }

                    if(!found)
                        throw new ModelerException("wsdlmodeler.invalid.bindingOperation.notFound", new Object[] {
                            bindingOperation.getName(), binding.getName()
                        });
                }
                com.sun.xml.rpc.processor.model.Operation operation2 = processSOAPOperation(new WSDLModeler$ProcessSOAPOperationInfo(this, wsdlPort, portTypeOperation, bindingOperation, soapBinding, document, hasOverloadedOperations, headers));
                if(operation2 != null) {
                    port.addOperation(operation2);
                    hasOperations = true;
                }
            }

            if(!hasOperations)
                warn("wsdlmodeler.warning.noOperationsInPort", wsdlPort.getName());
            port.setClientHandlerChainInfo(_modelInfo.getClientHandlerChainInfo());
            port.setServerHandlerChainInfo(_modelInfo.getServerHandlerChainInfo());
            service.addPort(port);
            createJavaInterfaceForPort(port, suffix);
            return true;
        }
        catch(NoSuchEntityException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected com.sun.xml.rpc.processor.model.Operation processSOAPOperation(WSDLModeler$ProcessSOAPOperationInfo info) {
        com.sun.xml.rpc.processor.model.Operation operation = new com.sun.xml.rpc.processor.model.Operation(new QName(null, info.bindingOperation.getName()));
        setDocumentationIfPresent(operation, info.portTypeOperation.getDocumentation());
        if(info.portTypeOperation.getStyle() != OperationStyle.REQUEST_RESPONSE) {
            warn("wsdlmodeler.warning.ignoringOperation.notRequestResponse", info.portTypeOperation.getName());
            return null;
        }
        SOAPStyle soapStyle = info.soapBinding.getStyle();
        SOAPOperation soapOperation = (SOAPOperation)getExtensionOfType(info.bindingOperation, com.sun.xml.rpc.wsdl.document.soap.SOAPOperation.class);
        if(soapOperation != null) {
            if(soapOperation.getStyle() != null)
                soapStyle = soapOperation.getStyle();
            if(soapOperation.getSOAPAction() != null)
                operation.setSOAPAction(soapOperation.getSOAPAction());
        }
        operation.setStyle(soapStyle);
        String uniqueOperationName = getUniqueName(info.portTypeOperation, info.hasOverloadedOperations);
        if(info.hasOverloadedOperations)
            operation.setUniqueName(uniqueOperationName);
        info.operation = operation;
        info.uniqueOperationName = uniqueOperationName;
        if(soapStyle == SOAPStyle.RPC)
            return processSOAPOperationRPCStyle(info);
        else
            return processSOAPOperationDocumentStyle(info);
    }

    protected com.sun.xml.rpc.processor.model.Operation processSOAPOperationRPCStyle(WSDLModeler$ProcessSOAPOperationInfo info) {
        Request request = new Request();
        Response response = new Response();
        SOAPBody soapRequestBody = (SOAPBody)getExtensionOfType(info.bindingOperation.getInput(), com.sun.xml.rpc.wsdl.document.soap.SOAPBody.class);
        if(soapRequestBody == null)
            throw new ModelerException("wsdlmodeler.invalid.bindingOperation.inputMissingSoapBody", new Object[] {
                info.bindingOperation.getName()
            });
        SOAPBody soapResponseBody = (SOAPBody)getExtensionOfType(info.bindingOperation.getOutput(), com.sun.xml.rpc.wsdl.document.soap.SOAPBody.class);
        if(soapResponseBody == null)
            throw new ModelerException("wsdlmodeler.invalid.bindingOperation.outputMissingSoapBody", new Object[] {
                info.bindingOperation.getName()
            });
        if(soapRequestBody.isLiteral() || !tokenListContains(soapRequestBody.getEncodingStyle(), "http://schemas.xmlsoap.org/soap/encoding/") || soapResponseBody.isLiteral() || !tokenListContains(soapResponseBody.getEncodingStyle(), "http://schemas.xmlsoap.org/soap/encoding/")) {
            warn("wsdlmodeler.warning.ignoringOperation.notEncoded", info.portTypeOperation.getName());
            return null;
        }
        String requestNamespaceURI = soapRequestBody.getNamespace();
        String responseNamespaceURI = soapResponseBody.getNamespace();
        if(requestNamespaceURI == null)
            throw new ModelerException("wsdlmodeler.invalid.bindingOperation.inputSoapBody.missingNamespace", new Object[] {
                info.bindingOperation.getName()
            });
        if(responseNamespaceURI == null)
            throw new ModelerException("wsdlmodeler.invalid.bindingOperation.outputSoapBody.missingNamespace", new Object[] {
                info.bindingOperation.getName()
            });
        QName requestBodyName = new QName(requestNamespaceURI, info.portTypeOperation.getName());
        SOAPStructureType requestBodyType = new RPCRequestUnorderedStructureType(requestBodyName);
        JavaStructureType requestBodyJavaType = new JavaStructureType(makePackageQualified(StringUtils.capitalize(info.uniqueOperationName) + "_RequestStruct"), false);
        requestBodyType.setJavaType(requestBodyJavaType);
        Block requestBodyBlock = new Block(requestBodyName, requestBodyType);
        request.addBodyBlock(requestBodyBlock);
        QName responseBodyName = new QName(responseNamespaceURI, info.portTypeOperation.getName() + "Response");
        SOAPStructureType responseBodyType = new RPCResponseStructureType(responseBodyName);
        JavaStructureType responseBodyJavaType = new JavaStructureType(makePackageQualified(StringUtils.capitalize(info.uniqueOperationName + "_ResponseStruct")), false);
        responseBodyType.setJavaType(responseBodyJavaType);
        Block responseBodyBlock = new Block(responseBodyName, responseBodyType);
        response.addBodyBlock(responseBodyBlock);
        if(soapRequestBody.getParts() != null) {
            warn("wsdlmodeler.warning.ignoringOperation.cannotHandleBodyPartsAttribute", info.portTypeOperation.getName());
            return null;
        }
        Message inputMessage = info.portTypeOperation.getInput().resolveMessage(info.document);
        Message outputMessage = info.portTypeOperation.getOutput().resolveMessage(info.document);
        String parameterOrder = info.portTypeOperation.getParameterOrder();
        List parameterList = null;
        boolean buildParameterList = false;
        if(parameterOrder != null) {
            parameterList = XmlUtil.parseTokenList(parameterOrder);
        } else {
            parameterList = new ArrayList();
            buildParameterList = true;
        }
        Set partNames = new HashSet();
        Set inputParameterNames = new HashSet();
        Set outputParameterNames = new HashSet();
        String resultParameterName = null;
        for(Iterator iter = inputMessage.parts(); iter.hasNext();) {
            MessagePart part = (MessagePart)iter.next();
            if(part.getDescriptorKind() != SchemaKinds.XSD_TYPE)
                throw new ModelerException("wsdlmodeler.invalid.message.partMustHaveTypeDescriptor", new Object[] {
                    inputMessage.getName(), part.getName()
                });
            partNames.add(part.getName());
            inputParameterNames.add(part.getName());
            if(buildParameterList)
                parameterList.add(part.getName());
        }

        for(Iterator iter = outputMessage.parts(); iter.hasNext();) {
            MessagePart part = (MessagePart)iter.next();
            if(part.getDescriptorKind() != SchemaKinds.XSD_TYPE)
                throw new ModelerException("wsdlmodeler.invalid.message.partMustHaveTypeDescriptor", new Object[] {
                    outputMessage.getName(), part.getName()
                });
            partNames.add(part.getName());
            if(buildParameterList && resultParameterName == null) {
                resultParameterName = part.getName();
            } else {
                outputParameterNames.add(part.getName());
                if(buildParameterList && !inputParameterNames.contains(part.getName()))
                    parameterList.add(part.getName());
            }
        }

        if(!buildParameterList) {
            String name;
            for(Iterator iter = parameterList.iterator(); iter.hasNext(); partNames.remove(name)) {
                name = (String)iter.next();
                if(!partNames.contains(name))
                    throw new ModelerException("wsdlmodeler.invalid.parameterorder.parameter", new Object[] {
                        name, info.operation.getName().getLocalPart()
                    });
            }

            if(partNames.size() > 1)
                throw new ModelerException("wsdlmodeler.invalid.parameterOrder.tooManyUnmentionedParts", new Object[] {
                    info.operation.getName().getLocalPart()
                });
            if(partNames.size() == 1)
                resultParameterName = (String)partNames.iterator().next();
        }
        if(resultParameterName == null) {
            info.operation.setProperty("operationHasVoidReturnType", Boolean.TRUE);
        } else {
            MessagePart part = outputMessage.getPart(resultParameterName);
            com.sun.xml.rpc.processor.model.soap.SOAPType soapType = _analyzer.schemaTypeToSOAPType(part.getDescriptor());
            SOAPStructureMember member = new SOAPStructureMember(new QName(null, part.getName()), soapType);
            JavaStructureMember javaMember = new JavaStructureMember(Names.validJavaMemberName(part.getName()), soapType.getJavaType(), member, false);
            javaMember.setReadMethod(Names.getJavaMemberReadMethod(javaMember));
            javaMember.setWriteMethod(Names.getJavaMemberWriteMethod(javaMember));
            member.setJavaStructureMember(javaMember);
            responseBodyType.add(member);
            responseBodyJavaType.add(javaMember);
            Parameter parameter = new Parameter(Names.validJavaMemberName(part.getName()));
            parameter.setEmbedded(true);
            parameter.setType(soapType);
            parameter.setBlock(responseBodyBlock);
            response.addParameter(parameter);
        }
        for(Iterator iter = parameterList.iterator(); iter.hasNext();) {
            String name = (String)iter.next();
            boolean isInput = inputParameterNames.contains(name);
            boolean isOutput = outputParameterNames.contains(name);
            com.sun.xml.rpc.processor.model.soap.SOAPType soapType = null;
            Parameter inParameter = null;
            if(isInput && isOutput && !inputMessage.getPart(name).getDescriptor().equals(outputMessage.getPart(name).getDescriptor()))
                throw new ModelerException("wsdlmodeler.invalid.parameter.differentTypes", new Object[] {
                    name, info.operation.getName().getLocalPart()
                });
            if(isInput) {
                MessagePart part = inputMessage.getPart(name);
                soapType = _analyzer.schemaTypeToSOAPType(part.getDescriptor());
                SOAPStructureMember member = new SOAPStructureMember(new QName(null, part.getName()), soapType);
                JavaStructureMember javaMember = new JavaStructureMember(Names.validJavaMemberName(part.getName()), soapType.getJavaType(), member, false);
                javaMember.setReadMethod(Names.getJavaMemberReadMethod(javaMember));
                javaMember.setWriteMethod(Names.getJavaMemberWriteMethod(javaMember));
                member.setJavaStructureMember(javaMember);
                requestBodyType.add(member);
                requestBodyJavaType.add(javaMember);
                inParameter = new Parameter(Names.validJavaMemberName(part.getName()));
                inParameter.setEmbedded(true);
                inParameter.setType(soapType);
                inParameter.setBlock(requestBodyBlock);
                request.addParameter(inParameter);
            }
            if(isOutput) {
                MessagePart part = outputMessage.getPart(name);
                if(soapType == null)
                    soapType = _analyzer.schemaTypeToSOAPType(part.getDescriptor());
                SOAPStructureMember member = new SOAPStructureMember(new QName(null, part.getName()), soapType);
                responseBodyType.add(member);
                JavaStructureMember javaMember = new JavaStructureMember(Names.validJavaMemberName(part.getName()), soapType.getJavaType(), member, false);
                responseBodyJavaType.add(javaMember);
                javaMember.setReadMethod(Names.getJavaMemberReadMethod(javaMember));
                javaMember.setWriteMethod(Names.getJavaMemberWriteMethod(javaMember));
                Parameter outParameter = new Parameter(Names.validJavaMemberName(part.getName()));
                outParameter.setEmbedded(true);
                outParameter.setType(soapType);
                outParameter.setBlock(responseBodyBlock);
                if(inParameter != null) {
                    inParameter.setLinkedParameter(outParameter);
                    outParameter.setLinkedParameter(inParameter);
                }
                response.addParameter(outParameter);
            }
        }

        for(Iterator iter = info.bindingOperation.faults(); iter.hasNext();) {
            BindingFault bindingFault = (BindingFault)iter.next();
            Fault portTypeFault = null;
            for(Iterator iter2 = info.portTypeOperation.faults(); iter2.hasNext();) {
                Fault aFault = (Fault)iter2.next();
                if(aFault.getName().equals(bindingFault.getName())) {
                    if(portTypeFault != null)
                        throw new ModelerException("wsdlmodeler.invalid.bindingFault.notUnique", new Object[] {
                            bindingFault.getName(), info.bindingOperation.getName()
                        });
                    portTypeFault = aFault;
                }
            }

            if(portTypeFault == null)
                throw new ModelerException("wsdlmodeler.invalid.bindingFault.notFound", new Object[] {
                    bindingFault.getName(), info.bindingOperation.getName()
                });
            com.sun.xml.rpc.processor.model.Fault fault = new com.sun.xml.rpc.processor.model.Fault(portTypeFault.getName());
            SOAPFault soapFault = (SOAPFault)getExtensionOfType(bindingFault, com.sun.xml.rpc.wsdl.document.soap.SOAPFault.class);
            if(soapFault == null)
                throw new ModelerException("wsdlmodeler.invalid.bindingFault.outputMissingSoapFault", new Object[] {
                    bindingFault.getName(), info.bindingOperation.getName()
                });
            if(soapFault.isLiteral() || !tokenListContains(soapFault.getEncodingStyle(), "http://schemas.xmlsoap.org/soap/encoding/")) {
                warn("wsdlmodeler.warning.ignoringFault.notEncoded", new Object[] {
                    bindingFault.getName(), info.bindingOperation.getName()
                });
            } else {
                String faultNamespaceURI = soapFault.getNamespace();
                if(faultNamespaceURI == null)
                    throw new ModelerException("wsdlmodeler.invalid.bindingFault.missingNamespace", new Object[] {
                        bindingFault.getName(), info.bindingOperation.getName()
                    });
                Message faultMessage = portTypeFault.resolveMessage(info.document);
                Iterator iter2 = faultMessage.parts();
                if(!iter2.hasNext())
                    throw new ModelerException("wsdlmodeler.invalid.bindingFault.emptyMessage", new Object[] {
                        bindingFault.getName(), faultMessage.getName()
                    });
                MessagePart faultPart = (MessagePart)iter2.next();
                if(iter2.hasNext())
                    throw new ModelerException("wsdlmodeler.invalid.bindingFault.messageHasMoreThanOnePart", new Object[] {
                        bindingFault.getName(), faultMessage.getName()
                    });
                if(faultPart.getDescriptorKind() != SchemaKinds.XSD_TYPE)
                    throw new ModelerException("wsdlmodeler.invalid.message.partMustHaveTypeDescriptor", new Object[] {
                        faultMessage.getName(), faultPart.getName()
                    });
                QName faultQName = new QName(faultNamespaceURI, faultPart.getName());
                com.sun.xml.rpc.processor.model.soap.SOAPType faultType = _analyzer.schemaTypeToSOAPType(faultPart.getDescriptor());
                Block faultBlock = new Block(faultQName, faultType);
                fault.setBlock(faultBlock);
                response.addFaultBlock(faultBlock);
                info.operation.addFault(fault);
            }
        }

        for(Iterator iter = info.bindingOperation.getInput().extensions(); iter.hasNext();) {
            Extension extension = (Extension)iter.next();
            if(extension instanceof SOAPHeader) {
                SOAPHeader header = (SOAPHeader)extension;
                if(header.isLiteral() || !tokenListContains(header.getEncodingStyle(), "http://schemas.xmlsoap.org/soap/encoding/")) {
                    warn("wsdlmodeler.warning.ignoringHeader.notEncoded", new Object[] {
                        header.getPart(), info.bindingOperation.getName()
                    });
                } else {
                    Message headerMessage = (Message)info.document.find(Kinds.MESSAGE, header.getMessage());
                    MessagePart part = headerMessage.getPart(header.getPart());
                    com.sun.xml.rpc.processor.model.soap.SOAPType requestHeaderType = _analyzer.schemaTypeToSOAPType(part.getDescriptor());
                    String namespaceURI = header.getNamespace();
                    if(namespaceURI == null)
                        namespaceURI = requestHeaderType.getName().getNamespaceURI();
                    QName requestHeaderName = new QName(namespaceURI, header.getPart());
                    Block requestHeaderBlock = new Block(requestHeaderName, requestHeaderType);
                    AbstractType alreadySeenHeaderType = (AbstractType)info.headers.get(requestHeaderName);
                    if(alreadySeenHeaderType != null && alreadySeenHeaderType != requestHeaderType) {
                        warn("wsdlmodeler.warning.ignoringHeader.inconsistentDefinition", new Object[] {
                            header.getPart(), info.bindingOperation.getName()
                        });
                    } else {
                        info.headers.put(requestHeaderName, requestHeaderType);
                        request.addHeaderBlock(requestHeaderBlock);
                        boolean explicitServiceContext = Boolean.valueOf(_options.getProperty("explicitServiceContext")).booleanValue();
                        if(explicitServiceContext) {
                            Parameter parameter = new Parameter(Names.validJavaMemberName(part.getName()));
                            parameter.setEmbedded(false);
                            parameter.setType(requestHeaderType);
                            parameter.setBlock(requestHeaderBlock);
                            request.addParameter(parameter);
                        }
                    }
                }
            }
        }

        for(Iterator iter = info.bindingOperation.getOutput().extensions(); iter.hasNext();) {
            Extension extension = (Extension)iter.next();
            if(extension instanceof SOAPHeader) {
                SOAPHeader header = (SOAPHeader)extension;
                if(header.isLiteral() || !tokenListContains(header.getEncodingStyle(), "http://schemas.xmlsoap.org/soap/encoding/")) {
                    warn("wsdlmodeler.warning.ignoringHeader.notEncoded", new Object[] {
                        header.getPart(), info.bindingOperation.getName()
                    });
                } else {
                    Message headerMessage = (Message)info.document.find(Kinds.MESSAGE, header.getMessage());
                    MessagePart part = headerMessage.getPart(header.getPart());
                    com.sun.xml.rpc.processor.model.soap.SOAPType responseHeaderType = _analyzer.schemaTypeToSOAPType(part.getDescriptor());
                    String namespaceURI = header.getNamespace();
                    if(namespaceURI == null)
                        namespaceURI = responseHeaderType.getName().getNamespaceURI();
                    QName responseHeaderName = new QName(namespaceURI, header.getPart());
                    Block responseHeaderBlock = new Block(responseHeaderName, responseHeaderType);
                    AbstractType alreadySeenHeaderType = (AbstractType)info.headers.get(responseHeaderName);
                    if(alreadySeenHeaderType != null && alreadySeenHeaderType != responseHeaderType) {
                        warn("wsdlmodeler.warning.ignoringHeader.inconsistentDefinition", new Object[] {
                            header.getPart(), info.bindingOperation.getName()
                        });
                    } else {
                        info.headers.put(responseHeaderName, responseHeaderType);
                        response.addHeaderBlock(responseHeaderBlock);
                        boolean explicitServiceContext = Boolean.valueOf(_options.getProperty("explicitServiceContext")).booleanValue();
                        if(explicitServiceContext) {
                            Parameter parameter = new Parameter(Names.validJavaMemberName(part.getName()));
                            parameter.setEmbedded(false);
                            parameter.setType(responseHeaderType);
                            parameter.setBlock(responseHeaderBlock);
                            response.addParameter(parameter);
                        }
                    }
                }
            }
        }

        info.operation.setRequest(request);
        info.operation.setResponse(response);
        return info.operation;
    }

    protected com.sun.xml.rpc.processor.model.Operation processSOAPOperationDocumentStyle(WSDLModeler$ProcessSOAPOperationInfo info) {
        Request request = new Request();
        Response response = new Response();
        SOAPBody soapRequestBody = (SOAPBody)getExtensionOfType(info.bindingOperation.getInput(), com.sun.xml.rpc.wsdl.document.soap.SOAPBody.class);
        if(soapRequestBody == null)
            throw new ModelerException("wsdlmodeler.invalid.bindingOperation.inputMissingSoapBody", new Object[] {
                info.bindingOperation.getName()
            });
        SOAPBody soapResponseBody = (SOAPBody)getExtensionOfType(info.bindingOperation.getOutput(), com.sun.xml.rpc.wsdl.document.soap.SOAPBody.class);
        if(soapResponseBody == null)
            throw new ModelerException("wsdlmodeler.invalid.bindingOperation.outputMissingSoapBody", new Object[] {
                info.bindingOperation.getName()
            });
        if(!soapRequestBody.isLiteral() || !soapResponseBody.isLiteral()) {
            warn("wsdlmodeler.warning.ignoringOperation.notLiteral", info.portTypeOperation.getName());
            return null;
        }
        Message inputMessage = info.portTypeOperation.getInput().resolveMessage(info.document);
        Message outputMessage = info.portTypeOperation.getOutput().resolveMessage(info.document);
        if(soapRequestBody.getParts() != null) {
            warn("wsdlmodeler.warning.ignoringOperation.cannotHandleBodyPartsAttribute", info.portTypeOperation.getName());
            return null;
        }
        Set partNames = new HashSet();
        Set inputParameterNames = new HashSet();
        Set outputParameterNames = new HashSet();
        String resultParameterName = null;
        List parameterList = new ArrayList();
        boolean gotOne = false;
        for(Iterator iter = inputMessage.parts(); iter.hasNext();) {
            if(gotOne) {
                warn("wsdlmodeler.warning.ignoringOperation.cannotHandleMoreThanOnePartInInputMessage", info.portTypeOperation.getName());
                return null;
            }
            MessagePart part = (MessagePart)iter.next();
            if(part.getDescriptorKind() != SchemaKinds.XSD_ELEMENT) {
                warn("wsdlmodeler.warning.ignoringOperation.cannotHandleTypeMessagePart", info.portTypeOperation.getName());
                return null;
            }
            partNames.add(part.getName());
            inputParameterNames.add(part.getName());
            parameterList.add(part.getName());
            gotOne = true;
        }

        if(!gotOne) {
            warn("wsdlmodeler.warning.ignoringOperation.cannotHandleEmptyInputMessage", info.portTypeOperation.getName());
            return null;
        }
        gotOne = false;
        for(Iterator iter = outputMessage.parts(); iter.hasNext();) {
            if(gotOne) {
                warn("wsdlmodeler.warning.ignoringOperation.cannotHandleMoreThanOnePartInOutputMessage", info.portTypeOperation.getName());
                return null;
            }
            MessagePart part = (MessagePart)iter.next();
            if(part.getDescriptorKind() != SchemaKinds.XSD_ELEMENT) {
                warn("wsdlmodeler.warning.ignoringOperation.cannotHandleTypeMessagePart", info.portTypeOperation.getName());
                return null;
            }
            partNames.add(part.getName());
            outputParameterNames.add(part.getName());
            if(!inputParameterNames.contains(part.getName()))
                parameterList.add(part.getName());
            gotOne = true;
        }

        if(!gotOne) {
            warn("wsdlmodeler.warning.ignoringOperation.cannotHandleEmptyOutputMessage", info.portTypeOperation.getName());
            return null;
        }
        for(Iterator iter = parameterList.iterator(); iter.hasNext();) {
            String name = (String)iter.next();
            boolean isInput = inputParameterNames.contains(name);
            boolean isOutput = outputParameterNames.contains(name);
            if(isInput) {
                MessagePart part = inputMessage.getPart(name);
                com.sun.xml.rpc.processor.model.literal.LiteralType literalType = _analyzer.schemaElementTypeToLiteralType(part.getDescriptor());
                Block block = new Block(part.getDescriptor(), literalType);
                request.addBodyBlock(block);
                if(literalType instanceof LiteralSequenceType) {
                    LiteralSequenceType sequenceType = (LiteralSequenceType)literalType;
                    Parameter parameter;
                    for(Iterator iter2 = sequenceType.getAttributeMembers(); iter2.hasNext(); request.addParameter(parameter)) {
                        LiteralAttributeMember attribute = (LiteralAttributeMember)iter2.next();
                        parameter = new Parameter(attribute.getJavaStructureMember().getName());
                        parameter.setEmbedded(true);
                        parameter.setType(attribute.getType());
                        parameter.setBlock(block);
                    }

                    Parameter parameter2;
                    for(Iterator iter2 = sequenceType.getElementMembers(); iter2.hasNext(); request.addParameter(parameter2)) {
                        LiteralElementMember element = (LiteralElementMember)iter2.next();
                        parameter2 = new Parameter(element.getJavaStructureMember().getName());
                        parameter2.setEmbedded(true);
                        if(element.isRepeated()) {
                            LiteralArrayType arrayType = new LiteralArrayType();
                            arrayType.setName(new QName("synthetic-array-type"));
                            arrayType.setElementType(element.getType());
                            JavaArrayType javaArrayType = new JavaArrayType(element.getType().getJavaType().getName() + "[]");
                            javaArrayType.setElementType(element.getType().getJavaType());
                            arrayType.setJavaType(javaArrayType);
                            parameter2.setType(arrayType);
                        } else {
                            parameter2.setType(element.getType());
                        }
                        parameter2.setBlock(block);
                    }

                } else {
                    Parameter parameter = new Parameter(Names.validJavaMemberName(part.getName()));
                    parameter.setEmbedded(false);
                    parameter.setType(literalType);
                    parameter.setBlock(block);
                    request.addParameter(parameter);
                }
            }
            if(isOutput) {
                MessagePart part = outputMessage.getPart(name);
                com.sun.xml.rpc.processor.model.literal.LiteralType literalType = _analyzer.schemaElementTypeToLiteralType(part.getDescriptor());
                Block block = new Block(part.getDescriptor(), literalType);
                response.addBodyBlock(block);
                if((literalType instanceof LiteralStructuredType) && ((LiteralStructuredType)literalType).getElementMembersCount() + ((LiteralStructuredType)literalType).getAttributeMembersCount() == 1) {
                    LiteralStructuredType structuredType = (LiteralStructuredType)literalType;
                    JavaStructureType javaStructureType = (JavaStructureType)structuredType.getJavaType();
                    Iterator iter2 = structuredType.getAttributeMembers();
                    if(iter2.hasNext()) {
                        LiteralAttributeMember attribute = (LiteralAttributeMember)iter2.next();
                        Parameter parameter = new Parameter(attribute.getJavaStructureMember().getName());
                        parameter.setEmbedded(true);
                        parameter.setType(attribute.getType());
                        parameter.setBlock(block);
                        response.addParameter(parameter);
                    } else {
                        iter2 = structuredType.getElementMembers();
                        LiteralElementMember element = (LiteralElementMember)iter2.next();
                        Parameter parameter = new Parameter(element.getJavaStructureMember().getName());
                        parameter.setEmbedded(true);
                        if(element.isRepeated()) {
                            LiteralArrayType arrayType = new LiteralArrayType();
                            arrayType.setName(new QName("synthetic-array-type"));
                            arrayType.setElementType(element.getType());
                            JavaArrayType javaArrayType = new JavaArrayType(element.getType().getJavaType().getName() + "[]");
                            javaArrayType.setElementType(element.getType().getJavaType());
                            arrayType.setJavaType(javaArrayType);
                            parameter.setType(arrayType);
                        } else {
                            parameter.setType(element.getType());
                        }
                        parameter.setBlock(block);
                        response.addParameter(parameter);
                    }
                } else {
                    Parameter parameter = new Parameter(Names.validJavaMemberName(part.getName()));
                    parameter.setEmbedded(false);
                    parameter.setType(literalType);
                    parameter.setBlock(block);
                    response.addParameter(parameter);
                }
            }
        }

        BindingFault bindingFault;
        for(Iterator iter = info.bindingOperation.faults(); iter.hasNext(); warn("wsdlmodeler.warning.ignoringFault.documentOperation", new Object[] {
    bindingFault.getName(), info.bindingOperation.getName()
}))
            bindingFault = (BindingFault)iter.next();

        for(Iterator iter = info.bindingOperation.getInput().extensions(); iter.hasNext();) {
            Extension extension = (Extension)iter.next();
            if(extension instanceof SOAPHeader) {
                SOAPHeader header = (SOAPHeader)extension;
                warn("wsdlmodeler.warning.ignoringHeader", new Object[] {
                    header.getPart(), info.bindingOperation.getName()
                });
            }
        }

        for(Iterator iter = info.bindingOperation.getOutput().extensions(); iter.hasNext();) {
            Extension extension = (Extension)iter.next();
            if(extension instanceof SOAPHeader) {
                SOAPHeader header = (SOAPHeader)extension;
                warn("wsdlmodeler.warning.ignoringHeader", new Object[] {
                    header.getPart(), info.bindingOperation.getName()
                });
            }
        }

        info.operation.setRequest(request);
        info.operation.setResponse(response);
        return info.operation;
    }

    protected void createJavaInterfaceForPort(com.sun.xml.rpc.processor.model.Port port, String suffix) {
        JavaInterface intf = new JavaInterface(makePackageQualified(getNonQualifiedNameOfInterfaceFor(port, suffix)));
        Set methodNames = new HashSet();
        Set methodSignatures = new HashSet();
        com.sun.xml.rpc.processor.model.Operation operation;
        for(Iterator iter = port.getOperations(); iter.hasNext(); createJavaMethodForOperation(port, operation, intf, methodNames, methodSignatures, suffix))
            operation = (com.sun.xml.rpc.processor.model.Operation)iter.next();

        port.setJavaInterface(intf);
    }

    private void createJavaMethodForOperation(com.sun.xml.rpc.processor.model.Port port, com.sun.xml.rpc.processor.model.Operation operation, JavaInterface intf, Set methodNames, Set methodSignatures, String suffix) {
        String candidateName = Names.validJavaMemberName(operation.getName().getLocalPart());
        JavaMethod method = new JavaMethod(candidateName);
        Request request = operation.getRequest();
        Block requestBlock = (Block)request.getBodyBlocks().next();
        Response response = operation.getResponse();
        Block responseBlock = (Block)response.getBodyBlocks().next();
        String signature = candidateName;
        for(Iterator iter = request.getParameters(); iter.hasNext();) {
            Parameter parameter = (Parameter)iter.next();
            if(parameter.getJavaParameter() != null)
                throw new ModelerException("wsdlmodeler.invalidOperation", operation.getName().getLocalPart());
            JavaType parameterType = parameter.getType().getJavaType();
            JavaParameter javaParameter = new JavaParameter(Names.validJavaMemberName(parameter.getName()), parameterType, parameter, parameter.getLinkedParameter() != null);
            method.addParameter(javaParameter);
            parameter.setJavaParameter(javaParameter);
            signature = signature + "%" + parameterType.getName();
        }

        String operationName = candidateName;
        if(methodSignatures.contains(signature)) {
            operationName = makeNameUniqueInSet(candidateName, methodNames);
            method.setName(operationName);
        }
        methodSignatures.add(signature);
        methodNames.add(method.getName());
        boolean operationHasVoidReturnType = operation.getProperty("operationHasVoidReturnType") != null;
        Parameter resultParameter = null;
        for(Iterator iter = response.getParameters(); iter.hasNext();)
            if(!operationHasVoidReturnType && resultParameter == null) {
                resultParameter = (Parameter)iter.next();
                if(resultParameter.getJavaParameter() != null)
                    throw new ModelerException("wsdlmodeler.invalidOperation", operation.getName().getLocalPart());
                if(resultParameter.getLinkedParameter() != null)
                    throw new ModelerException("wsdlmodeler.resultIsInOutParameter", operation.getName().getLocalPart());
                if(resultParameter.getBlock() != responseBlock)
                    throw new ModelerException("wsdlmodeler.invalidOperation", operation.getName().getLocalPart());
                JavaType returnType = resultParameter.getType().getJavaType();
                method.setReturnType(returnType);
            } else {
                Parameter parameter = (Parameter)iter.next();
                if(parameter.getJavaParameter() != null)
                    throw new ModelerException("wsdlmodeler.invalidOperation", operation.getName().getLocalPart());
                JavaParameter javaParameter = null;
                if(parameter.getLinkedParameter() != null)
                    javaParameter = parameter.getLinkedParameter().getJavaParameter();
                JavaType parameterType = parameter.getType().getJavaType();
                parameterType.setHolder(true);
                if(javaParameter == null)
                    javaParameter = new JavaParameter(Names.validJavaMemberName(parameter.getName()), parameterType, parameter, true);
                parameter.setJavaParameter(javaParameter);
                if(parameter.getLinkedParameter() == null)
                    method.addParameter(javaParameter);
            }

        if(operationHasVoidReturnType)
            method.setReturnType(ModelerConstants.VOID_JAVATYPE);
        operation.setJavaMethod(method);
        intf.addMethod(method);
        JavaException javaException;
        for(Iterator iter = operation.getFaults(); iter.hasNext(); method.addException(javaException.getName())) {
            com.sun.xml.rpc.processor.model.Fault fault = (com.sun.xml.rpc.processor.model.Fault)iter.next();
            String exceptionName = makePackageQualified(Names.validJavaClassName(getNonQualifiedNameOfInterfaceFor(port, suffix) + "_" + StringUtils.capitalize(operationName) + "_" + StringUtils.capitalize(fault.getName())));
            String propertyName = Names.validJavaMemberName(fault.getName());
            javaException = new JavaException(exceptionName, propertyName, fault.getBlock().getType().getJavaType(), false);
            fault.setJavaException(javaException);
        }

    }

    protected BatchEnvironment getEnvironment() {
        return _modelInfo.getParent().getEnvironment();
    }

    protected void warn(String key) {
        getEnvironment().warn(_messageFactory.getMessage(key));
    }

    protected void warn(String key, String arg) {
        getEnvironment().warn(_messageFactory.getMessage(key, arg));
    }

    protected void warn(String key, Object args[]) {
        getEnvironment().warn(_messageFactory.getMessage(key, args));
    }

    protected void info(String key) {
        getEnvironment().info(_messageFactory.getMessage(key));
    }

    protected void info(String key, String arg) {
        getEnvironment().info(_messageFactory.getMessage(key, arg));
    }

    protected String makePackageQualified(String s) {
        if(_modelInfo.getJavaPackageName() != null && !_modelInfo.getJavaPackageName().equals(""))
            return _modelInfo.getJavaPackageName() + "." + s;
        else
            return s;
    }

    protected QName makePackageQualified(QName name) {
        return new QName(name.getNamespaceURI(), makePackageQualified(name.getLocalPart()));
    }

    protected String makeNameUniqueInSet(String candidateName, Set names) {
        String baseName = candidateName;
        String name = baseName;
        for(int i = 2; names.contains(name); i++)
            name = baseName + Integer.toString(i);

        return name;
    }

    protected String getUniqueName(Operation operation, boolean hasOverloadedOperations) {
        if(hasOverloadedOperations)
            return operation.getUniqueKey().replace(' ', '_');
        else
            return operation.getName();
    }

    protected String getNonQualifiedNameOfInterfaceFor(com.sun.xml.rpc.processor.model.Port port, String suffix) {
        return Names.validJavaClassName(port.getName().getLocalPart() + suffix);
    }

    protected static void setDocumentationIfPresent(ModelObject obj, Documentation documentation) {
        if(documentation != null && documentation.getContent() != null)
            obj.setProperty("wsdlDocumentation", documentation.getContent());
    }

    protected static QName getQNameOf(GloballyKnown entity) {
        return new QName(entity.getDefining().getTargetNamespaceURI(), entity.getName());
    }

    protected static Extension getExtensionOfType(Extensible extensible, Class type) {
        for(Iterator iter = extensible.extensions(); iter.hasNext();) {
            Extension extension = (Extension)iter.next();
            if(extension.getClass().equals(type))
                return extension;
        }

        return null;
    }

    protected static boolean tokenListContains(String tokenList, String target) {
        if(tokenList == null)
            return false;
        for(StringTokenizer tokenizer = new StringTokenizer(tokenList, " "); tokenizer.hasMoreTokens();) {
            String s = tokenizer.nextToken();
            if(target.equals(s))
                return true;
        }

        return false;
    }
}
