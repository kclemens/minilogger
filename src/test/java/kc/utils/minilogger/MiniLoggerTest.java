package kc.utils.minilogger;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by kclemens on 8/14/17.
 */
public class MiniLoggerTest {

    @Test
    public void testChangeTimePattern() {
        MiniLogger miniLogger = new MiniLoggerBuilder().build();

        String newTimePattern = "new time pattern";

        Assert.assertNotEquals(newTimePattern, miniLogger.getTimePattern());
        miniLogger.setTimePattern(newTimePattern);
        Assert.assertEquals(newTimePattern, miniLogger.getTimePattern());
    }

    @Test
    public void testChangeSeparator() {
        MiniLogger miniLogger = new MiniLoggerBuilder().build();

        String newSeparator = "new separator";

        Assert.assertNotEquals(newSeparator, miniLogger.getSeparator());
        miniLogger.setSeparator(newSeparator);
        Assert.assertEquals(newSeparator, miniLogger.getSeparator());
    }

    @Test
    public void testFlipDebug() {
        MiniLogger miniLogger = new MiniLoggerBuilder().withDebugEnabled(true).build();

        Assert.assertTrue(miniLogger.isDebugEnabled());
        miniLogger.setDebugEnabled(false);
        Assert.assertFalse(miniLogger.isDebugEnabled());
    }

    @Test
    public void testAddRemoveFocus() {
        MiniLogger miniLogger = new MiniLoggerBuilder().build();

        String focusName = "name";

        Assert.assertFalse(miniLogger.getFocusSet().contains(focusName));
        miniLogger.addFocus(focusName);
        Assert.assertTrue(miniLogger.getFocusSet().contains(focusName));
        miniLogger.removeFocus(focusName);
        Assert.assertFalse(miniLogger.getFocusSet().contains(focusName));
    }

    @Test
    public void testAddRemoveMute() {
        MiniLogger miniLogger = new MiniLoggerBuilder().build();

        String muteName = "name";

        Assert.assertFalse(miniLogger.getMuteSet().contains(muteName));
        miniLogger.addMute(muteName);
        Assert.assertTrue(miniLogger.getMuteSet().contains(muteName));
        miniLogger.removeMute(muteName);
        Assert.assertFalse(miniLogger.getMuteSet().contains(muteName));
    }

}