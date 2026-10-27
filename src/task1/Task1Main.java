package task1;

import task2.AppLogging;
import task3.Messages;

import java.lang.reflect.InaccessibleObjectException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Завдання 1: консольний додаток, який змінює значення рядка через рефлексію. */
public class Task1Main {

    private static final Logger LOG = AppLogging.get(Task1Main.class);

    /** Рядок-літерал: один спільний об'єкт для всіх місць коду з таким самим текстом. */
    static final String LITERAL = "Привіт, світе!";

    /** Значення для заміни, задане програмно. */
    static final String PROGRAM_REPLACEMENT = "Рефлексія працює!";

    public static void run(Scanner in) {
        System.out.println();
        System.out.println(Messages.get("refl.title"));

        System.out.println(Messages.get("refl.source.title"));
        System.out.println("1. " + Messages.get("refl.source.literal"));
        System.out.println("2. " + Messages.get("refl.option.keyboard"));
        System.out.print(Messages.prompt("prompt.choice"));
        if (!in.hasNextLine()) {
            return;
        }
        boolean literalMode = !in.nextLine().trim().equals("2");

        String text;
        if (literalMode) {
            text = LITERAL;
        } else {
            String typed;
            do {
                System.out.print(Messages.prompt("refl.prompt.text"));
                if (!in.hasNextLine()) {
                    return;
                }
                typed = in.nextLine();
                if (typed.isEmpty()) {
                    System.out.println(Messages.get("refl.empty"));
                }
            } while (typed.isEmpty());
            // новий об'єкт String, не пов'язаний з жодним літералом
            text = new String(typed.toCharArray());
        }
        LOG.info("Рядок створено " + (literalMode ? "як літерал" : "введенням з клавіатури"));

        System.out.println(Messages.get("refl.replace.title"));
        System.out.println("1. " + Messages.format("refl.replace.program", PROGRAM_REPLACEMENT));
        System.out.println("2. " + Messages.get("refl.option.keyboard"));
        System.out.print(Messages.prompt("prompt.choice"));
        if (!in.hasNextLine()) {
            return;
        }
        String replacement = PROGRAM_REPLACEMENT;
        if (in.nextLine().trim().equals("2")) {
            System.out.print(Messages.prompt("refl.prompt.new"));
            if (!in.hasNextLine()) {
                return;
            }
            replacement = in.nextLine();
        }

        try {
            System.out.println(Messages.format("refl.field", StringReflection.valueFieldType()));
            int hashBefore = System.identityHashCode(text);
            System.out.println(Messages.format("refl.before", text, String.valueOf(text.length()), String.valueOf(hashBefore)));

            LOG.fine("Змінюємо вміст рядка через рефлексію на \"" + replacement + "\"");
            StringReflection.replaceContent(text, replacement);

            int hashAfter = System.identityHashCode(text);
            System.out.println(Messages.format("refl.after", text, String.valueOf(text.length()), String.valueOf(hashAfter)));
            if (hashBefore == hashAfter) {
                System.out.println(Messages.get("refl.same"));
            }
            if (literalMode) {
                // той самий літерал в іншому місці коду: це той самий об'єкт, тож він теж змінився
                System.out.println(Messages.format("refl.literal.shared", LITERAL));
            }
            LOG.info("Вміст рядка змінено");
        } catch (InaccessibleObjectException e) {
            LOG.log(Level.SEVERE, "Немає доступу до приватного поля String (потрібен --add-opens)", e);
            System.out.println(Messages.get("refl.error.access"));
        } catch (ReflectiveOperationException e) {
            LOG.log(Level.SEVERE, "Помилка рефлексії", e);
            System.out.println(Messages.format("refl.error", e.toString()));
        }
    }
}
