/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wsdl2ksoap.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author newky
 */
public class PropertyContainer
{

        public static String ServiceName;
        public static String WSDLAddress;
        public static String Namespace;
        public static Function[] Functions;

        public static List<SoapClass> Classes = new ArrayList<SoapClass>();
        public static List<SoapClass> ComplexTypes = new ArrayList<SoapClass>();

        public static String GetSoapPortName()
        {
            return ServiceName + "Soap";
        }

        public static String GetSoap12PortName()
        {
            return ServiceName + "Soap12";
        }

        ///Get Class with name
        public static SoapClass GetClassWithName(String name)
        {
            //first try the Classes array
            for (SoapClass spC : Classes)
            {
                if (spC.Name.equals(name))
                {
                    return spC;
                }
            }

            //then try ComplexTypes
            for (SoapClass spC : ComplexTypes)
            {
                if (spC.Name.equals(name))
                {
                    return spC;
                }
            }
            return null;
        }

        public static void reset()
        {
           
            Classes = new ArrayList<SoapClass>();
            ComplexTypes = new ArrayList<SoapClass>();
        }
}
