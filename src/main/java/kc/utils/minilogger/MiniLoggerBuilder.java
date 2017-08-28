package kc.utils.minilogger;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kclemens on 8/14/17.
 */
public class MiniLoggerBuilder {

    public static final String DEFAILT_TIME_PATTERN = "%1$tY-%1$tm-%1$tdT%1$tH:%1$tM:%1$tS.%1$tL";
    public static final String DEFAILT_SEPARATOR = " ";
    public static final boolean DEFAILT_DEBUG_ENABLED = true;
    public static final int DEFAULT_LOG_NAME_LENGTH = 10;
    public static final String DEFAULT_LOG_FILE = "log.txt";
    public static final PrintStream DEFAULT_LOG_CONSOLE = System.err;

    String timePattern;
    String separator;
    boolean debugEnabled;
    int logNameLength;
    Set<String> muteSet;
    Set<String> focusSet;
    PrintStream logPrintStream;
    PrintStream progressPrintStream;

    public static MiniLoggerBuilder fromFile() {
        return fromFile("/minilogger.conf");
    }

    public static MiniLoggerBuilder fromFile(String configFileName) {
        MiniLoggerBuilder builder = new MiniLoggerBuilder();

        InputStream configStream = MiniLoggerBuilder.class.getResourceAsStream(configFileName);
        if (configStream == null) {
            return builder;
        }

        BufferedReader configReader = new BufferedReader(new InputStreamReader(configStream));

        try {
            for (String line = configReader.readLine(); line != null; line = configReader.readLine()) {

                // cut off comments and witespace
                int commentStart = line.indexOf("#");
                if (commentStart >= 0) {
                    line = line.substring(0, commentStart);
                }
                line = line.trim();

                // ignore lines w/o content
                if ("".equals(line)) {
                    // ignore empty lines
                } else if (line.startsWith("timePattern:")) {
                    builder.withTimePattern(line.substring("timePattern:".length()).trim());
                } else if (line.startsWith("separator:")) {
                    builder.withSeparator(line.substring("separator:".length()).trim());
                } else if (line.startsWith("debugEnabled:")) {
                    builder.withDebugEnabled(Boolean.parseBoolean(line.substring("debugEnabled:".length()).trim()));
                } else if (line.startsWith("logNameLength:")) {
                    builder.withLogNameLength(Integer.parseInt(line.substring("logNameLength:".length()).trim()));
                } else if (line.startsWith("muteSet:")) {
                    HashSet<String> muteSet = new HashSet<String>();
                    String mutes = line.substring("muteSet:".length()).trim();
                    if (mutes.length() > 0) {
                        Collections.addAll(muteSet, mutes.split(","));
                    }
                    builder.withMuteSet(muteSet);
                } else if (line.startsWith("focusSet:")) {
                    HashSet<String> focusSet = new HashSet<String>();
                    String focuses = line.substring("focusSet:".length()).trim();
                    if (focuses.length() > 0) {
                        Collections.addAll(focusSet, focuses.split(","));
                    }
                    builder.withFocusSet(focusSet);
                } else if (line.startsWith("file:")) {
                    String fileName = line.substring("file:".length()).trim();
                    if (fileName.length() > 0) {
                        builder.withLogFile(fileName);
                    } else {
                        builder.withLogPrintStream(null);
                    }
                } else if (line.startsWith("console:")) {
                    String console = line.substring("console:".length()).trim();
                    if ("".equals(console)) {
                        builder.withProgressPrintStream(null);
                    } else if ("out".equalsIgnoreCase(console)) {
                        builder.withProgressPrintStream(System.out);
                    } else if ("err".equalsIgnoreCase(console)) {
                        builder.withProgressPrintStream(System.err);
                    } else {
                        throw new IllegalArgumentException("Unexpected console defined, expected 'out' or 'err', but got " + console);
                    }
                } else {
                    throw new IllegalArgumentException("Cannot parse config file line '" + line + "'!");
                }
            }

            return builder;
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading config file " + configFileName + "!", e);
        }
    }

    public MiniLoggerBuilder() {
        this.withTimePattern(DEFAILT_TIME_PATTERN);
        this.withSeparator(DEFAILT_SEPARATOR);
        this.withDebugEnabled(DEFAILT_DEBUG_ENABLED);
        this.withLogNameLength(DEFAULT_LOG_NAME_LENGTH);
        this.withMuteSet(new HashSet<String>());
        this.withFocusSet(new HashSet<String>());
        this.withLogFile(DEFAULT_LOG_FILE);
        this.withProgressPrintStream(DEFAULT_LOG_CONSOLE);
    }

    public MiniLogger build() {
        return new MiniLogger(
                this.timePattern,
                this.separator,
                this.logNameLength,
                this.debugEnabled,
                this.muteSet,
                this.focusSet,
                this.logPrintStream,
                this.progressPrintStream);
    }

    public MiniLoggerBuilder withTimePattern(String timePattern) {
        this.timePattern = timePattern;
        return this;
    }

    public MiniLoggerBuilder withSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    public MiniLoggerBuilder withDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        return this;
    }

    public MiniLoggerBuilder withLogPrintStream(PrintStream logPrintStream) {
        this.logPrintStream = logPrintStream;
        return this;
    }

    public MiniLoggerBuilder withLogFile(String fileName) {
        try {
            return this.withLogPrintStream(new PrintStream(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot log to file '" + fileName + "'!", e);
        }
    }

    public MiniLoggerBuilder withProgressPrintStream(PrintStream progressPrintStream) {
        this.progressPrintStream = progressPrintStream;
        return this;
    }

    public MiniLoggerBuilder withFocusSet(Set<String> focusSet) {
        this.focusSet = focusSet;
        return this;
    }

    public MiniLoggerBuilder withMuteSet(Set<String> muteSet) {
        this.muteSet = muteSet;
        return this;
    }

    public MiniLoggerBuilder withLogNameLength(int logNameLength) {
        if (logNameLength < 4) {
            throw new IllegalArgumentException("logNameLength should not be less than 3");
        }
        this.logNameLength = logNameLength;
        return this;
    }
}
