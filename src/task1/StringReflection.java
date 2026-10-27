package task1;

import java.lang.reflect.Field;

/**
 * Зміна вмісту рядка через рефлексію. Об'єкт String незмінний на рівні API, але всередині
 * він зберігає символи в приватному полі value, до якого рефлексія дає доступ.
 * <p>
 * У Java 8 це char[] value, починаючи з Java 9 це byte[] value разом з полем coder
 * (кодування: Latin-1 або UTF-16). Доступ до приватних полів java.base потребує
 * VM-параметра --add-opens java.base/java.lang=ALL-UNNAMED.
 */
public final class StringReflection {

    private StringReflection() {
    }

    /** Тип приватного поля value: char[] (Java 8) або byte[] (Java 9+). */
    public static String valueFieldType() throws ReflectiveOperationException {
        return privateField("value").getType().getSimpleName();
    }

    /**
     * Замінює вміст target вмістом replacement. Це той самий об'єкт, змінюється лише його внутрішній масив.
     */
    public static void replaceContent(String target, String replacement) throws ReflectiveOperationException {
        Field value = privateField("value");
        Object newValue = value.get(replacement);
        if (newValue instanceof byte[] bytes) {
            newValue = bytes.clone();
        } else if (newValue instanceof char[] chars) {
            newValue = chars.clone();
        }
        value.set(target, newValue);

        // поле coder є лише з Java 9: воно каже, як саме закодовано масив value
        try {
            Field coder = privateField("coder");
            coder.setByte(target, coder.getByte(replacement));
        } catch (NoSuchFieldException ignored) {
            // Java 8: поля coder немає
        }

        // скидаємо закешований hashCode, інакше він лишився б від старого вмісту
        Field hash = privateField("hash");
        hash.setInt(target, 0);
        try {
            privateField("hashIsZero").setBoolean(target, false);
        } catch (NoSuchFieldException ignored) {
            // поле hashIsZero з'явилося лише в нових версіях Java
        }
    }

    private static Field privateField(String name) throws ReflectiveOperationException {
        Field field = String.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
