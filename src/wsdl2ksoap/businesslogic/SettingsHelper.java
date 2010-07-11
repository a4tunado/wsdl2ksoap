/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wsdl2ksoap.businesslogic;

import java.util.prefs.Preferences;

/**
 *
 * @author newky
 */
public class SettingsHelper
{
    public static String Url;
    public static String OutputFolder;
    public static String Packagename;
    
    final private static String SettingsKeyUrl = "Url";
    final private static String SettingsKeyOutputFolder = "OutFolder";
    final private static String SettingsKeyPackagename = "Package";


    /*
     * loads saved settings
     *
     */
    public static void LoadSettings()
    {
        Preferences prefs = Preferences.userNodeForPackage(SettingsHelper.class);

        try
        {
            Url = prefs.get(SettingsKeyUrl, "");
            OutputFolder = prefs.get(SettingsKeyOutputFolder, "");
            Packagename = prefs.get(SettingsKeyPackagename, "");

        }
        catch (Exception ex)
        {

        }

    }

    /*
     * Saves settings to prefernce store
     */
    public static void SaveSettings()
    {
        Preferences prefs = Preferences.userNodeForPackage(SettingsHelper.class);

        prefs.put(SettingsKeyUrl, Url);
        prefs.put(SettingsKeyOutputFolder, OutputFolder);
        prefs.put(SettingsKeyPackagename, Packagename);
    }
}
