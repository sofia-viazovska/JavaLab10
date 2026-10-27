package task2;

import task3.Messages;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Демонстрація рівнів логування: по одному повідомленню кожного рівня.
 * До консолі й до файлу доходять не всі: частину відсіює реєстратор, частину обробники.
 */
public final class LogDemo {

    private static final Logger LOG = AppLogging.get(LogDemo.class);

    private static final Level[] LEVELS = {
            Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST};

    private LogDemo() {
    }

    public static void run() {
        System.out.println();
        System.out.println(Messages.get("logdemo.title"));
        System.out.println(Messages.format("logdemo.setup",
                AppLogging.loggerLevel(), AppLogging.consoleLevel(), AppLogging.fileLevel()));

        for (Level level : LEVELS) {
            LOG.log(level, "Демонстрація: повідомлення рівня " + level.getName());
        }
        AppLogging.flush();

        for (Level level : LEVELS) {
            System.out.println(Messages.format("logdemo.row", level.getName(),
                    yesNo(AppLogging.reachesConsole(level)), yesNo(AppLogging.reachesFile(level))));
        }

        System.out.println(Messages.format("logdemo.tail", AppLogging.LOG_FILE));
        List<String> lines = AppLogging.tail(5);
        if (lines.isEmpty()) {
            System.out.println(Messages.get("logdemo.nofile"));
        }
        lines.forEach(System.out::println);
    }

    private static String yesNo(boolean value) {
        return Messages.get(value ? "common.yes" : "common.no");
    }
}
