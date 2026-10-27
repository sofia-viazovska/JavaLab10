package task3;

import org.junit.After;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class MessagesTest {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\d+}");

    @After
    public void restoreDefaultLanguage() {
        Messages.setLocale(Messages.SUPPORTED.get(0));
    }

    @Test
    public void allLanguagesHaveTheSameKeys() {
        ResourceBundle base = ResourceBundle.getBundle("resources.location.messages", Messages.SUPPORTED.get(0));
        for (Locale locale : Messages.SUPPORTED) {
            ResourceBundle other = ResourceBundle.getBundle("resources.location.messages", locale);
            assertEquals(locale.toLanguageTag(), new TreeSet<>(base.keySet()), new TreeSet<>(other.keySet()));
        }
    }

    @Test
    public void allLanguagesHaveTheSamePlaceholders() {
        ResourceBundle base = ResourceBundle.getBundle("resources.location.messages", Messages.SUPPORTED.get(0));
        for (Locale locale : Messages.SUPPORTED) {
            ResourceBundle other = ResourceBundle.getBundle("resources.location.messages", locale);
            for (String key : base.keySet()) {
                assertEquals(locale.toLanguageTag() + " " + key,
                        placeholders(base.getString(key)), placeholders(other.getString(key)));
            }
        }
    }

    @Test
    public void noValueIsEmpty() {
        for (Locale locale : Messages.SUPPORTED) {
            ResourceBundle bundle = ResourceBundle.getBundle("resources.location.messages", locale);
            for (String key : bundle.keySet()) {
                assertFalse(locale.toLanguageTag() + " " + key, bundle.getString(key).isBlank());
            }
        }
    }

    @Test
    public void mainTitleIsFramedByEqualSigns() {
        for (Locale locale : Messages.SUPPORTED) {
            String title = ResourceBundle.getBundle("resources.location.messages", locale).getString("main.title");
            assertTrue(locale.toLanguageTag(), title.startsWith("=== ") && title.endsWith(" ==="));
        }
    }

    @Test
    public void switchingLanguageChangesTexts() {
        Messages.setLocale(Locale.forLanguageTag("uk"));
        String uk = Messages.get("main.item0");
        Messages.setLocale(Locale.ENGLISH);
        String en = Messages.get("main.item0");
        Messages.setLocale(Locale.GERMAN);
        String de = Messages.get("main.item0");
        assertEquals("0. Вихід", uk);
        assertEquals("0. Exit", en);
        assertEquals("0. Beenden", de);
        assertNotEquals(uk, en);
    }

    @Test
    public void formatInsertsArguments() {
        Messages.setLocale(Locale.ENGLISH);
        assertEquals("Done: out.txt", Messages.format("cipher.done", "out.txt"));
    }

    @Test
    public void nativeNames() {
        assertEquals("Українська", Messages.nativeName(Messages.SUPPORTED.get(0)));
        assertEquals("English", Messages.nativeName(Locale.ENGLISH));
        assertEquals("Deutsch", Messages.nativeName(Locale.GERMAN));
    }

    private static Set<String> placeholders(String text) {
        Set<String> result = new TreeSet<>();
        Matcher m = PLACEHOLDER.matcher(text);
        while (m.find()) {
            result.add(m.group());
        }
        return result;
    }
}
