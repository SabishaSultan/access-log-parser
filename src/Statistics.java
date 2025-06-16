import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> listPages; // Хранит список всех существующих страниц сайта
    private HashSet<String> nonListPages; // Хранит список несуществующих страниц (404)
    private HashMap<String, Integer> osStatistics; // Хранит статистику операционных систем пользователей сайта
    private HashMap<String, Integer> browserStatistics; // Хранит статистику браузеров пользователей сайта

    // Новые поля для подсчета
    private int totalVisits; // Общее количество посещений
    private int errorRequests; // Количество ошибочных запросов
    private HashSet<String> uniqueUsers; // Уникальные IP-адреса реальных пользователей
    private int totalHours; // Общее количество часов, за которые имеются записи


    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX; // Инициализируем максимальным значением
        this.maxTime = LocalDateTime.MIN; // Инициализируем минимальным значением
        this.listPages = new HashSet<>(); // Инициализируем HashSet
        this.nonListPages = new HashSet<>(); // Инициализируем HashSet для несуществующих страниц
        this.osStatistics = new HashMap<>(); // Инициализируем HashMap
        this.browserStatistics = new HashMap<>(); // Инициализируем HashMap для браузеров
        this.totalHours = 0; // Изначально ноль, будет обновляться при добавлении записей

        // Инициализация новых полей
        this.totalVisits = 0;
        this.errorRequests = 0;
        this.uniqueUsers = new HashSet<>();

    }

    public void addTraffic(long bytes) {
        totalTraffic += bytes;
    }

    public void addVisit(String userAgent, String ip) {
        totalVisits++;

        // Проверяем, является ли запрос от бота
        if (!userAgent.toLowerCase().contains("bot")) {
            uniqueUsers.add(ip); // Добавляем уникальный IP, если это не бот
        }
    }

    public void addErrorRequest() {
        errorRequests++;
    }

    public void addOS(String os) {
        osStatistics.put(os, osStatistics.getOrDefault(os, 0) + 1);
    }

    public void incrementHours() {
        totalHours++; // Увеличиваем количество часов
    }

    public void addEntry(LogEntry logEntry) {
        totalTraffic += logEntry.getDataSize();

        if (logEntry.getDateTime().isBefore(minTime)) {
            minTime = logEntry.getDateTime();
        }

        if (logEntry.getDateTime().isAfter(maxTime)) {
            maxTime = logEntry.getDateTime();
        }
        UserAgent userAgent = logEntry.getUserAgent(); // Получаем объект UserAgent
        // Проверяем код ответа и добавляем страницу
        if (logEntry.getResponseCode() == 200) {
            totalVisits++; // Увеличиваем общее количество посещений
            listPages.add(logEntry.getIpAddr());
            if (!userAgent.isBot()) { // Проверяем, не является ли это ботом
                uniqueUsers.add(logEntry.getIpAddr()); // Добавляем уникальный IP-адрес реального пользователя
            }
        } else if (logEntry.getResponseCode() == 404 || logEntry.getResponseCode() >= 400) {
            nonListPages.add(logEntry.getIpAddr()); // Добавляем URL несуществующей страницы
            errorRequests++; // Увеличиваем количество ошибочных запросов
        }

        // Обрабатываем операционную систему
        String os = logEntry.getUserAgent().getOperatingSystem();
        osStatistics.put(os, osStatistics.getOrDefault(os, 0) + 1);
        // Обрабатываем браузер
        String browser = logEntry.getUserAgent().getBrowser();
        browserStatistics.put(browser, browserStatistics.getOrDefault(browser, 0) + 1);
    }

    public double getTrafficRate() {
        long hoursDifference = java.time.Duration.between(minTime, maxTime).toHours();

        if (hoursDifference == 0) { // Если разница в часах равна нулю, чтобы избежать деления на ноль
            return totalTraffic; // Возвращаем общий трафик, если нет разницы во времени
        }

        return (double) totalTraffic / hoursDifference; // Возвращаем средний трафик
    }

    public double getAverageVisitsPerHour() {
        return totalHours > 0 ? (double) totalVisits / totalHours : 0; // Возвращаем среднее количество посещений за час
    }

    public double getAverageErrorRequestsPerHour() {
        return totalHours > 0 ? (double) errorRequests / totalHours : 0; // Возвращаем среднее количество ошибочных запросов за час
    }

    public double getAverageVisitsPerUser() {
        return uniqueUsers.size() > 0 ? (double) totalVisits / uniqueUsers.size() : 0; // Среднее количество посещений на уникальный IP
    }

    public HashSet<String> getListPages() {
        return listPages; // Возвращаем набор существующих страниц
    }

    public HashSet<String> getNonListPages() {
        return nonListPages; // Возвращаем набор несуществующих страниц
    }

    public HashMap<String, Double> getBrowserDistribution() {
        HashMap<String, Double> browserDistribution = new HashMap<>();
        int totalBrowserCount = browserStatistics.values().stream().mapToInt(Integer::intValue).sum(); // Суммируем все значения
        for (String browser : browserStatistics.keySet()) {
            double proportion = (double) browserStatistics.get(browser) / totalBrowserCount; // Рассчитываем долю
            browserDistribution.put(browser, proportion);
        }

        return browserDistribution;
    }
}