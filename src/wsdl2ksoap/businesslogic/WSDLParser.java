/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wsdl2ksoap.businesslogic;

import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import wsdl2ksoap.datatypes.Function;
import wsdl2ksoap.datatypes.PropertyContainer;
import wsdl2ksoap.datatypes.SoapClass;
import wsdl2ksoap.datatypes.SoapClass.ClassType;
import wsdl2ksoap.datatypes.SoapClassProperty;

/**
 *
 * @author newky
 */
public class WSDLParser
{
    public static boolean ProcessWSDL()
    {
        try
            {
            

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(PropertyContainer.WSDLAddress);

            doc.getDocumentElement ().normalize ();

            PropertyContainer.Namespace = doc.getDocumentElement().getAttribute("targetNamespace");


//            // normalize text representation
//            doc.getDocumentElement ().normalize ();
//            System.out.println ("Root element of the doc is " +
//                 doc.getDocumentElement().getNodeName());
//
//
//            System.out.println ("First element of the doc is " +
//                 doc.getFirstChild().getNodeName());
//
//            System.out.println("Total no of people : " + nodes.getLength());

            NodeList nodes = doc.getElementsByTagName("wsdl:service");
            Node serviceNode = nodes.item(0);

            if(serviceNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element serviceElement = (Element)serviceNode;

                //serviceNode
                PropertyContainer.ServiceName = serviceElement.getAttribute("name");

                System.out.println("Service Name: " + PropertyContainer.ServiceName);

                //Get Bindings
                NodeList bindings = doc.getElementsByTagName("wsdl:binding");

                //System.out.println("Total no of bindings : " + bindings.getLength());
                //get binding and then the Methods
                for(int s=0; s<bindings.getLength() ; s++)
                {
                    Node bindingNode = bindings.item(s);
                    if(bindingNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element bindingElement = (Element)bindingNode;

                        if (bindingElement.getAttribute("name").equals(PropertyContainer.GetSoapPortName()))
                        {
                            String portTypeNS = bindingElement.getAttribute("type").replaceFirst("tns:", "");

                            //System.out.println("Type : " + portTypeNS);

                            //get operations
                            NodeList operations = bindingElement.getElementsByTagName("wsdl:operation");

                            //System.out.println("Total no of bindings : " + operations.getLength());

                            //instatiate the Function array now we know how many functions there are.
                            PropertyContainer.Functions = new Function[operations.getLength()];


                            //Loop through Functions
                            for(int op=0; op<operations.getLength() ; op++)
                            {
                                  Node operationNode = operations.item(op);


                                 if(bindingNode.getNodeType() == Node.ELEMENT_NODE)
                                 {
                                     //get element
                                     Element operationElement = (Element)operationNode;

                                     //create new instance
                                     Function newFunc = new Function();
                                     newFunc.Name = operationElement.getAttribute("name");

                                     //get operation list and element
                                     NodeList soapOpList = operationElement.getElementsByTagName("soap:operation");
                                     Element soapOpElement = (Element)soapOpList.item(0);

                                     newFunc.SoapAction = soapOpElement.getAttribute("soapAction");

                                     //get port types
                                     NodeList portTypes = doc.getElementsByTagName("wsdl:portType");


                                     //loop through elements
                                    for(int pt=0; pt<portTypes.getLength() ; pt++)
                                    {
                                        Node portTypeNode = portTypes.item(pt);

                                        if(portTypeNode.getNodeType() == Node.ELEMENT_NODE)
                                        {
                                            Element portTypeElement = (Element)portTypeNode;

                                            //check to see if it is the same as the current operation
                                            if (portTypeElement.getAttribute("name").equals(portTypeNS))
                                            {
                                                //get the operations for this portType
                                                NodeList operationList = portTypeElement.getElementsByTagName("wsdl:operation");

                                                //loop through operation list
                                                for(int ol=0; ol<operationList.getLength() ; ol++)
                                                {
                                                    Node operationListNode = operationList.item(ol);

                                                    if(operationListNode.getNodeType() == Node.ELEMENT_NODE)
                                                    {
                                                        Element operationListElement = (Element)operationListNode;

                                                        //see if the element matches the function anem
                                                        if (operationListElement.getAttribute("name").equals(newFunc.Name))
                                                        {
                                                            //is this function - get input parameter type
                                                            NodeList inputList = operationListElement.getElementsByTagName("wsdl:input");
                                                            String inputMessage = ((Element)inputList.item(0)).getAttribute("message").replaceFirst("tns:", "");

                                                            //is this function - get return type
                                                            NodeList outputList = operationListElement.getElementsByTagName("wsdl:output");
                                                            String outputMessage = ((Element)outputList.item(0)).getAttribute("message").replaceFirst("tns:", "");

//                                                            

                                                            //now need to get the Types of the return and property value
                                                            //get port types
                                                            NodeList messages = doc.getElementsByTagName("wsdl:message");

                                                            for(int ml=0; ml<messages.getLength() ; ml++)
                                                            {
                                                                Node messageNode = messages.item(ml);

                                                                if(messageNode.getNodeType() == Node.ELEMENT_NODE)
                                                                {
                                                                    //convert to element
                                                                    Element messageElement = (Element)messageNode;

                                                                    //compare against inputMessage name
                                                                    if (messageElement.getAttribute("name").equals(inputMessage))
                                                                    {

                                                                        newFunc.InputType = ((Element)messageElement.getElementsByTagName("wsdl:part").item(0)).getAttribute("element").replaceFirst("tns:", "");

                                                                        //System.out.println("In " + newFunc.InputType);

                                                                    }
                                                                    else //compare against inputMessage name
                                                                    if (messageElement.getAttribute("name").equals(outputMessage))
                                                                    {
                                                                        //get response type
                                                                        //get input param type
                                                                        newFunc.OutputType = ((Element)messageElement.getElementsByTagName("wsdl:part").item(0)).getAttribute("element").replaceFirst("tns:", "");

                                                                        
                                                                    }

                                                                }

                                                            }



                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }

                                     //add newFunc to functions array
                                     PropertyContainer.Functions[op] = newFunc;

                                 }
                            }



                        }


                    }

                }

            }

            
            // now process the datatypes and classes
            NodeList typenodes = doc.getElementsByTagName("wsdl:types");
            Node typesNode = typenodes.item(0);

            if(serviceNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element typesElement = (Element)typesNode;

                 //now get schema element
                Node typeschemaNode = typesElement.getElementsByTagName("s:schema").item(0);

                //serviceElement.getAttribute("name");
                if(typeschemaNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element typeschemaElement = (Element)typeschemaNode;

                    //get elements
                    NodeList elementNodes = typeschemaElement.getElementsByTagName("s:element");

                    //get complexTypes
                    NodeList complexTypesNodes = typeschemaElement.getElementsByTagName("s:complexType");

                    //process element nodes and get class information

                    //create Classes container - make it big
                    //PropertyContainer.Classes = new ArrayList<SoapClass>();


                    //iterate through s:element objects
                    for (int elLoop = 0;elLoop < elementNodes.getLength();elLoop++)
                    {
                        Node elementNode = elementNodes.item(elLoop);

                        if(elementNode.getNodeType() == Node.ELEMENT_NODE)
                        {
                            Element elementElement = (Element)elementNode;

                            //get header elements from list - as the list contains all s:elements from the schema nodes



                            if (elementNode.hasChildNodes())
                            {
                                //check to see not has name

                                
                                    SoapClass newClass = new SoapClass(elementElement.getAttribute("name"));

                                    //set classtype to unknown
                                    newClass.Type = ClassType.Unknown;
                                    //now get properties from class
                                    //get elements
                                    NodeList propertyNodes = elementElement.getElementsByTagName("s:element");

                                    //iterate through properties
                                    for (int propLoop = 0; propLoop < propertyNodes.getLength();propLoop++)
                                    {
                                        Node propertyNode = propertyNodes.item(propLoop);

                                        if(propertyNode.getNodeType() == Node.ELEMENT_NODE)
                                        {
                                            Element propertyElement = (Element)propertyNode;

                                            //create new property class
                                            SoapClassProperty newProp = new SoapClassProperty(propertyElement.getAttribute("name"));
                                            newProp.SetPropertyClassType(propertyElement.getAttribute("type"));

                                            //check to see if is array of objects
                                            if (propertyElement.getAttribute("maxOccurs").equals("unbounded"))
                                            {
                                                //yes is array
                                                newProp.SetIsArray(true);
                                                
                                            }
                                            
                                            newClass.Properties.add(newProp);

                                        }

                                    }



                                    System.out.println("Element Class: " + newClass.Name + " Properties: " + newClass.Properties.size());

                                    

                                    PropertyContainer.Classes.add(newClass);
                                    
                                   
                                
                                
                            }
                        }

                    }

                    //iterate through s:comlextypes objects
                    for (int ctLoop = 0;ctLoop < complexTypesNodes.getLength();ctLoop++)
                    {
                        Node ctypeNode = complexTypesNodes.item(ctLoop);

                        if(ctypeNode.getNodeType() == Node.ELEMENT_NODE)
                        {
                            Element ctypeElement = (Element)ctypeNode;

                            //get header elements from list - as the list contains all s:elements from the schema nodes
                            if (ctypeNode.hasChildNodes())
                            {
                                //s:elements show up for some reason but have no name
                                if (!ctypeElement.getAttribute("name").isEmpty())
                                {
                                    SoapClass newClass = new SoapClass(ctypeElement.getAttribute("name"));

                                    //set class type to complex type
                                    newClass.Type = ClassType.ComplexType;

                                    //check for base super class
                                    //NodeList spBaseNode = ctypeElement.getChildNodes();
                                    NodeList spBaseNode = ctypeElement.getElementsByTagName("s:extension");
                                    //Node ctNode = spBaseNode.item(1);

                                    if (spBaseNode.getLength() != 0)
                                    {
                                        
                                        Node sbBaseNode = spBaseNode.item(0);

                                        if(sbBaseNode.getNodeType() == Node.ELEMENT_NODE)
                                        {
                                            Element sbBaseElement = (Element)sbBaseNode;
                                            
                                            newClass.SuperClassType = sbBaseElement.getAttribute("base").replaceAll("tns:", "");
  
                                        }
                                    }

                                    System.out.println("SuperClass: " + newClass.SuperClassType);
                                    
                                     //get elements
                                    NodeList propertyNodes = ctypeElement.getElementsByTagName("s:element");

                                    //iterate through properties
                                    for (int propLoop = 0; propLoop < propertyNodes.getLength();propLoop++)
                                    {
                                        Node propertyNode = propertyNodes.item(propLoop);

                                        if(propertyNode.getNodeType() == Node.ELEMENT_NODE)
                                        {
                                            Element propertyElement = (Element)propertyNode;

                                            //create new property class
                                            SoapClassProperty newProp = new SoapClassProperty(propertyElement.getAttribute("name"));
                                            newProp.SetPropertyClassType(propertyElement.getAttribute("type"));


                                            //check to see if is array of objects
                                            if (propertyElement.getAttribute("maxOccurs").equals("unbounded"))
                                            {
                                                //yes is array
                                                newProp.SetIsArray(true);
                                                newClass.ElementType = newProp.getPropertyClassType();
                                                newClass.isArray = true;
                                            }

                                            newClass.Properties.add(newProp);

                                        }

                                    }

                                    System.out.println("Complex Type: " + newClass.Name + " Properties: " + newClass.Properties.size());

                                    PropertyContainer.ComplexTypes.add(newClass);

                                }

                            }
                        }

                    }

                    //all class should have been created now - trim array
                    System.out.println("Class Count: " + PropertyContainer.Classes.size());
                    System.out.println("Complex Types: " + PropertyContainer.ComplexTypes.size());


                }


            }


            


            }
            catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line "
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        t.printStackTrace ();
        }

       return true;
    }
}
