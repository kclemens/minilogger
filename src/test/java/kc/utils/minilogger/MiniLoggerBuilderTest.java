package kc.utils.minilogger;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

/**
 * Created by kclemens on 10/20/17.
 */
public class MiniLoggerBuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void testBadParamName() {
        MiniLoggerBuilder.fromStream(new ByteArrayInputStream("lalala:12".getBytes()));
    }

    @Test(expected = IllegalArgumentException.class)
    @Ignore
    public void testBadDebugEnabled() {
        MiniLoggerBuilder.fromStream(new ByteArrayInputStream("debugEnabled:yes".getBytes()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadLogNameLength() {
        MiniLoggerBuilder.fromStream(new ByteArrayInputStream("logNameLength:very short please".getBytes()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadProgressSilencePeriod() {
        MiniLoggerBuilder.fromStream(new ByteArrayInputStream("progressSilencePeriod:none".getBytes()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadConsole() {
        MiniLoggerBuilder.fromStream(new ByteArrayInputStream("console:screen".getBytes()));
    }

    @Test
    public void testLoadConfigFromStream() throws IOException {
        MiniLoggerBuilder.fromStream(new ByteArrayInputStream((
                "# comments are fine \n" +
                "# as are blank lines\n" +
                "\n" +
                "\n" +
                "timePattern:  # disable timestamp\n" +
                "separator:--  # by the way, comments after parameters are allowed too!\n" +
                "debugEnabled: true\n" +
                "logNameLength: 4\n" +
                "#progressSilencePeriod left default\n" +
                "#muteSet left empty\n" +
                "focusSet: some-log-name,MiniLoggerBuilderTest,some-other-name\n" +
                "        console:#leading whitespaces not nice, but allowed\n" +
                "file:target/log.txt\n").getBytes())).build().getLog().info("it works!");

        assertFileContentsAndDelete("target/log.txt", MiniLoggerBuilderTest.class.getSimpleName().substring(0, 4) + "--it works!\n");
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