package kc.utils.minilogger;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * Created by kclemens on 8/14/17.
 */
public class MiniLoggerBuilderTest {

    @Test
    public void testDefaults() {
        MiniLogger miniLogger = new MiniLoggerBuilder().build();

        Assert.assertEquals(MiniLoggerBuilder.DEFAILT_DEBUG_ENABLED, miniLogger.isDebugEnabled());
        Assert.assertEquals(MiniLoggerBuilder.DEFAILT_TIME_PATTERN, miniLogger.getTimePattern());
        Assert.assertEquals(
                MiniLoggerBuilder.DEFAILT_SEPARATOR + "%" + MiniLoggerBuilder.DEFAULT_LOG_NAME_LENGTH + "s" + MiniLoggerBuilder.DEFAILT_SEPARATOR,
                miniLogger.getNamePattern());
    }

    @Test
    public void testSetNonDefaults() {
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withLogNameLength(12)
                .withTimePattern("time")
                .withSeparator("--")
                .withDebugEnabled(true)
                .build();

        Assert.assertTrue(miniLogger.isDebugEnabled());
        Assert.assertEquals("time", miniLogger.getTimePattern());
        Assert.assertEquals("--%12s--", miniLogger.getNamePattern());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooShortLogNameLength() {
        new MiniLoggerBuilder().withLogNameLength(2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadConfigFromBrokenFile() throws FileNotFoundException {
        MiniLoggerBuilder.fromFile("/broken-minilogger.conf");
    }

    @Test(expected = FileNotFoundException.class)
    public void testLoadConfigFromNonExistingFile() throws FileNotFoundException {
        MiniLoggerBuilder.fromFile("/non-existing-file-name.conf");
    }

    @Test
    public void testLoadConfigFromFile() {
        MiniLoggerBuilder builder = MiniLoggerBuilder.fromFile();

        Assert.assertEquals("time", builder.timePattern);
        Assert.assertEquals("++", builder.separator);
        Assert.assertEquals(22, builder.logNameLength);
        Assert.assertTrue(builder.debugEnabled);
        Assert.assertTrue(builder.muteSet.isEmpty());
        Assert.assertEquals(2, builder.focusSet.size());
        Assert.assertTrue(builder.focusSet.contains("other-name"));
        Assert.assertTrue(builder.focusSet.contains("some-logger-name"));
        Assert.assertEquals(System.err, builder.progressPrintStream);
        Assert.assertEquals(null, builder.logPrintStream);
    }
}