import task1.Task1Main;
import task2.AppLogging;
import task2.CipherMenu;
import task2.LogDemo;
import task3.LanguageMenu;
import task3.Messages;

import java.util.Scanner;
import java.util.logging.Logger;

/** Лабораторна 10: рефлексія, логування, інтернаціоналізація. Головне меню. */
public class Main {

    private static final Logger LOG = AppLogging.get(Main.class);

    public static void main(String[] args) {
        LOG.info("Програму запущено");
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println(Messages.get("main.title"));
            System.out.println(Messages.get("main.item1"));
            System.out.println(Messages.get("main.item2"));
            System.out.println(Messages.get("main.item3"));
            System.out.println(Messages.get("main.item4"));
            System.out.println(Messages.get("main.item0"));
            System.out.print(Messages.prompt("prompt.choice"));
            if (!in.hasNextLine()) {
                LOG.info("Введення завершено, вихід з програми");
                return;
            }
            String choice = in.nextLine().trim();
            LOG.fine("Головне меню, обрано пункт: " + choice);
            switch (choice) {
                case "1" -> Task1Main.run(in);
                case "2" -> CipherMenu.run(in);
                case "3" -> LogDemo.run();
                case "4" -> LanguageMenu.choose(in);
                case "0" -> {
                    LOG.info("Програму завершено");
                    return;
                }
                default -> {
                    LOG.warning("Невідомий пункт головного меню: \"" + choice + "\"");
                    System.out.println(Messages.get("menu.unknown"));
                }
            }
        }
    }
}
