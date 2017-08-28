package kc.utils.minilogger;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals("--", miniLogger.getSeparator());
        Assert.assertEquals("time", miniLogger.getTimePattern());
        Assert.assertEquals("--%12s--", miniLogger.getNamePattern());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooShortLogNameLength() {
        new MiniLoggerBuilder().withLogNameLength(2);
    }

}