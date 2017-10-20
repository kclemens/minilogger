package kc.utils.minilogger;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.Calendar;
import java.util.Set;

/**
 * The MiniLogger class that manages configuration modifiable at run-time and generates Logs.
 */
public class MiniLogger {

    public static final MiniLogger ROOT = MiniLoggerBuilder.fromDefaultConfigFile().build();

    // params set from outside
    private boolean isDebugEnabled;
    private String timePattern;
    private String separator;
    private int logNameLength;
    private String fileNamePattern;
    private PrintStream consoleStream;
    private long progressSilencePeriod;
    private Set<String> muteSet;
    private Set<String> focusSet;

    MiniLogger(boolean enableDebug, String timePattern, String separator, int logNameLength, String fileNamePattern, OutputStream consoleStream, long progressSilencePeriod, Set<String> muteSet, Set<String> focusSet) {
        this.isDebugEnabled = enableDebug;
        this.timePattern = timePattern;
        this.separator = separator;
        this.logNameLength = logNameLength;
        this.fileNamePattern = fileNamePattern;
        this.consoleStream = consoleStream == null?null:new PrintStream(consoleStream, false);
        this.progressSilencePeriod = progressSilencePeriod;
        this.muteSet = muteSet;
        this.focusSet = focusSet;
    }

    /**
     * Creates a Log that uses this MiniLoggers configuration params with the name of the class where this method
     * has been called from. Note, packages are not part of generated Log names; also if the logNameLength parameter
     * is smaller than the class name, the name will be abbreviated.
     *
     * @return the Log created
     */
    public Log getLog() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String name = stackTraceElement.getClassName();
        return getLog(name.substring(name.lastIndexOf('.') + 1));
    }

    /**
     * Creates a Log that uses this MiniLoggers configuration params with the name specified. Note, if the
     * logNameLength parameter is smaller than the specified name, it will be abbreviated.
     *
     * @param name the name for the Log
     * @return the Log created
     */
    public Log getLog(String name) {
        return new Log(this, name);
    }

    private StringBuilder makeLine(String logName, String pattern, Object...params) {
        StringBuilder lineBuilder = new StringBuilder();

        if (this.timePattern != null) {
            lineBuilder.append(String.format(this.timePattern, Calendar.getInstance()))
                       .append(this.separator);
        }

        if (this.logNameLength > 0) {
            lineBuilder.append(String.format("%" + this.logNameLength + "." + this.logNameLength + "s", logName))
                       .append(this.separator);
        }

        lineBuilder.append(String.format(pattern, params));

        return lineBuilder;
    }

    private PrintStream fileStream = null;
    private String fileLastName = null;
    void toFileAndConsole(String logName, boolean isDebug, String pattern, Object... params) {
        if ((isDebug && this.muteSet.contains(logName)) ||
            (isDebug && !this.isDebugEnabled && !this.focusSet.contains(logName))) {
            // it is a muted logger or neither is debug enabled nor is this logger focused on. skip this debug message
            return;
        }

        // roll files if necessary
        if (this.fileNamePattern == null) {
            this.fileStream = null;
            this.fileLastName = null;
        } else {
            String newFileName = String.format(this.fileNamePattern, Calendar.getInstance());
            if (!newFileName.equals(fileLastName)) {
                fileLastName = newFileName;
                if (fileStream != null) {
                    fileStream.close();
                }
                try {
                    fileStream = new PrintStream(newFileName);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("cannot log to " + newFileName + "!", e);
                }
            }
        }

        // log to file
        StringBuilder line = makeLine(logName, pattern, params);
        if (this.fileStream != null) {
            this.fileStream.println(line);
            this.fileStream.flush();
        }

        // prolong message to cover possible previous progress and log to console
        if (this.consoleStream != null) {
            while (line.length() < this.progressLastLineLength) {
                line.append(' ');
            }
            this.consoleStream.println(line);
            this.consoleStream.flush();
        }

        // mark that previous progress is overwritted
        this.progressLastLineLength = 0;
    }

    private int progressLastLineLength = 0;
    private long progressLastAction = -1;
    void toConsoleNoNewline(String logName, String pattern, Object... params) {
        if (this.consoleStream == null ||
            this.progressLastAction + this.progressSilencePeriod > System.currentTimeMillis()) {
            // consoleStream is null or progress has just been updated, skip this
            return;
        }

        StringBuilder line = makeLine(logName, pattern, params);
        int currentLineLength = line.length();
        while (line.length() < this.progressLastLineLength) {
            line.append(' ');
        }
        this.progressLastLineLength = currentLineLength;
        this.progressLastAction = System.currentTimeMillis();
        line.append('\r');

        this.consoleStream.print(line);
        this.consoleStream.flush();
    }


    /**
     * Enables debugging for Logs from this MiniLogger, so that all debug messages from non-muted Loggers will
     * be logged.
     */
    public void enableDebug() {
        this.isDebugEnabled = true;
    }

    /**
     * Disables debugging for Logs from this MiniLogger, so that all debug messages from non-focused Loggers will
     * be skipped.
     */
    public void disableDebug() {
        this.isDebugEnabled = false;
    }

    /**
     * Adds the specified Log name to the list of focused Logs. Focused Logs are outputting debug statements,
     * unless they are muted, even if debug is disabled.
     *
     * @param name the Log name to focus on going forward
     */
    public void focus(String name) {
        this.focusSet.add(name);
    }

    /**
     * Removes the specified Log name from the list of focused Logs. Focused Logs are outputting debug statements,
     * unless they are muted, even if debug is disabled.
     *
     * @param name the Log name to no longer focus on going forward
     */
    public void unFocus(String name) {
        this.focusSet.remove(name);
    }

    /**
     * Adds the specified Log name to the list of muted Logs. Muted Logs are not outputting any debug statements,
     * even if debug is disabled.
     *
     * @param name the Log name to mute going forward
     */
    public void mute(String name) {
        this.muteSet.add(name);
    }

    /**
     * Removes the specified Log name from the list of muted Logs. Muted Logs are not outputting any debug statements,
     * even if debug is disabled.
     *
     * @param name the Log name to not mute anymore going forward
     */
    public void unMute(String name) {
        this.muteSet.remove(name);
    }

    /**
     * Updates the time pattern on this MiniLogger affecting all Logs created using this MiniLogger instance. Make
     * sure to use String.format patterns and specify the patterns to refer to the first parameter given.
     *
     * @param timePattern the time pattern to use
     */
    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    /**
     * Updates the separator on this MiniLogger. Separators are used between the time pattern and the Log name as well
     * as between the Log name and the message itself. Unnecessary separators are not used so that, e.g., if no time
     * pattern is specified, the separator between the time pattern and the log name is skipeed too.
     *
     * @param separator the new separator to use between the timestamp, the Log name, and the Log message.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Updates the Log name length on this MiniLogger so that for all future messages, the newly specified Log name
     * length will be used. Note, setting the log name length to zero removes separators that may become unnecessary.
     *
     * @param logNameLength the new logNameLength to pad or abbreviate future Log names to.
     */
    public void setLogNameLength(int logNameLength) {
        this.logNameLength = logNameLength;
    }

    /**
     * Updates the progress silence period, during which no subsequent progress statements are logged for performance
     * reasons, in milliseconds.
     *
     * @param progressSilencePeriod the new minimum period between to printed progress statements
     */
    public void setProgressSilencePeriod(long progressSilencePeriod) {
        this.progressSilencePeriod = progressSilencePeriod;
    }

    /**
     * Sets the file name pattern to log info and debug statements. If set to null, logging to file is disabled. If
     * the file name pattern changes at run time, a new file will be created. Use String.format patterns to introduce
     * a time component in the pattern so that log files are logged.
     *
     * @param fileNamePattern the file name pattern to use going forward
     */
    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    /**
     * Sets the console stream that recieves info, debug, and progress statements. If set to null, console output will
     * be skipped.
     *
     * @param consoleStream the console stream to write to going forward, usually System.out or System.err
     */
    public void setConsoleStream(OutputStream consoleStream) {
        this.consoleStream = consoleStream == null?null:new PrintStream(consoleStream, false);
    }
}
