package kc.utils.minilogger;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;

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
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withSeparator("-")
                .withLogNameLength(33)
                .build();

        Assert.assertEquals("-%33s-", miniLogger.getNamePattern());
        miniLogger.setSeparator("++");
        Assert.assertEquals("++%33s++", miniLogger.getNamePattern());
    }

    @Test
    public void testChangeLogNameLength() {
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withSeparator("-")
                .withLogNameLength(33)
                .build();

        Assert.assertEquals("-%33s-", miniLogger.getNamePattern());
        miniLogger.setLogNameLength(12);
        Assert.assertEquals("-%12s-", miniLogger.getNamePattern());
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

    @Test
    public void testOverwriteEntireProgressLine() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withLogPrintStreams(Collections.<PrintStream>emptySet())
                .withProgressPrintStreams(Collections.singleton(new PrintStream(stream)))
                .build();

        miniLogger.log("1234567890");
        miniLogger.progress("123456");
        miniLogger.progress("1234");
        miniLogger.progress("123");
        miniLogger.progress("12345");
        miniLogger.log("12");

        Assert.assertEquals(
                "1234567890\n" +
                "123456\r" +
                "1234  \r" +
                "123 \r" +
                "12345\r" +
                "12   \n", stream.toString());
    }

}