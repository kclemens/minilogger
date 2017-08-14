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
        Assert.assertEquals(MiniLoggerBuilder.DEFAILT_SEPARATOR, miniLogger.getSeparator());
        Assert.assertEquals(MiniLoggerBuilder.DEFAILT_TIME_PATTERN, miniLogger.getTimePattern());
    }

}