import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static javax.xml.bind.DatatypeConverter.parseDate;
import static sun.net.www.protocol.http.HttpURLConnection.userAgent;


public class Statistics {
    long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> listPages; // Хранит список всех существующих страниц сайта
    private HashSet<String> nonListPages; // Хранит список несуществующих страниц (404)
    private HashMap<String, Integer> osFrequency; // Хранит частоту встречаемости ОС
    private HashMap<String, Integer> browserStatistics; // Хранит статистику браузеров пользователей сайта

    private int totalVisits; // Общее количество посещений
    private int errorRequests; // Количество ошибочных запросов
    private HashSet<String> uniqueUsers; // Уникальные IP-адреса реальных пользователей
    private int totalHours; // Общее количество часов, за которые имеются записи

    private Map<Integer, Integer> visitsPerSecond; // Количество посещений в секунду
    private Map<String, Integer> userVisits; // Количество посещений для каждого пользователя (по IP)
    private Set<String> referrerDomains; // Список доменов рефереров
    private final String botIdentifier = "bot"; // Идентификатор бота в User-Agent
    private Set<LogEntry> logEntries; // Предполагается, что у вас есть коллекция LogEntry


    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX; // Инициализируем максимальным значением
        this.maxTime = LocalDateTime.MIN; // Инициализируем минимальным значением
        this.listPages = new HashSet<>(); // Инициализируем HashSet
        this.nonListPages = new HashSet<>(); // Инициализируем HashSet для несуществующих страниц
        this.osFrequency = new HashMap<>(); // Инициализируем HashMap
        this.browserStatistics = new HashMap<>(); // Инициализируем HashMap для браузеров
        this.totalHours = 0; // Изначально ноль, будет обновляться при добавлении записей


        this.totalVisits = 0;
        this.errorRequests = 0;
        this.uniqueUsers = new HashSet<>();

        this.visitsPerSecond = new HashMap<>();
        this.userVisits = new HashMap<>();
        this.referrerDomains = new HashSet<>();
        this.logEntries = new HashSet<>(); // Инициализация коллекции логов

    }

    // Метод для расчета пиковой посещаемости
    public int getPeakVisitsPerSecond() {
        int peakVisits = 0;
        for (int visits : visitsPerSecond.values()) {
            if (visits > peakVisits) {
                peakVisits = visits;
            }
        }
        return peakVisits;
    }

    // Метод для получения списка доменов рефереров
    public Set<String> getReferrerDomains() {
        Set<String> referrerDomains = new HashSet<>();

        for (LogEntry entry : logEntries) { // Предполагается, что logEntries - это ваша коллекция логов
            String referer = entry.getReferer();
            String domain = extractDomainFromReferer(referer);
            if (!domain.isEmpty()) {
                referrerDomains.add(domain);
            }
        }

        return referrerDomains;
    }

    // Метод для извлечения домена из реферера
    private String extractDomainFromReferer(String url) {
        try {
            // Удаляем протокол и путь
            String domain = url.split("/")[2];
            // Удаляем порт, если есть
            return domain.split(":")[0];
        } catch (Exception e) {
            return null;
        }
    }
    public Set<String> getReferringDomains() {
        return new HashSet<>(referrerDomains);
    }

    // Метод для расчета максимальной посещаемости одним пользователем
    public int getMaxVisitsByUser() {
        int maxVisits = 0;
        for (int visits : userVisits.values()) {
            if (visits > maxVisits) {
                maxVisits = visits;
            }
        }
        return maxVisits;
    }

    // Метод для проверки, является ли пользователь ботом
    private boolean isBot(String userAgent) {
        return userAgent != null && userAgent.toLowerCase().contains(botIdentifier);
    }

    private LocalDateTime parseDate(String dateString) {
        return LocalDateTime.parse(dateString, java.time.format.DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z"));
    }

    public void addErrorRequest() {
        errorRequests++;
    }

    public void addOS(String os) {
        osFrequency.put(os, osFrequency.getOrDefault(os, 0) + 1);
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
            listPages.add(logEntry.getPath());

                // Добавляем логику для учета посещений
                String ipAddr = logEntry.getIpAddr();
                String referer = logEntry.getReferer();
            LocalDateTime time = logEntry.getDateTime();

            // Преобразуем LocalDateTime в Unix-время (в секундах)
            int timeInSeconds = (int) time.toEpochSecond(ZoneOffset.UTC);


            // Проверка на бота
                if (!userAgent.isBot()) {
                    uniqueUsers.add(ipAddr); // Добавляем уникального пользователя
                    visitsPerSecond.put((timeInSeconds), visitsPerSecond.getOrDefault(timeInSeconds, 0) + 1); // Обновляем статистику по времени
                    userVisits.put(ipAddr, userVisits.getOrDefault(ipAddr, 0) + 1); // Увеличиваем количество посещений для уникального пользователя
                }
        } else if (logEntry.getResponseCode() == 404 || logEntry.getResponseCode() >= 400) {
            nonListPages.add(logEntry.getPath()); // Добавляем URL несуществующей страницы
            errorRequests++; // Увеличиваем количество ошибочных запросов
        }

        if (logEntry.getReferer() != null && !logEntry.getReferer().isEmpty()) {
            String domain = extractDomainFromReferer(logEntry.getReferer());
            if (domain != null) {
                referrerDomains.add(domain);
            }
        }

        // Обрабатываем операционную систему
        String os = logEntry.getUserAgent().getOperatingSystem();
        osFrequency.put(os, osFrequency.getOrDefault(os, 0) + 1);

        //Обрабатываем браузер
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

    public HashMap<String, Double> getOSDistribution() {
        HashMap<String, Double> osDistribution = new HashMap<>();
        int totalOSCount = osFrequency.values().stream().mapToInt(Integer::intValue).sum(); // Суммируем все значения

        for (String os : osFrequency.keySet()) {
            double proportion = (double) osFrequency.get(os) / totalOSCount; // Рассчитываем долю
            osDistribution.put(os, proportion);
        }

        return osDistribution;
    }

    // Возвращаем среднее количество посещений за час
    public double averageVisitsPerHour() {
        if (minTime.equals(LocalDateTime.MAX) || maxTime.equals(LocalDateTime.MIN)) {
            return 0; // Если нет записей, возвращаем 0
        }

        long hoursDifference = java.time.Duration.between(minTime, maxTime).toHours();
        if (hoursDifference == 0) {
            return totalVisits; // Если разница менее часа, возвращаем общее количество посещений
        }

        return (double) totalVisits / hoursDifference;
    }

    // Возвращаем среднее количество ошибочных запросов за час
    public double averageErrorsPerHour() {
        if (minTime.equals(LocalDateTime.MAX) || maxTime.equals(LocalDateTime.MIN)) {
            return 0; // Если нет записей, возвращаем 0
        }

        long hoursDifference = java.time.Duration.between(minTime, maxTime).toHours();
        if (hoursDifference == 0) {
            return errorRequests; // Если разница менее часа, возвращаем общее количество ошибочных запросов
        }
        return (double) errorRequests / hoursDifference;
    }

    // Среднее количество посещений на уникальный IP
    public double averageVisitsPerUniqueUser() {
        if (uniqueUsers.isEmpty()) {
            return 0; // Если нет уникальных пользователей, возвращаем 0
        }

        return (double) totalVisits / uniqueUsers.size();
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