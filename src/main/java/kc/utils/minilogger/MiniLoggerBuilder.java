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
     * The default time pattern produces toFileAndConsole lines starting with yyyy-mm-ddTHH:MM:SS.ssss, e.g. 2017-08-28T20:26:16.449
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
     * The default Log name length. Shorter names are filled with whitespace for nicer-looking toFileAndConsole; longer names are
     * abbreviated.
     */
    public static final int DEFAULT_LOG_NAME_LENGTH = 0;

    /**
     * The default file name to toFileAndConsole to.
     */
    public static final String DEFAULT_LOG_FILE_PATTERN = null;

    /**
     * The default console to toFileAndConsole to.
     */
    public static final PrintStream DEFAULT_LOG_CONSOLE = System.err;

    /**
     * The period after a progress statements during which subsequent progress are skipped for performance reasons,
     * in milliseconds.
     */
    public static final long DEFAULT_PROGRESS_SILENCE_PERIOD = 250;

    private boolean enableDebug;
    private String timePattern;
    private String separator;
    private int logNameLength;
    private String fileNamePattern;
    private OutputStream consoleStream;
    private long progressSilencePeriod;
    private Set<String> muteSet;
    private Set<String> focusSet;

    /**
     * Reads the default config file ("/minilogger.conf" on class path) and returns a MiniLoggerBuilder instance
     * with parameters set from this file. If no config file is found at the default location, the default config is loaded.
     *
     * @return the MiniLoggerBuilder with config loaded from file, or default config if no file was found
     */
    public static MiniLoggerBuilder fromDefaultConfigFile() {
        InputStream in = null;
        try {
            in = MiniLoggerBuilder.class.getResourceAsStream("/minilogger.conf");
            if (in == null) {
                return new MiniLoggerBuilder();
            } else {
                return fromStream(in);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore this
                }
            }
        }
    }

    /**
     * Reads the config data from the given stream and returns a MiniLoggerBuilder instance with parameters set
     * as specified.
     *
     * @param in the stream to read from
     * @return the MiniLoggerBuilder configured as specified in the stream
     */
    public static MiniLoggerBuilder fromStream(InputStream in) {
        MiniLoggerBuilder builder = new MiniLoggerBuilder();

        BufferedReader configReader = new BufferedReader(new InputStreamReader(in));

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
                    String timePattern = line.substring("timePattern:".length()).trim();
                    if (timePattern.length() == 0) {
                        builder.withTimePattern(null);
                    } else {
                        builder.withTimePattern(timePattern);
                    }
                } else if (line.startsWith("separator:")) {
                    builder.withSeparator(line.substring("separator:".length()).trim());
                } else if (line.startsWith("debugEnabled:")) {
                    builder.withDebugEnabled(Boolean.valueOf(line.substring("debugEnabled:".length()).trim()));
                } else if (line.startsWith("logNameLength:")) {
                    builder.withLogNameLength(Integer.parseInt(line.substring("logNameLength:".length()).trim()));
                } else if (line.startsWith("progressSilencePeriod:")) {
                    builder.withProgressSilencePeriod(Long.parseLong(line.substring("progressSilencePeriod:".length()).trim()));
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
                        builder.withFileNamePattern(fileName);
                    } else {
                        builder.withFileNamePattern(null);
                    }
                } else if (line.startsWith("console:")) {
                    String console = line.substring("console:".length()).trim();
                    if ("".equals(console)) {
                        builder.withConsoleStream(null);
                    } else if ("out".equalsIgnoreCase(console)) {
                        builder.withConsoleStream(System.out);
                    } else if ("err".equalsIgnoreCase(console)) {
                        builder.withConsoleStream(System.err);
                    } else {
                        throw new IllegalArgumentException("Unexpected console defined, expected 'out' or 'err', but got " + console);
                    }
                } else {
                    throw new IllegalArgumentException("Cannot parse config file line '" + line + "'!");
                }
            }

            return builder;
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading config file!", e);
        }
    }

    /**
     * Creates a MiniLoggerBuilder with default values, ready to be adopted to your needs.
     */
    public MiniLoggerBuilder() {
        this.withTimePattern(DEFAILT_TIME_PATTERN)
            .withSeparator(DEFAILT_SEPARATOR)
            .withDebugEnabled(DEFAILT_DEBUG_ENABLED)
            .withLogNameLength(DEFAULT_LOG_NAME_LENGTH)
            .withMuteSet(new HashSet<String>())
            .withFocusSet(new HashSet<String>())
            .withFileNamePattern(DEFAULT_LOG_FILE_PATTERN)
            .withConsoleStream(DEFAULT_LOG_CONSOLE)
            .withProgressSilencePeriod(DEFAULT_PROGRESS_SILENCE_PERIOD);
    }

    /**
     * Creates an instance of MiniLogger configured with parameters as set on the builder.
     *
     * @return the configured MiniLogger instance.
     */
    public MiniLogger build() {
        return new MiniLogger(
                this.enableDebug,
                this.timePattern,
                this.separator,
                this.logNameLength,
                this.fileNamePattern,
                this.consoleStream,
                this.progressSilencePeriod,
                this.muteSet,
                this.focusSet);
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
        this.enableDebug = debugEnabled;
        return this;
    }

    /**
     * Specifies the file name to be used by the MiniLogger created from this builder. Only info and debug but
     * no progress messages are written to this file. If the pattern contains a time reference, files will be rolled
     * accordingly.
     *
     * @param fileNamePattern to generate files file to use for info and debug
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
        return this;
    }

    /**
     * Specifies the toConsoleNoNewline PrintStream to be used by the MiniLogger created from this builder. Besides info and
     * debug but toConsoleNoNewline messages are written to this stream.
     *
     * @param consoleStream the OutputStram to use for info, debug, and progress
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withConsoleStream(OutputStream consoleStream) {
        this.consoleStream = consoleStream;
        return this;
    }

    /**
     * Specifies the silence period during which progress statements are not written to the console for performance
     * reasons. In milliseconds.
     *
     * @param progressSilencePeriod the period after a progress statement during which subsequent progress statements
     *                              are not written to the console.
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withProgressSilencePeriod(long progressSilencePeriod) {
        this.progressSilencePeriod = progressSilencePeriod;
        return this;
    }

    /**
     * Specifies the set of focus names. If a Log has a name that is in the focus set, it's debug messages are output
     * even if the debugEnabled flag is set to false
     *
     * @param focusSet the set of toFileAndConsole names that are printing their debug statements
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
     * @param muteSet the set of toFileAndConsole names that are not printing their debug statements
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withMuteSet(Set<String> muteSet) {
        this.muteSet = muteSet;
        return this;
    }

    /**
     * Specifies the length of Log names. Shorter names will be expanded with whitespace so that every toFileAndConsole message
     * starts at the same point in a line. Longer names will be abbreviated.
     *
     * @param logNameLength the length of Log names to use
     * @return this MiniLoggerBuilder, for further configuration
     */
    public MiniLoggerBuilder withLogNameLength(int logNameLength) {
        this.logNameLength = logNameLength;
        return this;
    }
}
