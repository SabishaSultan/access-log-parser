import java.io.File;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        cycles();
    }

    public static void cycles() {
        int count = 0;
        while (true) {
            System.out.println("Введите путь к файлу: ");
            String path = new Scanner(System.in).nextLine();//запросить путь к консоли
            System.out.println("Путь к папке " + path);
            File file = new File(path);
            boolean fileExists = file.exists(); //существует ли файл, путь к котрому был указан
            boolean isDirectory = file.isDirectory(); //является ли указанный путь путём именно к файлу, а не к папке.
            System.out.println("Указывает введенный путь к директории " + isDirectory);
            if (!fileExists || isDirectory) {
                System.out.println("Указанный файл не существует или это путь к папке." + path);
                continue; // Продолжаем цикл, если файл не существует или это папка
            } else {
                System.out.println("Путь указан верно.");
                count++;
                System.out.println("Количество успешных запросов= " + count);
            }
        }
    }
}