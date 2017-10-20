package kc.utils.minilogger;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by kclemens on 8/12/17.
 */
public class LogTest {

    @Test
    public void testDebug() {
        MiniLogger miniLogger = Mockito.mock(MiniLogger.class);
        Log log = new Log(miniLogger, "name");

        log.debug("pattern", "hello", "world");

        Mockito.verify(miniLogger).toFileAndConsole("name", true, "pattern", "hello", "world");
        Mockito.verifyNoMoreInteractions(miniLogger);
    }

    @Test
    public void testInfo() {
        MiniLogger miniLogger = Mockito.mock(MiniLogger.class);
        Log log = new Log(miniLogger, "name");

        log.info("pattern", "hello", "world");

        Mockito.verify(miniLogger).toFileAndConsole("name", false, "pattern", "hello", "world");
        Mockito.verifyNoMoreInteractions(miniLogger);
    }

    @Test
    public void testProgress() {
        MiniLogger miniLogger = Mockito.mock(MiniLogger.class);
        Log log = new Log(miniLogger, "name");

        log.progress("pattern", "hello", "world");

        Mockito.verify(miniLogger).toConsoleNoNewline("name", "pattern", "hello", "world");
        Mockito.verifyNoMoreInteractions(miniLogger);
    }

    @Test
    @Ignore
    public void demoProgress() {
        Log log = new MiniLoggerBuilder().withFileNamePattern("log.txt").build().getLog();

        log.info("using Threed.sleep to emulate long-lasting progress...");
        for (int i = 0; i <= 100; i++) {
            log.progress("%03d%% completed.", i);
            try {
                Thread.sleep(69);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("...all done");
        log.info("thank you and good bye");
    }
}