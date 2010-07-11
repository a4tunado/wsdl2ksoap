/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wsdl2ksoap.businesslogic;

/**
 *
 * @author newky
 */
public class Helper
{

    static public String convertUrlToJavaPackageName(String url)
    {
        String packageName;
        
        //firstly removed http://
        packageName = url.replaceFirst("http://", "");

        //the strip the last / if it has one
        if (packageName.endsWith("/"))
        {
           //yes - so remove it
           packageName = packageName.substring(0, packageName.length()-1);

        }

        //now split the packagename
        String[] parts = packageName.split("\\.");

        packageName = "";

        //now flip reverse to java standards
        //loop through in reverse order, as is the standard for Java packagenames
        for (int loop = parts.length - 1; loop > -1; loop--)
        {
            //path string
            packageName = packageName + "." + parts[loop];



        }

        //remove . from begining
        packageName = packageName.substring(1);


        return packageName;

    }
}
