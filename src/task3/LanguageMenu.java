package task3;

import task2.AppLogging;

import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Logger;

/** Пункт меню «Вибір мови»: показує список мов і перемикає інтерфейс. */
public final class LanguageMenu {

    private static final Logger LOG = AppLogging.get(LanguageMenu.class);

    private LanguageMenu() {
    }

    public static void choose(Scanner in) {
        System.out.println();
        System.out.println(Messages.get("lang.title"));
        for (int i = 0; i < Messages.SUPPORTED.size(); i++) {
            Locale locale = Messages.SUPPORTED.get(i);
            String mark = locale.equals(Messages.getLocale()) ? " *" : "";
            System.out.println((i + 1) + ". " + Messages.nativeName(locale) + mark);
        }
        System.out.print(Messages.prompt("lang.prompt"));
        if (!in.hasNextLine()) {
            return;
        }
        String line = in.nextLine().trim();
        try {
            int index = Integer.parseInt(line) - 1;
            Locale locale = Messages.SUPPORTED.get(index);
            Messages.setLocale(locale);
            LOG.info("Мову інтерфейсу змінено на " + locale.toLanguageTag());
            System.out.println(Messages.format("lang.changed", Messages.nativeName(locale)));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            LOG.warning("Некоректний вибір мови: \"" + line + "\"");
            System.out.println(Messages.get("lang.invalid"));
        }
    }
}
