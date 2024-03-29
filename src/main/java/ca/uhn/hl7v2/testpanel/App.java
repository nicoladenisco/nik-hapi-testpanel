/**
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
 * specific language governing rights and limitations under the License.
 *
 * The Original Code is ""  Description:
 * ""
 *
 * The Initial Developer of the Original Code is University Health Network. Copyright (C)
 * 2001.  All Rights Reserved.
 *
 * Contributor(s): ______________________________________.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License (the  "GPL"), in which case the provisions of the GPL are
 * applicable instead of those above.  If you wish to allow use of your version of this
 * file only under the terms of the GPL and not to allow others to use your version
 * of this file under the MPL, indicate your decision by deleting  the provisions above
 * and replace  them with the notice and other provisions required by the GPL License.
 * If you do not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the GPL.
 */
package ca.uhn.hl7v2.testpanel;

import ca.uhn.hl7v2.testpanel.controller.Controller;
import ca.uhn.hl7v2.testpanel.controller.Prefs;
import ca.uhn.hl7v2.util.MessageIDGenerator;
import java.awt.EventQueue;
import java.lang.reflect.Method;
import java.net.URL;
import javax.swing.UIManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.commonlib5.utils.OsIdent;

public class App
{
  public static final String APP_NAME = "HapiTestPanel";
  private static Controller myController;

  /**
   * Launch the application.
   */
  public static void main(String[] args)
  {
    try
    {
      doMain(args);
    }
    catch(Exception e)
    {
      System.err.println("FATAL ERROR occurred in application startup.");
      e.printStackTrace();
    }
  }

  public static void doMain(String[] args)
     throws Exception
  {
    if(!OsIdent.isJava8())
    {
      System.out.println(
         "This application use only Java 1.8\n"
         + "Change jour JVM to run application."
      );
      return;
    }

    //System.setProperty("log4j2.debug", "true");
    System.setProperty("hapi.home", Prefs.getTestpanelHomeDirectory().getAbsolutePath());
    System.setProperty("testpanel.log.dir", Prefs.getTestpanelLogDirectory().getAbsolutePath());
    URL logres = App.class.getClassLoader().getResource("log4j2_testpanel.xml");

    Configurator.initialize("hapi", App.class.getClassLoader(), logres.toURI());

    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HAPI TestPanel");
    System.setProperty(MessageIDGenerator.NEVER_FAIL_PROPERTY, Boolean.TRUE.toString());

    // allunga il timeout a 30 secondi
    System.setProperty("ca.uhn.hl7v2.app.initiator.timeout", "30000");

    // Set System L&F
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    myController = new Controller();

    switch(OsIdent.checkOStype())
    {
      case OsIdent.OS_LINUX:
      case OsIdent.OS_FREEBSD:
      {
        Class<?> clazz = Class.forName("ca.uhn.hl7v2.testpanel.LinuxInitializer");
        Method runMethod = clazz.getMethod("run", Controller.class);
        runMethod.invoke(clazz.newInstance(), myController);
        break;
      }

      case OsIdent.OS_WINDOWS:
      {
        Class<?> clazz = Class.forName("ca.uhn.hl7v2.testpanel.WindowsInitializer");
        Method runMethod = clazz.getMethod("run", Controller.class);
        runMethod.invoke(clazz.newInstance(), myController);
        break;
      }

      case OsIdent.OS_MACOSX:
      {
        Class<?> clazz = Class.forName("ca.uhn.hl7v2.testpanel.OSXInitializer");
        Method runMethod = clazz.getMethod("run", Controller.class);
        runMethod.invoke(clazz.newInstance(), myController);
        break;
      }
    }

    EventQueue.invokeLater(() -> myController.start());
  }
}
