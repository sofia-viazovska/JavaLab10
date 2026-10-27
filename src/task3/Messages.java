package task3;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Інтернаціоналізація інтерфейсу: тексти беруться з ResourceBundle,
 * файли messages_uk / messages_en / messages_de лежать у resources/location.
 */
public final class Messages {

    public static final List<Locale> SUPPORTED =
            List.of(Locale.forLanguageTag("uk"), Locale.ENGLISH, Locale.GERMAN);

    private static final String BASE_NAME = "resources.location.messages";

    private static Locale current = SUPPORTED.get(0);
    private static ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, current);

    private Messages() {
    }

    public static Locale getLocale() {
        return current;
    }

    public static void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle(BASE_NAME, locale);
        current = locale;
    }

    /** Текст за ключем у поточній мові. */
    public static String get(String key) {
        return bundle.getString(key);
    }

    /** Текст за ключем, у який підставлено аргументи на місце {0}, {1}, ... */
    public static String format(String key, Object... args) {
        return MessageFormat.format(get(key), args);
    }

    /** Запрошення до вводу: текст за ключем і пробіл після нього. */
    public static String prompt(String key) {
        return get(key) + " ";
    }

    /** Назва мови нею самою: Українська, English, Deutsch. */
    public static String nativeName(Locale locale) {
        String name = locale.getDisplayLanguage(locale);
        return name.substring(0, 1).toUpperCase(locale) + name.substring(1);
    }
}
