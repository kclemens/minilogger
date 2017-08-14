package kc.utils.minilogger;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kclemens on 8/14/17.
 */
public class MiniLoggerBuilder {

    public static final String DEFAILT_TIME_PATTERN = "%1$tH:%1$tM:%1$tS.%1$tL";
    public static final String DEFAILT_SEPARATOR = "\t";
    public static final boolean DEFAILT_DEBUG_ENABLED = true;

    private String timePattern;
    private String separator;
    private boolean debugEnabled;

    private Set<String> muteSet;
    private Set<String> focusSet;
    private Set<PrintStream> logPrintStreams;
    private Set<PrintStream> progressPrintStreams;

    public MiniLoggerBuilder() {
        this.timePattern = DEFAILT_TIME_PATTERN;
        this.separator = DEFAILT_SEPARATOR;
        this.debugEnabled = DEFAILT_DEBUG_ENABLED;

        this.muteSet = new HashSet<String>();
        this.focusSet = new HashSet<String>();

        this.logPrintStreams = new HashSet<PrintStream>();
        try {
            logPrintStreams.add(new PrintStream("log.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot add file log", e);
        }

        this.progressPrintStreams = new HashSet<PrintStream>();
        this.progressPrintStreams.add(System.err);
    }

    public MiniLogger build() {
        return new MiniLogger(
                this.timePattern,
                this.separator,
                this.debugEnabled,
                this.muteSet,
                this.focusSet,
                this.logPrintStreams,
                this.progressPrintStreams);
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

    public MiniLoggerBuilder withLogPrintStreams(Set<PrintStream> logPrintStreams) {
        this.logPrintStreams = logPrintStreams;
        return this;
    }

    public MiniLoggerBuilder withProgressPrintStreams(Set<PrintStream> progressPrintStreams) {
        this.progressPrintStreams = progressPrintStreams;
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
}
