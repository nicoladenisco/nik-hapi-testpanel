package ca.uhn.hl7v2.testpanel.util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.Version;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.AbstractModelClassFactory;
import java.util.Map;

public class EventMapUtil
{
  private static MyMCF ourMcf;

  public static Map<String, String> getEventMap(String theVersion)
     throws HL7Exception
  {
    if(ourMcf == null)
      ourMcf = new MyMCF();
    return ourMcf.getEventMapForVersion(Version.versionOf(theVersion));
  }

  public static Map<String, String> getEventMapWithDefault(String theVersion, String defaultVersion)
     throws HL7Exception
  {
    if(ourMcf == null)
      ourMcf = new MyMCF();

    Map<String, String> rv;

    if((rv = ourMcf.getEventMapForVersion(Version.versionOf(theVersion))) != null)
      return rv;

    return ourMcf.getEventMapForVersion(Version.versionOf(defaultVersion));
  }

  private static class MyMCF extends AbstractModelClassFactory
  {
    public Class<? extends Message> getMessageClass(String theName, String theVersion, boolean theIsExplicit)
       throws HL7Exception
    {
      throw new UnsupportedOperationException();
    }

    public Class<? extends Message> getMessageClassInASpecificPackage(String theName, String theVersion, boolean theIsExplicit, String thePackageName)
       throws HL7Exception
    {
      throw new UnsupportedOperationException();
    }

    public Class<? extends Group> getGroupClass(String theName, String theVersion)
       throws HL7Exception
    {
      throw new UnsupportedOperationException();
    }

    public Class<? extends Segment> getSegmentClass(String theName, String theVersion)
       throws HL7Exception
    {
      throw new UnsupportedOperationException();
    }

    public Class<? extends Type> getTypeClass(String theName, String theVersion)
       throws HL7Exception
    {
      throw new UnsupportedOperationException();
    }
  }
}
