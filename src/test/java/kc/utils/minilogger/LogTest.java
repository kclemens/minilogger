package kc.utils.minilogger;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;

/**
 * Created by kclemens on 8/12/17.
 */
public class LogTest {

    @Test
    public void testLogAndProgress() {
        ByteArrayOutputStream logAndProgress = new ByteArrayOutputStream(1024);
        ByteArrayOutputStream logOnly = new ByteArrayOutputStream(1024);
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withTimePattern("time")
                .withSeparator(" - ")
                .withDebugEnabled(true)
                .withLogPrintStream(new PrintStream(logOnly))
                .withProgressPrintStream(new PrintStream(logAndProgress))
                .build();

        Log log = miniLogger.getLog("name");
        log.info("hello world");
        log.debug("hello %s", "world");
        log.progress("%s %s", "hello", "world");

        Assert.assertEquals(
                "time -       name - hello world\ntime -       name - hello world\ntime -       name - hello world\r",
                logAndProgress.toString());

        Assert.assertEquals(
                "time -       name - hello world\ntime -       name - hello world\n",
                logOnly.toString());
    }

    @Test
    public void testNoDebug() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withTimePattern("time")
                .withSeparator(" - ")
                .withLogPrintStream(null)
                .withProgressPrintStream(new PrintStream(stream))
                .withDebugEnabled(false)
                .build();

        Log log = miniLogger.getLog("name");
        log.info("hello world");
        log.debug("hello %s", "world");
        log.progress("%s %s", "hello", "world");

        Assert.assertEquals(
                "time -       name - hello world\ntime -       name - hello world\r",
                stream.toString());
    }

    @Test
    public void testNoDebugFocus() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withTimePattern("time")
                .withSeparator(" - ")
                .withLogPrintStream(null)
                .withProgressPrintStream(new PrintStream(stream))
                .withDebugEnabled(false)
                .withFocusSet(Collections.singleton("focus"))
                .build();

        Log focus = miniLogger.getLog("focus");
        Log other = miniLogger.getLog("other");

        focus.debug("message");
        other.debug("other message");

        Assert.assertEquals(
                "time -      focus - message\n",
                stream.toString());
    }

    @Test
    public void testCommonNameWidth() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withTimePattern("time")
                .withLogPrintStream(null)
                .withProgressPrintStream(new PrintStream(stream))
                .withLogNameLength(5)
                .build();

        miniLogger.getLog("a").info("a");
        miniLogger.getLog("bb").info("bb");
        miniLogger.getLog("super-long-name").info("super-long-name");
        miniLogger.getLog("cc").info("cc");

        Assert.assertEquals(
                "time     a a\n" +
                "time    bb bb\n" +
                "time sup.. super-long-name\n" +
                "time    cc cc\n",
                stream.toString());
    }

    @Test
    public void testDebugMute() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withTimePattern("time")
                .withSeparator(" - ")
                .withLogPrintStream(null)
                .withProgressPrintStream(new PrintStream(stream))
                .withDebugEnabled(true)
                .withMuteSet(Collections.singleton("mute"))
                .build();

        Log mute = miniLogger.getLog("mute");
        Log other = miniLogger.getLog("other");

        mute.debug("muted message");
        other.debug("message");

        Assert.assertEquals(
                "time -      other - message\n",
                stream.toString());
    }

    @Test
    public void testNamesForLogger() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withTimePattern("")
                .withSeparator("")
                .withLogPrintStream(null)
                .withProgressPrintStream(new PrintStream(stream))
                .withLogNameLength(7)
                .build();

        miniLogger.getLog("1234567890").info("");
        miniLogger.getLog().info("");
        miniLogger.getLog("123").info("");

        Assert.assertEquals(
                "12345..\n" +
                "LogTest\n" +
                "    123\n",
                stream.toString());
    }

    @Test
    @Ignore
    public void demo() {
        Log log = new MiniLoggerBuilder().build().getLog();

        log.info("loading fake data with Threed.sleep to emulate long-lasting progress...");
        String[] rotator = {"/", "-", "\\", "|"};
        for (int i = 0; i <= 100; i++) {
            log.progress("%s %3s%% completed.", rotator[i % rotator.length], i);
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("thank you and good bye.");
    }
}