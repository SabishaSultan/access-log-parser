import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

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
        Statistics statistics = new Statistics(); // Создаем объект Statistics
        // int totalRequests = 0;
        //int googlebotRequests = 0;
        //int yandexbotRequests = 0;

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
                    String[] parts = line.split(" "); // Предполагаем, что строка имеет формат: [ip] [user-agent] [status_code]
                    if (parts.length < 8) {
                        System.err.println("Неверный формат строки: " + line);
                        continue; // Пропускаем строку с неверным форматом
                    }
                    String ipAddr = parts[1]; // IP-адрес клиента
                    String time = parts[2]; // Дата и время запроса
                    String method = parts[3].substring(1); // Метод запроса (GET, POST и т.д.)
                    String paths = parts[4]; // Путь запроса
                    String responseCode = parts[5]; // Код HTTP-ответа
                    String responseSize = parts[6]; // Размер отданных данных в байтах
                    String referer = parts[7];
                    String userAgent = parts[8]; // User-Agent

                    //statistics.incrementHours(); // Увеличиваем количество часов, если есть данные за этот час

                    //statistics.addVisit(userAgent, ipAddr, referer, Integer.parseInt(time));

                    LogEntry entry = new LogEntry(line);
                    statistics.addEntry(entry);



                }
                System.out.println("Total Traffic: " + statistics.totalTraffic);
                System.out.println("Traffic Rate: " + statistics.getTrafficRate());

                // Получение существующих страниц
                // System.out.println("Существующие страницы: " + statistics.getListPages());

                // Получение несуществующих страниц
                System.out.println("Несуществующие страницы: " + statistics.getNonListPages());

                // Получение статистики операционных систем
                System.out.println("Статистика ОС: " + statistics.getOSDistribution());

                // Получение статистики браузеров
                System.out.println("Статистика браузеров: " + statistics.getBrowserDistribution());

                System.out.println("Среднее количество посещений за час: " + statistics.averageVisitsPerHour());
                System.out.println("Среднее количество ошибочных запросов за час: " + statistics.averageErrorsPerHour());
                System.out.println("Среднее количество посещений на уникальный IP: " + statistics.averageVisitsPerUniqueUser());



                // Проверяем пик посещаемости
                // int visitsPerSecond = statistics.getPeakVisitsPerSecond();
                //System.out.println("Пиковая посещаемость в секунду: " + visitsPerSecond);

                // Проверяем максимальную посещаемость одним пользователем
               // int userVisits = statistics.getMaxVisitsByUser();
                //System.out.println("Максимальная посещаемость одним пользователем: " + userVisits);

                // Получаем список доменов рефереров
               // Set<String> referrerDomains = statistics.getReferrerDomains();
               // System.out.println("Сайты, со страниц которых есть ссылки на текущий сайт: " + referrerDomains);

                // Проверяем максимальную посещаемость одним пользователем
                //int maxVisitsByUser = statistics.getMaxVisitsByUser();
               // System.out.println("Максимальная посещаемость одним пользователем: " + maxVisitsByUser);

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
}


//totalRequests++; // Увеличиваем общее количество запросов
//parseLogLine(line, totalRequests);

// Проверяем, является ли запрос от бота
// if (isBotRequest(line)) {
//   if (line.contains("Googlebot")) {
//      googlebotRequests++;
//    } else if (line.contains("YandexBot")) {
//      yandexbotRequests++;
//     }
//  }


// Выводим долю запросов от ботов
//  if (totalRequests > 0) {
//      double googlebotPercentage = (double) googlebotRequests / totalRequests * 100;
//     double yandexbotPercentage = (double) yandexbotRequests / totalRequests * 100;
//      System.out.printf("Доля запросов от Googlebot: %.2f%%\n", googlebotPercentage);
//      System.out.printf("Доля запросов от YandexBot: %.2f%%\n", yandexbotPercentage);
//   }


//private static boolean isBotRequest(String line) {
// Проверяем, содержит ли строка идентификаторы ботов
// return line.contains("Googlebot") || line.contains("YandexBot");
// }

// private static int[] parseLogLine(String line, int totalRequests) {
// int googlebotCount = 0;
//  int yandexbotCount = 0;

// String[] parts = line.split(" "); // Разделяем строку на части

// if (parts.length < 9) {
//      return new int[]{googlebotCount, yandexbotCount}; // Если строка не содержит достаточного количества данных, возвращаем нули
//   }

//  String ipAddr = parts[0]; // IP-адрес клиента
//  String time = parts[3] + " " + parts[4]; // Дата и время запроса
//  String method = parts[5].substring(1); // Метод запроса (GET, POST и т.д.)
//  String path = parts[6]; // Путь запроса
//   String responseCode = parts[8]; // Код HTTP-ответа
//  String responseSize = parts[9]; // Размер отданных данных в байтах
//   String referer = parts [10];
//  String userAgent = parts[11]; // User-Agent

// Поиск User-Agent в скобках
//  if (userAgent.startsWith("") && userAgent.endsWith("")) {
//      userAgent = userAgent.substring(1, userAgent.length() - 1);
//  }

//  String[] userAgentParts = userAgent.split("s*\\s*");
//   if (userAgentParts.length > 1) {
//       String firstBrackets = userAgentParts[1].split("\\)")[0]; // Часть в скобках

//       String[] agentParts = firstBrackets.split(";");
//     if (agentParts.length >= 2) {
//       String fragment = agentParts[1].trim(); // Второй фрагмент
//       String botName = fragment.split("/")[0].trim(); // Имя бота

// Подсчет запросов от Googlebot и YandexBot
//   if (botName.equalsIgnoreCase("Googlebot")) {
//        googlebotCount++;
//    } else if (botName.equalsIgnoreCase("YandexBot")) {
//       yandexbotCount++;
//     }
//   }
//  }
//   return new int[]{googlebotCount, yandexbotCount}; // Возвращаем количество запросов от ботов
//  }

// private static void printStatistics(Statistics statistics) {
// Выводим результаты статистики
//   System.out.println("Общий трафик: " + statistics.getTrafficRate() + " байт/час");
//  System.out.println("Среднее количество посещений в час: " + statistics.getAverageVisitsPerHour());
//  System.out.println("Среднее количество ошибочных запросов в час: " + statistics.getAverageErrorRequestsPerHour());
//  System.out.println("Среднее количество посещений одним пользователем: " + statistics.getAverageVisitsPerUser());




