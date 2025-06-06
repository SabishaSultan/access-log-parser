import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class LongLineException extends RuntimeException {
    public LongLineException(String message) {
        super(message);
    }
}

public class Main {
    public static void main(String[] args) {
        cycles();
    }

    public static void cycles() {
        int count = 0;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Введите путь к файлу: ");
            String path = scanner.nextLine();//запросить путь к консоли
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

            int lineCount = 0;
            int maxLength = 0;
            int minLength = Integer.MAX_VALUE;

            try(FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int length = line.length();
                // Проверка на длину строки
                if (length > 1024) {
                    throw new LongLineException ("Строка длиннее 1024 символов: " + length + " символов.");

            }
                lineCount++;
                if (length > maxLength) {
                    maxLength = length;
                }
                if (length < minLength) {
                    minLength = length;
                }
            }
                // Проверка, если файл пустой
                if (lineCount == 0) {
                    minLength = 0; // Если нет строк, минимальная длина будет 0
                }

            } catch (LongLineException ex) {
                    System.err.println("Ошибка: " + ex.getMessage());
                    return; // Завершаем выполнение программы
                } catch (IOException ex) {
                System.err.println("Ошибка чтения файла: " + ex.getMessage());
                return; // Завершаем выполнение программы
                } catch (Exception ex) {
                System.err.println("Произошла ошибка: " + ex.getMessage());
                return; // Завершаем выполнение программы
                }

            // Вывод результатов
            System.out.println("Общее количество строк: " + lineCount);
            System.out.println("Длина самой длинной строки: " + maxLength);
            System.out.println("Длина самой короткой строки: " + (minLength == Integer.MAX_VALUE ? 0 : minLength));


        }
    }
}