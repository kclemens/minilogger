package kc.utils.minilogger;

import java.io.PrintStream;

import java.util.Set;

/**
 * The configurable Logger that manages configuration for Log instances.
 */
public class MiniLogger {

    public static final MiniLogger ROOT = MiniLoggerBuilder.fromFile().build();

    private boolean debugEnabled;
    private String timePattern;
    private String separator;
    private int logNameLength;
    private String namePattern;
    private String lastProgressLine;

    private final Set<String> muteSet;
    private final Set<String> focusSet;
    private final PrintStream logPrintStream;
    private final PrintStream progressPrintStream;

    MiniLogger(String timePattern, String separator, int logNameLength, boolean debugEnabled, Set<String> muteSet, Set<String> focusSet, PrintStream logPrintStreams, PrintStream progressPrintStreams) {
        this.timePattern = timePattern;
        this.separator = separator;
        this.debugEnabled = debugEnabled;
        this.logNameLength = logNameLength;
        this.muteSet = muteSet;
        this.focusSet = focusSet;
        this.logPrintStream = logPrintStreams;
        this.progressPrintStream = progressPrintStreams;

        this.namePattern = separator + "%" + logNameLength + "s" + separator;
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
        if (name.length() > this.logNameLength) {
            name = name.substring(0, this.logNameLength - 2) + "..";
        }

        return new Log(this, name);
    }

    /**
     * Update the time pattern on this MiniLogger affecting all Logs created using this MiniLogger instance.
     *
     * @param timePattern the time pattern to use
     */
    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    /**
     * Update the debug flag on this MiniLogger affecting all Logs created using this MiniLogger instance.
     *
     * @param debugEnabled true to enable debugging, or false to disable it
     */
    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    /**
     * Update the focus list on this MiniLogger affecting all Logs created using this MiniLogger instance. Focused
     * Logs are outputting debug statements if they are not muted, even if debug is disabled.
     *
     * @param name the Log name to focus on
     */
    public void focus(String name) {
        this.focusSet.add(name);
    }

    /**
     * Update the focus list on this MiniLogger affecting all Logs created using this MiniLogger instance. Focused
     * Logs are outputting debug statements if they are not muted, even if debug is disabled.
     *
     * @param name the Log name to no longer focus on
     */
    public void unFocus(String name) {
        this.focusSet.remove(name);
    }

    /**
     * Update the mute list on this MiniLogger affecting all Logs created using this MiniLogger instance. Muted Logs
     * are not outputting debug statements, even if debug is enabled, or the logs are focused.
     *
     * @param name the Log name to mute
     */
    public void mute(String name) {
        this.muteSet.add(name);
    }

    /**
     * Update the mute list on this MiniLogger affecting all Logs created using this MiniLogger instance. Muted Logs
     * are not outputting debug statements, even if debug is enabled, or the logs are focused.
     *
     * @param name the Log name to no longer mute
     */
    public void unMute(String name) {
        this.muteSet.remove(name);
    }

    /**
     * Update the separator on this MiniLogger affecting all Logs created using this MiniLogger instance.
     *
     * @param separator the new separator to use between the timestamp, the Log name, and the Log message.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
        this.namePattern = this.separator + "%" + this.logNameLength + "s" + this.separator;
    }

    /**
     * Update the log name length on this MiniLogger affecting all Logs created using this MiniLogger instance. Note,
     * increasing the logNameLength will not expand already abbreviated Log names. Similarly, decreasing the log name
     * length, will only abbreviate longer names of newly created Logs.
     *
     * @param logNameLength the new logNameLength to pad or abbreviate future Log names to.
     */
    public void setLogNameLength(int logNameLength) {
        this.logNameLength = logNameLength;
        this.namePattern = this.separator + "%" + this.logNameLength + "s" + this.separator;
    }

    void log(String line) {
        if (this.progressPrintStream != null) {
            if (this.lastProgressLine != null) {
                this.progressPrintStream.print('\n');
            }
            this.progressPrintStream.print(line);
            this.progressPrintStream.print('\n');
            this.progressPrintStream.flush();
        }

        if (this.logPrintStream != null) {
            if (this.lastProgressLine != null) {
                this.logPrintStream.print(this.lastProgressLine);
                this.logPrintStream.print('\n');
            }
            this.logPrintStream.print(line);
            this.logPrintStream.print('\n');
            this.logPrintStream.flush();
        }

        this.lastProgressLine = null;
    }

    void progress(String line) {
        String eventuallyProlongedLine = line;
        if (this.lastProgressLine != null) {
            eventuallyProlongedLine = String.format("%-" + this.lastProgressLine.length() + "s", line);
        }
        this.lastProgressLine = line;

        progressPrintStream.print(eventuallyProlongedLine);
        progressPrintStream.print('\r');
        progressPrintStream.flush();
    }

    String getTimePattern() {
        return timePattern;
    }

    boolean isDebugEnabled() {
        return this.debugEnabled;
    }

    Set<String> getFocusSet() {
        return this.focusSet;
    }

    Set<String> getMuteSet() {
        return muteSet;
    }

    String getNamePattern() {
        return this.namePattern;
    }
}
