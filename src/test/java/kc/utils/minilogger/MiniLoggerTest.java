package kc.utils.minilogger;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Calendar;
import java.util.HashSet;

/**
 * Created by kclemens on 8/14/17.
 */
public class MiniLoggerTest {

    @Test
    public void testLogAndProgress() throws IOException {
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withConsoleStream(new FileOutputStream("target/console.txt"))
                .withFileNamePattern("target/file.txt")
                .withTimePattern(null)
                .withLogNameLength(0)
                .withProgressSilencePeriod(0)
                .build();

        miniLogger.toFileAndConsole("logname", false, "hello %s", "world");
        miniLogger.toConsoleNoNewline("logname", "progress %d", 1);
        miniLogger.toConsoleNoNewline("logname", "progress %d", 99);
        miniLogger.toFileAndConsole("logname", false, "bye bye bye");

        assertFileContentsAndDelete("target/console.txt", "hello world\nprogress 1\rprogress 99\rbye bye bye\n");
        assertFileContentsAndDelete("target/file.txt", "hello world\nbye bye bye\n");
    }

    @Test
    public void testDebugOnAndOff() throws IOException {
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withConsoleStream(new FileOutputStream("target/console.txt"))
                .withFileNamePattern("target/file.txt")
                .withTimePattern(null)
                .withLogNameLength(0)
                .withProgressSilencePeriod(0)
                .withDebugEnabled(true)
                .build();

        miniLogger.toFileAndConsole("logname", false, "i1");
        miniLogger.toFileAndConsole("logname", true, "d1");
        miniLogger.toConsoleNoNewline("logname", "p1");

        miniLogger.disableDebug();

        miniLogger.toFileAndConsole("logname", false, "i2");
        miniLogger.toFileAndConsole("logname", true, "d2");
        miniLogger.toConsoleNoNewline("logname", "p2");

        miniLogger.enableDebug();

        miniLogger.toFileAndConsole("logname", false, "i3");
        miniLogger.toFileAndConsole("logname", true, "d3");
        miniLogger.toConsoleNoNewline("logname", "p3");

        assertFileContentsAndDelete("target/console.txt", "i1\nd1\np1\ri2\np2\ri3\nd3\np3\r");
        assertFileContentsAndDelete("target/file.txt", "i1\nd1\ni2\ni3\nd3\n");
    }

    @Test
    public void testMuteUnMute() throws IOException {
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withConsoleStream(new FileOutputStream("target/console.txt"))
                .withFileNamePattern("target/file.txt")
                .withTimePattern(null)
                .withLogNameLength(0)
                .withProgressSilencePeriod(0)
                .withDebugEnabled(true)
                .build();

        miniLogger.toFileAndConsole("logname", false, "i1");
        miniLogger.toFileAndConsole("logname", true, "d1");
        miniLogger.toConsoleNoNewline("logname", "p1");

        miniLogger.mute("logname");

        miniLogger.toFileAndConsole("logname", false, "i2");
        miniLogger.toFileAndConsole("logname", true, "d2");
        miniLogger.toConsoleNoNewline("logname", "p2");

        miniLogger.unMute("logname");

        miniLogger.toFileAndConsole("logname", false, "i3");
        miniLogger.toFileAndConsole("logname", true, "d3");
        miniLogger.toConsoleNoNewline("logname", "p3");

        assertFileContentsAndDelete("target/console.txt", "i1\nd1\np1\ri2\np2\ri3\nd3\np3\r");
        assertFileContentsAndDelete("target/file.txt", "i1\nd1\ni2\ni3\nd3\n");
    }

    @Test
    public void testFocusUnFocus() throws IOException {
        HashSet<String> focusSet = new HashSet<String>();
        focusSet.add("logname");

        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withConsoleStream(new FileOutputStream("target/console.txt"))
                .withFileNamePattern("target/file.txt")
                .withTimePattern(null)
                .withLogNameLength(0)
                .withProgressSilencePeriod(0)
                .withDebugEnabled(false)
                .withFocusSet(focusSet)
                .build();

        miniLogger.toFileAndConsole("logname", false, "i1");
        miniLogger.toFileAndConsole("logname", true, "d1");
        miniLogger.toConsoleNoNewline("logname", "p1");

        miniLogger.unFocus("logname");

        miniLogger.toFileAndConsole("logname", false, "i2");
        miniLogger.toFileAndConsole("logname", true, "d2");
        miniLogger.toConsoleNoNewline("logname", "p2");

        miniLogger.focus("logname");

        miniLogger.toFileAndConsole("logname", false, "i3");
        miniLogger.toFileAndConsole("logname", true, "d3");
        miniLogger.toConsoleNoNewline("logname", "p3");

        assertFileContentsAndDelete("target/console.txt", "i1\nd1\np1\ri2\np2\ri3\nd3\np3\r");
        assertFileContentsAndDelete("target/file.txt", "i1\nd1\ni2\ni3\nd3\n");
    }

    @Test
    public void testRollingLogFiles() throws InterruptedException, IOException {
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withConsoleStream(null)
                .withFileNamePattern("target/log%tS.txt")
                .withTimePattern(null)
                .withLogNameLength(0)
                .build();

        String fileA = String.format("target/log%tS.txt", System.currentTimeMillis());
        miniLogger.toFileAndConsole("logname", false, "text");
        Thread.sleep(1000);
        String fileB = String.format("target/log%tS.txt", System.currentTimeMillis());
        miniLogger.toFileAndConsole("logname", false, "text");

        Assert.assertNotEquals(fileA, fileB);
        assertFileContentsAndDelete(fileA, "text\n");
        assertFileContentsAndDelete(fileB, "text\n");
    }

    @Test
    public void testAppendSpacesToOverwriteLastProgress() throws IOException {
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withFileNamePattern("file.txt")
                .withConsoleStream(new FileOutputStream("console.txt"))
                .withTimePattern(null)
                .withLogNameLength(0)
                .withProgressSilencePeriod(0)
                .build();

        miniLogger.toFileAndConsole("logname", false, "long line");
        miniLogger.toConsoleNoNewline("logname", "short");
        miniLogger.toFileAndConsole("logname", false, "long line");
        miniLogger.toConsoleNoNewline("logname", "super very long line");
        miniLogger.toConsoleNoNewline("logname", "super long line");
        miniLogger.toFileAndConsole("logname", false, "long line");
        miniLogger.toFileAndConsole("logname", false, "short");

        assertFileContentsAndDelete("console.txt",
                                    "long line\n" +
                                    "short\r" +
                                    "long line\n" +
                                    "super very long line\r" +
                                    "super long line     \r" +
                                    "long line      \n" +
                                    "short\n");
        assertFileContentsAndDelete("file.txt",
                                    "long line\n" +
                                    "long line\n" +
                                    "long line\n" +
                                    "short\n");
    }

    @Test
    public void testModifyMessagePrefix() throws IOException {
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withFileNamePattern("file.txt")
                .withConsoleStream(new FileOutputStream("console.txt"))
                .withTimePattern(null)
                .withLogNameLength(0)
                .build();

        miniLogger.toFileAndConsole("logname", false, "hello");

        miniLogger.setTimePattern("%tY");
        miniLogger.setSeparator("|");
        miniLogger.setLogNameLength(7);

        miniLogger.toFileAndConsole("logname", false, "hello");

        miniLogger.setTimePattern("%1$tY=%1$tm");
        miniLogger.setSeparator("=");
        miniLogger.setLogNameLength(10);

        miniLogger.toFileAndConsole("logname", false, "hello");

        miniLogger.setTimePattern("%tm");
        miniLogger.setSeparator("*");
        miniLogger.setLogNameLength(3);

        miniLogger.toFileAndConsole("logname", false, "hello");

        String expectedFileContents = "hello\n" +
                                      String.format("%tY|logname|hello\n", Calendar.getInstance()) +
                                      String.format("%1$tY=%1$tm=   logname=hello\n", Calendar.getInstance()) +
                                      String.format("%tm*log*hello\n", Calendar.getInstance());

        assertFileContentsAndDelete("console.txt", expectedFileContents);
        assertFileContentsAndDelete("file.txt", expectedFileContents);
    }

    @Test
    public void testProgressLogCadence() throws InterruptedException, IOException {
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withFileNamePattern(null)
                .withConsoleStream(new FileOutputStream("console.txt"))
                .withTimePattern(null)
                .withLogNameLength(0)
                .withProgressSilencePeriod(0)
                .build();

        miniLogger.toConsoleNoNewline("logname", "p1");
        Thread.sleep(1);
        miniLogger.toConsoleNoNewline("logname", "p2");
        Thread.sleep(30);

        miniLogger.setProgressSilencePeriod(5);

        miniLogger.toConsoleNoNewline("logname", "p3");
        Thread.sleep(3);
        miniLogger.toConsoleNoNewline("logname", "p4"); // skipped as it is during the silence period
        Thread.sleep(3);
        miniLogger.toConsoleNoNewline("logname", "p5");

        miniLogger.setProgressSilencePeriod(0);

        Thread.sleep(1);
        miniLogger.toConsoleNoNewline("logname", "p6");
        Thread.sleep(1);
        miniLogger.toConsoleNoNewline("logname", "p7");

        assertFileContentsAndDelete("console.txt", "p1\rp2\rp3\rp5\rp6\rp7\r");
    }

    @Test
    public void testModifyFileAndConsole() throws IOException {
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withConsoleStream(new FileOutputStream("target/console1.txt"))
                .withFileNamePattern("target/file1.txt")
                .withTimePattern(null)
                .withLogNameLength(0)
                .withProgressSilencePeriod(0)
                .build();

        miniLogger.toFileAndConsole("logname", false, "hello");
        miniLogger.toConsoleNoNewline("logname", "hello");

        miniLogger.setConsoleStream(null);
        miniLogger.setFileNamePattern(null);

        miniLogger.toFileAndConsole("logname", false, "cruel and unfair");
        miniLogger.toConsoleNoNewline("logname", "cruel and unfair");

        miniLogger.setConsoleStream(new FileOutputStream("target/console2.txt"));
        miniLogger.setFileNamePattern("target/file2.txt");

        miniLogger.toFileAndConsole("logname", false, "world!");
        miniLogger.toConsoleNoNewline("logname", "world!");

        assertFileContentsAndDelete("target/console1.txt", "hello\nhello\r");
        assertFileContentsAndDelete("target/file1.txt", "hello\n");
        assertFileContentsAndDelete("target/console2.txt", "world!\nworld!\r");
        assertFileContentsAndDelete("target/file2.txt", "world!\n");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidFilePath() {
        new MiniLoggerBuilder()
                .withConsoleStream(null)
                .withFileNamePattern("target/non-existing/file.txt")
                .build()
                .toFileAndConsole("logname", false, "hello");
    }

    @Test
    public void testLogNames() throws IOException {
        MiniLogger miniLogger = new MiniLoggerBuilder()
                .withFileNamePattern("file.txt")
                .withConsoleStream(null)
                .withTimePattern(null)
                .withLogNameLength(10)
                .withSeparator(":")
                .build();

        miniLogger.getLog().info("hello");
        miniLogger.getLog("1234567890").info("hi");
        miniLogger.getLog("abcdefghijklmnopqrstuvfxyz").info("sup");

        assertFileContentsAndDelete("file.txt",
                                    String.format("%10.10s:hello\n", this.getClass().getSimpleName()) +
                                    "1234567890:hi\n" +
                                    "abcdefghij:sup\n");
    }

    private void assertFileContentsAndDelete(String fileName, String expectedFileContents) throws IOException {
        int expectedBytes = expectedFileContents.toCharArray().length;
        char[] consoleBuffer = new char[expectedBytes];
        BufferedReader console = new BufferedReader(new FileReader(fileName));
        Assert.assertEquals(expectedBytes, console.read(consoleBuffer));
        Assert.assertArrayEquals(expectedFileContents.toCharArray(), consoleBuffer);
        Assert.assertEquals(-1, console.read());
        console.close();
        Assert.assertTrue(new File(fileName).delete());
    }

}