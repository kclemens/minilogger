package kc.utils.minilogger;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder class for creating MiniLogger instances from config files or programmatically.
 */
public class MiniLoggerBuilder {

    /**
     * The default time pattern produces log lines starting with yyyy-mm-ddTHH:MM:SS.ssss, e.g. 2017-08-28T20:26:16.449
     */
    public static final String DEFAILT_TIME_PATTERN = "%1$tY-%1$tm-%1$tdT%1$tH:%1$tM:%1$tS.%1$tL";

    /**
     * The default separator used around a Log name.
     */
    public static final String DEFAILT_SEPARATOR = " ";

    /**
     * The flag indicating if debug statements are enabled by default;
     */
    public static final boolean DEFAILT_DEBUG_ENABLED = false;

    /**
     * The default Log name length. Shorter names are filled with whitespace for nicer-looking log; longer names are
     * abbreviated.
     */
    public static final int DEFAULT_LOG_NAME_LENGTH = 10;

    /**
     * The default file name to log to.
     */
    public static final String DEFAULT_LOG_FILE = "log.txt";

    /**
     * The default console to log to.
     */
    public static final PrintStream DEFAULT_LOG_CONSOLE = System.err;

    String timePattern;
    String separator;
    boolean debugEnabled;
    int logNameLength;
    Set<String> muteSet;
    Set<String> focusSet;
    PrintStream logPrintStream;
    PrintStream progressPrintStream;

    /**
     * Reads the default config file ("/minilogger.conf" on class path) and returns a MiniLoggerBuilder instance
     * with parameters set from this file. If no config file is found at the default location, the default config is loaded.
     *
     * @return the MiniLoggerBuilder with config loaded from file, or default config if no file was found
     */
    public static MiniLoggerBuilder fromFile() {
        try {
            return fromFile("/minilogger.conf");
        } catch (FileNotFoundException e) {
            return new MiniLoggerBuilder();
        }
    }

    /**
     * Reads the config file specified and returns a MiniLoggerBuilder instance with parameters set from the specified
     * file.
     *
     * @param configFileName the file to load
     * @return the MiniLoggerBuilder with config loaded from the specified file
     * @throws FileNotFoundException if the specified file is not found
     */
    public static MiniLoggerBuilder fromFile(String configFileName) throws FileNotFoundException {
        MiniLoggerBuilder builder = new MiniLoggerBuilder();

        InputStream configStream = MiniLoggerBuilder.class.getResourceAsStream(configFileName);
        if (configStream == null) {
            throw new FileNotFoundException("cannot find config file at '" + configFileName +"'!");
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
            throw new RuntimeException("Cannot read config file " + configFileName + "!", e);
        }
    }

    /**
     * Creates a MiniLoggerBuilder with default values, ready to be adopted to your needs.
     */
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

    /**
     * Creates an instance of MiniLogger configured with parameters as set on the builder.
     *
     * @return the configured MiniLogger instance.
     */
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

    /**
     * Specifies the time pattern to be used by the MiniLogger created from this builder.
     *
     * @param timePattern the time pattern to use
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withTimePattern(String timePattern) {
        this.timePattern = timePattern;
        return this;
    }

    /**
     * Specifies the separator to be used by the MiniLogger created from this builder.
     *
     * @param separator the time pattern to use
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    /**
     * Specifies the debug flag to be used by the MiniLogger created from this builder.
     *
     * @param debugEnabled the flag to use
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        return this;
    }

    /**
     * Specifies the log PrintStream to be used by the MiniLogger created from this builder. Only info and debug but
     * not progress messages are written to this stream.
     *
     * @param logPrintStream the PrintStream to use for info and debug
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withLogPrintStream(PrintStream logPrintStream) {
        this.logPrintStream = logPrintStream;
        return this;
    }

    /**
     * Specifies the log file name to be used by the MiniLogger created from this builder. Only info and debug but
     * not progress messages are written to this file.
     *
     * @param fileName the file to use for info and debug
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withLogFile(String fileName) {
        try {
            return this.withLogPrintStream(new PrintStream(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot log to file '" + fileName + "'!", e);
        }
    }

    /**
     * Specifies the progress PrintStream to be used by the MiniLogger created from this builder. Besides info and
     * debug but progress messages are written to this stream.
     *
     * @param progressPrintStream the PrintStream to use for info, debug, and progress
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withProgressPrintStream(PrintStream progressPrintStream) {
        this.progressPrintStream = progressPrintStream;
        return this;
    }

    /**
     * Specifies the set of focus names. If a Log has a name that is in the focus set, it's debug messages are output
     * even if the debugEnabled flag is set to false
     *
     * @param focusSet the set of log names that are printing their debug statements
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withFocusSet(Set<String> focusSet) {
        this.focusSet = focusSet;
        return this;
    }

    /**
     * Specifies the set of mute names. If a Log has a name that is in the mute set, it's debug messages are not output
     * even if the debugEnabled flag is set to true
     *
     * @param muteSet the set of log names that are not printing their debug statements
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withMuteSet(Set<String> muteSet) {
        this.muteSet = muteSet;
        return this;
    }

    /**
     * Specifies the length of Log names. Shorter names will be expanded with whitespace so that every log message
     * starts at the same point in a line. Longer names will be abbreviated.
     *
     * @param logNameLength the length of Log names to use
     * @return this MiniLoggerBuilder, for further configuration
     */

    public MiniLoggerBuilder withLogNameLength(int logNameLength) {
        if (logNameLength < 4) {
            throw new IllegalArgumentException("logNameLength should not be less than 3");
        }
        this.logNameLength = logNameLength;
        return this;
    }
}
