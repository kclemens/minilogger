package kc.utils.minilogger;

import java.io.PrintStream;

import java.util.Set;

/**
 * The configurable Logger that  log to.
 */
public class MiniLogger {

    public static final MiniLogger root = new MiniLoggerBuilder().build();

    private boolean debugEnabled;
    private String timePattern;
    private String separator;
    private int logNameLength;
    private String namePattern;

    private final Set<String> muteSet;
    private final Set<String> focusSet;
    private final Set<PrintStream> logPrintStreams;
    private final Set<PrintStream> progressPrintStreams;

    public MiniLogger(String timePattern, String separator, int logNameLength, boolean debugEnabled, Set<String> muteSet, Set<String> focusSet, Set<PrintStream> logPrintStreams, Set<PrintStream> progressPrintStreams) {
        this.timePattern = timePattern;
        this.separator = separator;
        this.debugEnabled = debugEnabled;
        this.logNameLength = logNameLength;
        this.muteSet = muteSet;
        this.focusSet = focusSet;
        this.logPrintStreams = logPrintStreams;
        this.progressPrintStreams = progressPrintStreams;

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
        for (PrintStream printStream : logPrintStreams) {
            printStream.print(line);
            printStream.print('\n');
            printStream.flush();
        }
        for (PrintStream printStream : progressPrintStreams) {
            printStream.print(line);
            printStream.print('\n');
            printStream.flush();
        }
    }

    void progress(String line) {
        for (PrintStream printStream : progressPrintStreams) {
            printStream.print(line);
            printStream.print('\r');
            printStream.flush();
        }
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
