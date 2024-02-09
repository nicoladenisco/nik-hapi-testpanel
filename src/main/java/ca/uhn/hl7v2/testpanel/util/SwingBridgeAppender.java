package ca.uhn.hl7v2.testpanel.util;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.message.ReusableMessage;

/**
 * Bridge di compatibilita fra log4j e log4j2.
 * Questo appendere viene refenziato in log4j2.xml
 * il suo compito e girare gli eventi a SwingLogAppender
 * per renderli disponibili nella vecchia interfaccia swing.
 *
 * @author Nicola De Nisco
 */
@Plugin(name = "SwingLogAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class SwingBridgeAppender extends AbstractAppender
{
  private final SwingLogAppender la = new SwingLogAppender();

  protected SwingBridgeAppender(String name, Filter filter)
  {
    super(name, filter, null);
  }

  @PluginFactory
  public static SwingBridgeAppender createAppender(
     @PluginAttribute("name") String name,
     @PluginElement("Filter") final Filter filter)
  {
    return new SwingBridgeAppender(name, filter);
  }

  @Override
  public void append(LogEvent event)
  {

    la.append(convert(event));
  }

  private LoggingEvent convert(LogEvent event)
  {
    return new LoggingEventImp(event);
  }

  public static class LoggingEventImp extends LoggingEvent
  {
    private final LogEvent event;

    public LoggingEventImp(LogEvent event)
    {
      this.event = event;
    }

    @Override
    public long getTimeStamp()
    {
      return event.getTimeMillis();
    }

    @Override
    public Level getLevel()
    {
      org.apache.logging.log4j.Level l = event.getLevel();
      //System.out.println("**> " + l);
      return new Level(l.intLevel(), l.name(), 0)
      {
      };
    }

    @Override
    public Object getMessage()
    {
      if(event instanceof ReusableMessage)
        return ((ReusableMessage) event).getFormattedMessage();

      return event.getMessage();
    }
  }
}
