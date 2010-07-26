/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wsdl2ksoap.businesslogic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import wsdl2ksoap.datatypes.Function;
import wsdl2ksoap.datatypes.PropertyContainer;
import wsdl2ksoap.datatypes.SoapClass;
import wsdl2ksoap.datatypes.SoapClassProperty;

/**
 *
 * @author newky
 */
public class ClassProcessor
{
    static public void CreateServiceClass(String packagename) throws Exception
    {
            //retrieve class
            InputStream file = ClassProcessor.class.getResourceAsStream("resources/ClassTemplate.txt");


            if (file != null) {
                System.out.println("Woohoo");

                String classText = FileHelper.getContents(file);

                file.close();

                //find packname string and replace it
                classText = classText.replaceAll("%%PACKAGENAME%%", packagename);

                //now find classname and replace that with the service name add soap to name for clarity
                classText = classText.replaceAll("%%CLASSNAME%%", PropertyContainer.ServiceName + "Soap");

                //load service imports
                InputStream impFile = ClassProcessor.class.getResourceAsStream("resources/ServiceImportsTemplate.txt");

                if (impFile != null) {
                    //replace imports place holder
                    classText = classText.replace("%%IMPORTS%%", FileHelper.getContents(impFile));
                    impFile.close();

                } else {
                    throw new Exception("unable loads methods template");
                }




                InputStream methFile = ClassProcessor.class.getResourceAsStream("resources/MethodTemplate.txt");
                String blankfuncText = FileHelper.getContents(methFile);


                String methText = "";

                if (methFile != null) {
                    methFile.close();

                    // add methods
                    for (int methLoop = 0; methLoop < PropertyContainer.Functions.length; methLoop++) {
                        //get current Function
                        Function curFunc = PropertyContainer.Functions[methLoop];

                        String funcText = blankfuncText;

                        //replace method name
                        funcText = funcText.replaceAll("%%METHODNAME%%", curFunc.Name);

                        //replace input param type
                        funcText = funcText.replaceAll("%%INPUT%%", curFunc.InputType);

                        //replace return type
                        funcText = funcText.replaceAll("%%OUTPUT%%", curFunc.OutputType);

                        methText = methText + funcText + "\n";

                    }

                    //replace Methods place holder with methText

                    classText = classText.replace("%%METHODS%%", methText);


                } else {
                    throw new Exception("unable loads methods template");
                }


                //Get full path
                String filePath = FileHelper.GetOutputFolderPath() + "/" + PropertyContainer.ServiceName + "Soap" + ".java";

                //write to file
                if (!FileHelper.WriteClassTextToFile(filePath, classText)) {
                    throw new Exception("unable to create service file");
                }

            } else {
                throw new Exception("service template file not found");
            }
        
    }

    /*
     * Create and modifies the BaseObject class
     */
    public static void CreateBaseObjectFile(String packagename) throws Exception
    {
        InputStream file = ClassProcessor.class.getResourceAsStream("resources/BaseObject.txt");


        if (file != null) {
            String classText = FileHelper.getContents(file);

            file.close();

            //replace package place holder
            classText = classText.replaceAll("%%PACKAGENAME%%", packagename);

            //replace namespace placeholder with value from wsdl
            classText = classText.replaceAll("%%NAMESPACE%%", PropertyContainer.Namespace);

            //now save to folder
            String filePath = FileHelper.GetOutputFolderPath() + "/BaseObject.java";
            FileHelper.WriteClassTextToFile(filePath, classText);
        }
        else
        {
         throw new Exception("could not locatie BaseObject template");
        }


    }

     /*
     * Create and modifies the BaseObject class
     */
    public static void CreateLiteralVectorArrayFile(String packagename) throws Exception
    {
        InputStream file = ClassProcessor.class.getResourceAsStream("resources/LiteralArrayVector.txt");


        if (file != null) {
            String classText = FileHelper.getContents(file);

            file.close();

            //replace package place holder
            classText = classText.replaceAll("%%PACKAGENAME%%", packagename);

            //now save to folder
            String filePath = FileHelper.GetOutputFolderPath() + "/LiteralArrayVector.java";
            FileHelper.WriteClassTextToFile(filePath, classText);
        }
        else
        {
         throw new Exception("could not locatie LiteralArrayVector template");
        }


    }

    public static void CreateClasess(String packagename) throws Exception
    {
        //split classes into Unknowns and Complextypes

        //create complex types
        InputStream file = ClassProcessor.class.getResourceAsStream("resources/SoapComplexTypeClassTemplate.txt");


        if (file != null)
        {
            String blankText = FileHelper.getContents(file);
            file.close();
            
            //loop through class and sort into types
            for (SoapClass spClass : PropertyContainer.ComplexTypes)
            {

                if (spClass.isArray == true)
                {
                    InputStream litFile = ClassProcessor.class.getResourceAsStream("resources/ArrayObjectTemplate.txt");


                    if (litFile != null) {
                        String classText = FileHelper.getContents(litFile);

                        litFile.close();

                        //replace package place holder
                        classText = classText.replaceAll("%%PACKAGENAME%%", packagename);

                        //now find classname and replace that with the class name
                        classText = classText.replaceAll("%%CLASSNAME%%", spClass.Name);

                        //now find element type placeholder and replace that with the element type
                        classText = classText.replaceAll("%%ELEMENTTYPE%%", spClass.ElementType);

                        //now save to folder
                        String filePath = FileHelper.GetOutputFolderPath() + "/" + spClass.Name + ".java";
                        FileHelper.WriteClassTextToFile(filePath, classText);
                    }
                    else
                    {
                     throw new Exception("could not locate Array Object Template");
                    }
                }
                else
                {
                    String classText = blankText;

                    //replace package place holder
                    classText = classText.replaceAll("%%PACKAGENAME%%", packagename);

                    //now find classname and replace that with the class name
                    classText = classText.replaceAll("%%CLASSNAME%%", spClass.Name);

                     //set superclas
                    //classText = classText.replaceAll("%%SUPERCLASS%%",spClass.SuperClassType);

                    //build properties insert
                    String propText = "";
                    String getPropText = "";
                    String getPropInfoText = "";
                    String setPropText = "";
                    String regTypes = "";
                    int caseCount = 0;


                    //need to build up properties from base class if not soapobject
                    List<SoapClassProperty> propertyArray =  new ArrayList<SoapClassProperty>();


                    if (!spClass.SuperClassType.equals("BaseObject"))
                    {
                         //if base object is not default load properties from superclass first then

                        //find the super class
                        SoapClass superClass = PropertyContainer.GetClassWithName(spClass.SuperClassType);

                        if (superClass != null)
                        {
                            propertyArray.addAll(superClass.Properties);
                        }
                    }
                    
                    //now load the properties from this class
                    propertyArray.addAll(spClass.Properties);

                    //set property count
                    classText = classText.replaceAll("%%PROPCOUNT%%", String.format("%d", propertyArray.size()));

                    for (SoapClassProperty prop : propertyArray)
                    {

                        if (!prop.getIsArray())
                        {
                            propText += String.format("     public %s %s;", prop.getPropertyClassType(),prop.getPropertyName()) + "\n";
                        }
                        else
                        {
                             propText += String.format("     //array \n");
                             propText += String.format("     public %s %s;", prop.getPropertyClassType(),prop.getPropertyName()) + "\n";
                        }

                        //prop case text
                        getPropText += String.format("           case %d: \n", caseCount);
                        getPropText += String.format("                return %s; \n",prop.getPropertyName());

                        //prop info text
                        getPropInfoText += String.format("           case %d: \n", caseCount);
                        getPropInfoText += String.format("                info.name = \"%s\"; \n",prop.getPropertyName());
                        getPropInfoText += String.format("                info.type = %s; \n",getClassTypeRetrievalString(prop.getPropertyName(),prop.getPropertyClassType()));
                        getPropInfoText += String.format("                             break; \n","");

                        //set prop text
                        setPropText += String.format("           case %d: \n", caseCount);
                        setPropText += String.format("                %s = %s; \n",prop.getPropertyName(),getConvertorForType(prop.getPropertyClassType()));
                        setPropText += String.format("                  break; \n","");



                        if (prop.getIsComplexType())
                        {

                            //get the class for this complex type and then check to see if its an

                            regTypes +=  String.format("           new %s().register(envelope); \n",prop.getPropertyClassType());
                        }

                        caseCount++;

                    }

                    //set property count
                    classText = classText.replaceAll("%%PROPERTIES%%", propText);



                    //set getProperty text
                    classText = classText.replaceAll("%%GETPROPERTY%%", getPropText);

                    //set getProperty text
                    classText = classText.replaceAll("%%GETPROPINFO%%", getPropInfoText);

                    //set setProperty text
                    classText = classText.replaceAll("%%SETPROP%%", setPropText);

                    //set setProperty text
                    classText = classText.replaceAll("%%REGISTERTYPES%%", regTypes);

                    //write to file
                    String filePath = FileHelper.GetOutputFolderPath() + "/" + spClass.Name + ".java";
                    FileHelper.WriteClassTextToFile(filePath, classText);

                }
            }
        }

    }

    private static String getConvertorForType(String propType)
    {
        if (propType.equals("boolean"))
        {
            return String.format("Boolean.getBoolean(value.toString())", propType);
        }
        else if (propType.equals("int"))
        {
            return String.format("Integer.parseInt(value.toString())", propType);
        }
        else
        {
            return String.format("(%s)value", propType);
        }
        //return "value";
    }

    private static String getClassTypeRetrievalString(String propName, String propType)
    {
       if (propType.equals("boolean"))
        {
            return String.format("PropertyInfo.BOOLEAN_CLASS", propType);
        }
        else if (propType.equals("int"))
        {
            return String.format("PropertyInfo.INTEGER_CLASS", propType);
        }
        else
        {
            return String.format("new %s().getClass()",propType);
        }
    }
    public static void CreateFunctionClasses(String packageName) throws Exception
    {
        //work throug functions and

        for (Function fn : PropertyContainer.Functions)
        {
            //for each function get the parameter class and the return type and create the classes

            SoapClass paramClass = PropertyContainer.GetClassWithName(fn.InputType);
            SoapClass returnClass = PropertyContainer.GetClassWithName(fn.OutputType);

            

            if (paramClass != null)
            {
                InputStream file = ClassProcessor.class.getResourceAsStream("resources/ParameterClassTemplate.txt");
                
                //set up paramClass
                if (file != null )
                {
                    String classText = FileHelper.getContents(file);

                    file.close();

                    //replace package place holder
                    classText = classText.replaceAll("%%PACKAGENAME%%", packageName);

                    //now find classname and replace that with the class name
                    classText = classText.replaceAll("%%CLASSNAME%%", paramClass.Name);

                    //now find soapmethodname placeholder and replace
                    //remove namespace from action
                    String soapAction = fn.SoapAction.replace(PropertyContainer.Namespace, "");
                    classText = classText.replaceAll("%%SOAPMETHODNAME%%", soapAction);

                    //now find namespace and replace
                    classText = classText.replaceAll("%%NAMESPACE%%", PropertyContainer.Namespace);

                    //build properties
                    //build properties insert
                    String propText = "";
                    String soapPropText = "";


                    for (SoapClassProperty prop : paramClass.Properties)
                    {

                        propText += String.format("     public %s %s;", prop.getPropertyClassType(),prop.getPropertyName()) + "\n";

                        soapPropText += String.format("     request.addProperty(\"%s\", %s);", prop.getPropertyName(),prop.getPropertyName()) + "\n";



                    }

                    //set properties 
                    classText = classText.replaceAll("%%PROPERTIES%%", propText);

                    //set soap params properties
                    classText = classText.replaceAll("%%SOAPPROPERTIES%%", soapPropText);

                    //now save to folder
                    //Get full path
                    String filePath = FileHelper.GetOutputFolderPath() + "/" + paramClass.Name + ".java";

                    //write to file
                    if (!FileHelper.WriteClassTextToFile(filePath, classText))
                    {
                        throw new Exception("unable to create parameter class file");
                    }
                }
                else
                {
                 throw new Exception("could not locatie Parameter Class template");
                }
            }
            else
            {
                throw new Exception("parameter class not found");
            }

            //now do the Return type class
            if (returnClass != null)
            {
                InputStream file = ClassProcessor.class.getResourceAsStream("resources/ResponseTemplate.txt");

                //set up paramClass
                if (file != null )
                {
                    String classText = FileHelper.getContents(file);

                    file.close();

                    //replace package place holder
                    classText = classText.replaceAll("%%PACKAGENAME%%", packageName);

                    //now find classname and replace that with the class name
                    classText = classText.replaceAll("%%CLASSNAME%%", returnClass.Name);

                    //get first Parameter only as it a reutrn type
                    SoapClassProperty prop = returnClass.Properties.get(0);

                    //now replace Prop type and name place holders
                    if (prop.getIsComplexType())
                    {
                        //insert register text to register as seiraliable object
                        classText = classText.replaceAll("%%REGISTERTYPES%%", "new %%RESULTPROPTYPE%%().register(envelope);");
                    }
                    else
                    {
                        //remove placeholder
                        classText = classText.replaceAll("%%REGISTERTYPES%%", "");
                    }


                    //set replace the getproperty placeholder
                    classText = classText.replaceAll("%%GETPROPINFO%%",getClassTypeRetrievalString(prop.getPropertyName(),prop.getPropertyClassType()));

                    //replace the setproperty place holder
                    classText = classText.replaceAll("%%SETPROP%%",getConvertorForType(prop.getPropertyClassType()));

                    
                    //now find classname and replace that with the class name
                    classText = classText.replaceAll("%%RESULTPROPNAME%%", prop.getPropertyName());

                    //now find classname and replace that with the class name
                    classText = classText.replaceAll("%%RESULTPROPTYPE%%", prop.getPropertyClassType());


                    //now save to folder
                    //Get full path
                    String filePath = FileHelper.GetOutputFolderPath() + "/" + returnClass.Name + ".java";

                    //write to file
                    if (!FileHelper.WriteClassTextToFile(filePath, classText))
                    {
                        throw new Exception("unable to create return class file");
                    }
                }
                else
                {
                    throw new Exception("could not locatie Return Class template");
                }

            }
            else
            {
                throw new Exception("return class not found");
            }


        }
    }
}
