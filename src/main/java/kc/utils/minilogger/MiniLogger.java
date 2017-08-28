package kc.utils.minilogger;

import java.io.PrintStream;

import java.util.Set;

/**
 * The configurable Logger that  log to.
 */
public class MiniLogger {

    public static final MiniLogger root = MiniLoggerBuilder.fromFile().build();

    private boolean debugEnabled;
    private String timePattern;
    private String separator;
    private int logNameLength;
    private String namePattern;
    private int lastProgressLineLength = 0;
    private final Set<String> muteSet;
    private final Set<String> focusSet;
    private final PrintStream logPrintStream;
    private final PrintStream progressPrintStream;

    public MiniLogger(String timePattern, String separator, int logNameLength, boolean debugEnabled, Set<String> muteSet, Set<String> focusSet, PrintStream logPrintStreams, PrintStream progressPrintStreams) {
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

    /** generate log instances **/
    public Log getLog() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String name = stackTraceElement.getClassName();
        return getLog(name.substring(name.lastIndexOf('.') + 1));
    }

    public Log getLog(String name) {
        if (name.length() > this.logNameLength) {
            name = name.substring(0, this.logNameLength - 2) + "..";
        }

        return new Log(this, name);
    }

    /** public configuration changable at runtime **/
    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public void addFocus(String name) {
        this.focusSet.add(name);
    }

    public void removeFocus(String name) {
        this.focusSet.remove(name);
    }

    public void addMute(String name) {
        this.muteSet.add(name);
    }

    public void removeMute(String name) {
        this.muteSet.remove(name);
    }

    public void setSeparator(String separator) {
        this.separator = separator;
        this.namePattern = this.separator + "%" + this.logNameLength + "s" + this.separator;
    }

    public void setLogNameLength(int logNameLength) {
        this.logNameLength = logNameLength;
        this.namePattern = this.separator + "%" + this.logNameLength + "s" + this.separator;
    }

    /** internal methods used by the logger **/
    void log(String line) {
        if (this.progressPrintStream != null) {
            if (this.lastProgressLineLength > 0) {
                this.progressPrintStream.print('\n');
            }
            this.progressPrintStream.print(line);
            this.progressPrintStream.print('\n');
            this.progressPrintStream.flush();
        }

        if (this.logPrintStream != null) {
            this.logPrintStream.print(line);
            this.logPrintStream.print('\n');
            this.logPrintStream.flush();
        }
    }

    void progress(String line) {
        if (this.lastProgressLineLength > 0) {
            String prolongedLine = String.format("%-" + this.lastProgressLineLength + "s", line);
            this.lastProgressLineLength = line.length();
            line = prolongedLine;
        } else {
            this.lastProgressLineLength = line.length();
        }

        progressPrintStream.print(line);
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
