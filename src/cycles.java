import java.io.File;
import java.lang.reflect.Field;
import java.util.Scanner;

public class cycles {
    public static void main(String[] args) {
        cycles();
    }

    public static void cycles() {
        int count = 0;
        while (true) {
            System.out.print("Введите путь к файлу: ");
            String path = new Scanner(System.in).nextLine();//запросить путь к консоли
            count++;
            File file = new File(path);
            boolean fileExists = file.exists(); //существует ли файл, путь к котрому был указан
            boolean isDirectory = file.isDirectory(); //является ли указанный путь путём именно к файлу, а не к папке.
            if (!fileExists || !isDirectory) {
                System.out.println("Указанный файл не существует или это путь к папке.");
                continue; // Продолжаем цикл, если файл не существует или это папка
            } else {
                System.out.println("Путь указан верно.");
                break;
            }
        }
        System.out.println("Это файл номер " + count);
    }
}

