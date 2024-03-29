package ca.uhn.hl7v2.testpanel.controller;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.message.ADT_A01;
import ca.uhn.hl7v2.testpanel.ui.tools.Hl7V2FileSortDialog;
import ca.uhn.hl7v2.testpanel.util.Hl7V2FileSorter;
import ca.uhn.hl7v2.testpanel.util.IProgressCallback.OperationCancelRequestedException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Hl7V2FileSortController
{
  private static final String OVERWRITE = "Overwrite";
  private static final String APPEND = "Append";
  private static final String CANCEL = "Cancel";
  private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(Hl7V2FileSortController.class);

  private final Hl7V2FileSortDialog myView;
  private final Controller myMasterController;
  private RunningThread myThread;

  public Hl7V2FileSortController(Controller theMasterController)
  {
    myMasterController = theMasterController;
    myView = new Hl7V2FileSortDialog(this);
  }

  public void begin()
  {
    assert SwingUtilities.isEventDispatchThread() : "Shouldn't be called from " + Thread.currentThread().getName();
    if(myThread != null)
    {
      return;
    }

    File inputFile = new File(myView.getInputFileText());
    if(!inputFile.exists() || !inputFile.canRead())
    {
      myMasterController.showDialogError("Input file does not exist or can not be read:\n" + myView.getInputFileText());
      return;
    }

    File outputFile = new File(myView.getOutputFileText());
    if(outputFile.isDirectory())
    {
      myMasterController.showDialogError("Output file is a directory:\n" + myView.getOutputFileText());
      return;
    }

    File outputDir = outputFile.getParentFile();
    if(!outputDir.exists())
    {
      myMasterController.showDialogError("Output file directory does not exist:\n" + outputDir.getAbsolutePath());
      return;
    }

    boolean append = false;
    if(outputFile.exists())
    {
      String message = "Output file already exists:\n" + outputFile.getName();
      String title = "TestPanel - File Exists";
      int messageType = JOptionPane.WARNING_MESSAGE;
      Icon icon = null;
      String[] selectionValues =
      {
        APPEND, OVERWRITE
      };

      String initialSelectionValue = Prefs.getInstance().getHl7V2SortOverwriteMode();
      String opt = (String) JOptionPane.showInputDialog(myView, message, title, messageType,
         icon, selectionValues, initialSelectionValue);

      if(null == opt)
      {
        return;
      }
      else
        switch(opt)
        {
          case APPEND:
            Prefs.getInstance().setHl7V2SortOverwriteMode(opt);
            append = true;
            break;
          case OVERWRITE:
            Prefs.getInstance().setHl7V2SortOverwriteMode(opt);
            append = false;
            break;
          case CANCEL:
            Prefs.getInstance().setHl7V2SortOverwriteMode(opt);
            return;
          default:
            return;
        }
    }

    Hl7V2FileSorter sorter = new Hl7V2FileSorter();
    sorter.setComparator(myView.provideMessageComparator());
    sorter.setProgressCallback(myView.provideProgressCallback());
    sorter.setInputFile(inputFile);
    sorter.setOutputFile(outputFile);
    sorter.setAppendOutputFile(append);

    myThread = new RunningThread(sorter);
    myMasterController.invokeInBackground(myThread);
  }

  private class RunningThread implements Runnable
  {
    private Hl7V2FileSorter mySorter;

    public RunningThread(Hl7V2FileSorter theSorter)
    {
      mySorter = theSorter;
    }

    @Override
    public void run()
    {
      try
      {
        mySorter.sort();
      }
      catch(OperationCancelRequestedException e)
      {
        // user clicked "cancel"
      }
      catch(Exception e)
      {
        ourLog.error("Failed to sort", e);
        myMasterController.showDialogError("Failed with message: " + e.getMessage());
      }
      finally
      {
        myThread = null;
      }
    }
  }

  public static void main(String[] args)
     throws IOException, HL7Exception
  {
    // Create random file
    try (FileWriter fw = new FileWriter("/Users/james/tmp/input.txt"))
    {
      // Create random file
      for(int i = 0; i < 10000; i++)
      {
        ADT_A01 msg = new ADT_A01();
        msg.initQuickstart("ADT", "A01", "T");
        String cid = Integer.toString((int) (Math.random() * Integer.MAX_VALUE));
        msg.getMSH().getMessageControlID().setValue(cid);
        fw.append(msg.encode());
        fw.append("\n\n");
      }
    }
  }

  public void show()
  {
    myView.setVisible(true);
  }
}
