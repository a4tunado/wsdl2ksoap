/*
 * WSDL2KSoapApp.java
 */

package wsdl2ksoap;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class WSDL2KSoapApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new WSDL2KSoapView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of WSDL2KSoapApp
     */
    public static WSDL2KSoapApp getApplication() {
        return Application.getInstance(WSDL2KSoapApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(WSDL2KSoapApp.class, args);
    }
}
