package task2;

import task3.LanguageMenu;
import task3.Messages;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Консольний запуск шифрування: текст, ключ і шляхи до файлів вводяться з клавіатури.
 * Код із 5-ї лабораторної (завдання 5.3) з доданими логуванням і вибором мови.
 */
public class CipherMenu {

    private static final Logger LOG = AppLogging.get(CipherMenu.class);

    public static void run(Scanner in) {
        LOG.info("Відкрито меню шифрування");
        while (true) {
            System.out.println();
            System.out.println(Messages.get("cipher.title"));
            System.out.println(Messages.get("cipher.item1"));
            System.out.println(Messages.get("cipher.item2"));
            System.out.println(Messages.get("cipher.item3"));
            System.out.println(Messages.get("cipher.item4"));
            System.out.println(Messages.get("cipher.item0"));
            System.out.print(Messages.prompt("prompt.choice"));
            if (!in.hasNextLine()) {
                return;
            }
            String choice = in.nextLine().trim();
            LOG.fine("Меню шифрування, обрано пункт: " + choice);
            switch (choice) {
                case "1" -> textDemo(in);
                case "2" -> fileMode(in, true);
                case "3" -> fileMode(in, false);
                case "4" -> LanguageMenu.choose(in);
                case "0" -> {
                    LOG.info("Вихід з меню шифрування");
                    return;
                }
                default -> {
                    LOG.warning("Невідомий пункт меню: \"" + choice + "\"");
                    System.out.println(Messages.get("menu.unknown"));
                }
            }
        }
    }

    private static Character readKey(Scanner in) {
        LOG.finest("Запитуємо символ-ключ");
        System.out.print(Messages.prompt("cipher.key.prompt"));
        String s = in.nextLine();
        if (s.length() != 1) {
            LOG.warning("Ключ має довжину " + s.length() + ", потрібен рівно один символ");
            System.out.println(Messages.get("cipher.key.length"));
            return null;
        }
        if (Character.isSurrogate(s.charAt(0))) {
            LOG.warning("Сурогатний символ не може бути ключем");
            System.out.println(Messages.get("cipher.key.bad"));
            return null;
        }
        LOG.fine("Ключ прийнято, код символа " + (int) s.charAt(0));
        return s.charAt(0);
    }

    private static void textDemo(Scanner in) {
        Character key = readKey(in);
        if (key == null) {
            return;
        }
        System.out.print(Messages.prompt("cipher.text.prompt"));
        String text = in.nextLine();
        LOG.info("Шифрування рядка довжиною " + text.length());
        try {
            // шифруємо через EncryptingWriter, розшифровуємо через DecryptingReader
            StringWriter sink = new StringWriter();
            try (Writer w = new EncryptingWriter(sink, key)) {
                w.write(text);
            }
            String encrypted = sink.toString();
            LOG.fine("Рядок зашифровано, довжина результату " + encrypted.length());

            StringBuilder decrypted = new StringBuilder();
            try (var r = new DecryptingReader(new StringReader(encrypted), key)) {
                int c;
                while ((c = r.read()) != -1) {
                    decrypted.append((char) c);
                }
            }
            System.out.println(Messages.format("cipher.encrypted", visible(encrypted)));
            System.out.println(Messages.format("cipher.decrypted", decrypted));
            if (decrypted.toString().equals(text)) {
                LOG.info("Розшифрований текст збігається з оригіналом");
                System.out.println(Messages.get("cipher.match"));
            } else {
                LOG.severe("Розшифрований текст не збігається з оригіналом");
                System.out.println(Messages.get("cipher.mismatch"));
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Помилка потоку під час шифрування рядка", e);
            System.out.println(Messages.format("cipher.stream.error", e.getMessage()));
        }
    }

    private static void fileMode(Scanner in, boolean encrypt) {
        Character key = readKey(in);
        if (key == null) {
            return;
        }
        System.out.print(Messages.prompt("cipher.src.prompt"));
        String src = in.nextLine().trim();
        System.out.print(Messages.prompt("cipher.dst.prompt"));
        String dst = in.nextLine().trim();
        LOG.info((encrypt ? "Шифрування" : "Розшифрування") + " файлу " + src + " -> " + dst);
        try {
            Path source = Path.of(src);
            Path target = Path.of(dst);
            if (encrypt) {
                FileCipher.encryptFile(source, target, key);
            } else {
                FileCipher.decryptFile(source, target, key);
            }
            LOG.info("Файл оброблено: " + target + ", розмір " + Files.size(target) + " байт");
            System.out.println(Messages.format("cipher.done", target));
        } catch (InvalidPathException e) {
            LOG.warning("Некоректний шлях: " + e.getMessage());
            System.out.println(Messages.format("cipher.path.invalid", e.getMessage()));
        } catch (NoSuchFileException e) {
            LOG.warning("Файл не знайдено: " + e.getFile());
            System.out.println(Messages.format("cipher.file.notfound", e.getFile()));
        } catch (MalformedInputException e) {
            LOG.warning("Вхідний файл не є текстом у кодуванні UTF-8: " + src);
            System.out.println(Messages.get("cipher.file.notutf8"));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Помилка файлу " + src, e);
            System.out.println(Messages.format("cipher.file.error", e.getMessage()));
        }
    }

    /** Керівні й невидимі символи показуємо як \\uXXXX, щоб вивід не ламав консоль. */
    static String visible(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isISOControl(c) || !Character.isDefined(c) || Character.isSurrogate(c)
                    || Character.getType(c) == Character.PRIVATE_USE) {
                sb.append(String.format("\\u%04X", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        run(new Scanner(System.in));
    }
}
