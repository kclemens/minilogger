package kc.utils.minilogger;

import java.util.Calendar;

/**
 * Created by kclemens on 8/12/17.
 */
public class Log {

    private final MiniLogger logger;
    private final String name;

    protected Log(MiniLogger logger, String name) {
        this.logger = logger;
        this.name = name;
    }

    private String makeLine(String text, Object...params) {
        return String.format(this.logger.getTimePattern(), Calendar.getInstance()) +
                             this.logger.getSeparator() +
                             this.name +
                             this.logger.getSeparator() +
                             String.format(text, params);
    }

    public void info(String text, Object...params) {
        this.logger.log(makeLine(text, params));
    }

    public void debug(String text, Object...params) {
        boolean isMuted = this.logger.getMuteSet().contains(this.name);
        boolean isFocused = this.logger.getFocusSet().contains(this.name);

        if (!isMuted && (this.logger.isDebugEnabled() || isFocused)) {
            this.logger.log(makeLine(text, params));
        }
    }

    public void progress(String text, Object... params) {
        this.logger.progress(makeLine(text, params));
    }

    public String getName() {
        return name;
    }
}
