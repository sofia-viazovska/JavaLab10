package task2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Налаштування системи логування (java.util.logging).
 * <p>
 * Головний реєстратор "lab10" має рівень FINE, тобто пропускає повідомлення від FINE і вище
 * (FINER та FINEST відсіюються ще тут). У нього два обробники:
 * консольний (рівень INFO: у консоль іде хід виконання і помилки) і файловий (рівень FINE:
 * у файл іде ще й детальна діагностика). Реєстратори окремих класів "lab10.ІмяКласу"
 * успадковують і рівень, і обробники від головного.
 */
public final class AppLogging {

    public static final String ROOT_NAME = "lab10";
    public static final Path LOG_FILE = Path.of("logs", "lab10.log");

    private static final Level LOGGER_LEVEL = Level.FINE;
    private static final Level CONSOLE_LEVEL = Level.INFO;
    private static final Level FILE_LEVEL = Level.FINE;

    private static Handler consoleHandler;
    private static Handler fileHandler;

    private AppLogging() {
    }

    /** Реєстратор для класу. Налаштування виконуються при першому виклику. */
    public static Logger get(Class<?> owner) {
        init();
        return Logger.getLogger(ROOT_NAME + "." + owner.getSimpleName());
    }

    private static synchronized void init() {
        if (consoleHandler != null) {
            return;
        }
        // формат рядка: дата час [РІВЕНЬ] реєстратор - повідомлення
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT.%1$tL [%4$s] %3$s - %5$s%6$s%n");

        Logger root = Logger.getLogger(ROOT_NAME);
        root.setUseParentHandlers(false);
        root.setLevel(LOGGER_LEVEL);

        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(CONSOLE_LEVEL);
        consoleHandler.setFormatter(new SimpleFormatter());
        root.addHandler(consoleHandler);

        try {
            Files.createDirectories(LOG_FILE.getParent());
            fileHandler = new FileHandler(LOG_FILE.toString(), true);
            fileHandler.setEncoding(StandardCharsets.UTF_8.name());
            fileHandler.setLevel(FILE_LEVEL);
            fileHandler.setFormatter(new SimpleFormatter());
            root.addHandler(fileHandler);
        } catch (IOException e) {
            root.log(Level.WARNING, "Не вдалося відкрити файл логу " + LOG_FILE, e);
        }
    }

    public static Level loggerLevel() {
        return LOGGER_LEVEL;
    }

    public static Level consoleLevel() {
        return CONSOLE_LEVEL;
    }

    public static Level fileLevel() {
        return FILE_LEVEL;
    }

    /** Чи дійде повідомлення цього рівня до консолі: його пропустить і реєстратор, і консольний обробник. */
    public static boolean reachesConsole(Level level) {
        return level.intValue() >= LOGGER_LEVEL.intValue() && level.intValue() >= CONSOLE_LEVEL.intValue();
    }

    /** Чи дійде повідомлення цього рівня до файлу: його пропустить і реєстратор, і файловий обробник. */
    public static boolean reachesFile(Level level) {
        return level.intValue() >= LOGGER_LEVEL.intValue() && level.intValue() >= FILE_LEVEL.intValue();
    }

    public static synchronized void flush() {
        init();
        consoleHandler.flush();
        if (fileHandler != null) {
            fileHandler.flush();
        }
    }

    /** Останні n рядків файлу логу (порожній список, якщо файлу ще немає). */
    public static List<String> tail(int n) {
        flush();
        try {
            List<String> lines = Files.readAllLines(LOG_FILE, StandardCharsets.UTF_8);
            return lines.subList(Math.max(0, lines.size() - n), lines.size());
        } catch (IOException e) {
            return List.of();
        }
    }
}
