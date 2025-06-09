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
        int totalRequests = 0;
        int googlebotRequests = 0;
        int yandexbotRequests = 0;

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

            try (FileReader fileReader = new FileReader(file);
                 BufferedReader reader = new BufferedReader(fileReader)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    // Проверка на длину строки
                    if (line.length() > 1024) {
                        throw new LongLineException("Строка длиннее 1024 символов");
                    }
                    totalRequests++; // Увеличиваем общее количество запросов
                    parseLogLine(line, totalRequests);
                }

                // Выводим долю запросов от ботов
                if (totalRequests > 0) {
                    double googlebotPercentage = (double) googlebotRequests / totalRequests * 100;
                    double yandexbotPercentage = (double) yandexbotRequests / totalRequests * 100;
                    System.out.printf("Доля запросов от Googlebot: %.2f%%\n", googlebotPercentage);
                    System.out.printf("Доля запросов от YandexBot: %.2f%%\n", yandexbotPercentage);
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
        }
    }

    private static int[] parseLogLine(String line, int totalRequests) {
        int googlebotCount = 0;
        int yandexbotCount = 0;

        String[] parts = line.split(" "); // Разделяем строку на части

        if (parts.length < 9) {
            return new int[]{googlebotCount, yandexbotCount}; // Если строка не содержит достаточного количества данных, возвращаем нули
        }

        String ipAddress = parts[0]; // IP-адрес клиента
        String dateTime = parts[3] + " " + parts[4]; // Дата и время запроса
        String requestMethod = parts[5].substring(1); // Метод запроса (GET, POST и т.д.)
        String requestPath = parts[6]; // Путь запроса
        String httpResponseCode = parts[8]; // Код HTTP-ответа
        String dataSize = parts[9]; // Размер отданных данных в байтах
        String userAgent = parts[11]; // User-Agent

        // Поиск User-Agent в скобках
        if (userAgent.startsWith("") && userAgent.endsWith("")) {
            userAgent = userAgent.substring(1, userAgent.length() - 1);
        }

        String[] userAgentParts = userAgent.split("s*\\s*");
        if (userAgentParts.length > 1) {
            String firstBrackets = userAgentParts[1].split("\\)")[0]; // Часть в скобках

            String[] agentParts = firstBrackets.split(";");
            if (agentParts.length >= 2) {
                String fragment = agentParts[1].trim(); // Второй фрагмент
                String botName = fragment.split("/")[0].trim(); // Имя бота

                // Подсчет запросов от Googlebot и YandexBot
                if (botName.equalsIgnoreCase("Googlebot")) {
                    googlebotCount++;
                } else if (botName.equalsIgnoreCase("YandexBot")) {
                    yandexbotCount++;
                }
            }
        }
        return new int[]{googlebotCount, yandexbotCount}; // Возвращаем количество запросов от ботов
    }
}