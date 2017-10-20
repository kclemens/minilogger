package kc.utils.minilogger;

/**
 * The Log class to use for logging. Features info and debug toFileAndConsole levels as well as a toConsoleNoNewline statement that will
 * be overwritten with any number of subsequent toConsoleNoNewline statements. An info or debug statement will show up an a
 * new line however.
 *
 * Get instances from a MiniLogger, or it's default root instance MiniLogger.root
 */
public class Log {

    private final MiniLogger miniLogger;
    private final String name;

    Log(MiniLogger miniLogger, String name) {
        this.miniLogger = miniLogger;
        this.name = name;
    }

    /**
     * Logs a message to the file and the console streams, if these are configured.
     *
     * @param pattern the pattern String that formats params, if any
     * @param params optional array of paramters that will be serialized using the specified pattern.
     */
    public void info(String pattern, Object...params) {
        this.miniLogger.toFileAndConsole(this.name, false, pattern, params);
    }

    /**
     * Logs a message to the file and the console streams, if these are configured, iff this Logs name is not
     * muted and either this Logs name is focused or debug statements are enabled.
     *
     * @param pattern the pattern String that formats params, if any
     * @param params optional array of paramters that will be serialized using the specified pattern.
     */
    public void debug(String pattern, Object...params) {
        this.miniLogger.toFileAndConsole(this.name, true, pattern, params);
    }

    /**
     * Logs a message to the console stream, if it is configured. The next call to this method will overwrite the
     * previous progress line on the console, thus allowing to display the progress of a long-lasting task, in, e.g.
     * percentages or anticipated time of completion. A subsequent call to info or debug, will overwrite the last
     * progress line.
     *
     * @param pattern the pattern String that formats params, if any
     * @param params optional array of parameters that will be serialized using the specified pattern.
     */
    public void progress(String pattern, Object... params) {
        this.miniLogger.toConsoleNoNewline(this.name, pattern, params);
    }
}
