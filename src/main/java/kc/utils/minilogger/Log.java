package kc.utils.minilogger;

import java.util.Calendar;

/**
 * The Log class to use for logging. Features info and debug log levels as well as a progress statement that will
 * be overwritten with any number of subsequent progress statements. An info or debug statement will show up an a
 * new line however.
 *
 * Get instances from a MiniLogger, or it's default root instance MiniLogger.root
 */
public class Log {

    private final MiniLogger logger;
    private final String name;

    Log(MiniLogger logger, String name) {
        this.logger = logger;
        this.name = name;
    }

    private String makeLine(String text, Object...params) {
        return String.format(this.logger.getTimePattern(), Calendar.getInstance()) +
               String.format(this.logger.getNamePattern(), this.name) +
               String.format(text, params);
    }

    /**
     * Logs a message to the file and the console streams, if these are configured.
     *
     * @param pattern the pattern String that formats params, if any
     * @param params optional array of paramters that will be serialized using the specified pattern.
     */
    public void info(String pattern, Object...params) {
        this.logger.log(makeLine(pattern, params));
    }

    /**
     * Logs a message to the file and the console streams, if these are configured, only iff this Logs name is not
     * muted and either this Logs name is focused or debug statements are enabled.
     *
     * @param pattern the pattern String that formats params, if any
     * @param params optional array of paramters that will be serialized using the specified pattern.
     */
    public void debug(String pattern, Object...params) {
        boolean isMuted = this.logger.getMuteSet().contains(this.name);
        boolean isFocused = this.logger.getFocusSet().contains(this.name);
        boolean isEnabled = this.logger.isDebugEnabled();

        if (!isMuted && (isEnabled || isFocused)) {
            this.logger.log(makeLine(pattern, params));
        }
    }

    /**
     * Logs a message to the console stream, if it is configured. A subsequent call to this method will overwrite the
     * last message output via this method. A subsequent call to info (or, if enabled, debug) will not overwrite the
     * last progress statement.
     *
     * @param pattern the pattern String that formats params, if any
     * @param params optional array of parameters that will be serialized using the specified pattern.
     */
    public void progress(String pattern, Object... params) {
        this.logger.progress(makeLine(pattern, params));
    }
}
