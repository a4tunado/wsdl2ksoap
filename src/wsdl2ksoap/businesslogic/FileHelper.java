/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wsdl2ksoap.businesslogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author newky
 */
public class FileHelper
{
    private static String m_CompleteFolderPath;

    public static String GetOutputFolderPath()
    {
        return m_CompleteFolderPath;
    }
static public String getContents(InputStream aStream) {
    //...checks on aFile are elided
    StringBuilder contents = new StringBuilder();

    try {
      //use buffering, reading one line at a time
      //FileReader always assumes default encoding is OK!
      BufferedReader input =  new BufferedReader(new InputStreamReader(aStream));
      try {
        String line = null; //not declared within while loop
        /*
        * readLine is a bit quirky :
        * it returns the content of a line MINUS the newline.
        * it returns null only for the END of the stream.
        * it returns an empty String if two newlines appear in a row.
        */
        while (( line = input.readLine()) != null){
          contents.append(line);
          contents.append(System.getProperty("line.separator"));
        }
      }
      finally {
        input.close();
      }
    }
    catch (IOException ex){
      ex.printStackTrace();
    }

    return contents.toString();
  }

static public boolean createFolderStructure(String parentPath, String packageName)
{
    try
    {
        if (parentPath.length() != 0)
        {
            //output folder set

            //see if last character is already set with a /
            if (parentPath.endsWith("/"))
            {
                //yes - so remove it
                parentPath = parentPath.substring(0, parentPath.length()-1);

            }

           
            //see if the packagename ends with a /
            //see if last character is already set with a /
            if (packageName.endsWith("/"))
            {
                //yes - so remove it
                packageName = packageName.substring(0, packageName.length()-1);

            }


            //now split the packagename
            String[] folders = packageName.split("\\.");


            
            String folderPath = parentPath;

            
            //manual packaname entry
            //loop through in reverse order, as is the standard for Java packagenames
            for (int loop = 0; loop < folders.length; loop++)
            {
                //path string
                folderPath = folderPath + "/" + folders[loop];

                File f = new File(folderPath);

                if (!f.exists()) {
                    if (!f.mkdir()) {
                        throw new Exception("Failed to create folder");
                    }
                }

            }
            

            //set main folder location for all files
            m_CompleteFolderPath = folderPath;

        }
        else
        {
            throw new Exception("Output folder has not been set");
        }
    }
    catch (Exception ex)
    {
        return false;
    }

    return true;
}

    static public boolean WriteClassTextToFile(String filename, String text)
    {
        try {
            //see if file exists first
            File fi = new File(filename);

            if (fi.exists()) {
                //delete it
                fi.delete();
            }

            //write to file
            Writer out = new OutputStreamWriter(new FileOutputStream(fi));

            out.write(text);
            out.close();
        } catch (Exception ex) {
            return false;
        }


        return true;
    }
}
