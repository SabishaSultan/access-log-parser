import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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

    }

    public void addTraffic(long bytes) {
        totalTraffic += bytes;
    }

    // Метод для добавления посещения
    public void addVisit(String userAgent, String ipAddr, String referrer, int time) {
        if (!isBot(userAgent)) {
            // Увеличиваем количество посещений за эту секунду
            visitsPerSecond.put(time, visitsPerSecond.getOrDefault(time, 0) + 1);

            // Увеличиваем количество посещений для данного пользователя
            userVisits.put(ipAddr, userVisits.getOrDefault(ipAddr, 0) + 1);

            // Добавляем домен реферера
            if (referrer != null) {
                String domain = extractDomain(referrer);
                referrerDomains.add(domain);
            }
        }
    }

    // Метод для извлечения домена из URL
    private String extractDomain(String url) {
        try {
            String domain = url.split("/")[2]; // Извлекаем домен из URL
            return domain.startsWith("www.") ? domain.substring(4) : domain; // Убираем www.
        } catch (ArrayIndexOutOfBoundsException e) {
            return ""; // Если не удалось извлечь домен, возвращаем пустую строку
        }
    }

    // Метод для расчета пиковой посещаемости сайта в секунду
    public int getPeakVisitsPerSecond() {
        return visitsPerSecond.values().stream()
                .max(Integer::compare)
                .orElse(0);
    }

    // Метод для получения списка доменов рефереров
    public Set<String> getReferrerDomains() {
        return referrerDomains;
    }

    // Метод для расчета максимальной посещаемости одним пользователем
    public int getMaxVisitsByUser() {
        return userVisits.values().stream()
                .max(Integer::compare)
                .orElse(0);
    }

    // Метод для проверки, является ли пользователь ботом
    private boolean isBot(String userAgent) {
        return userAgent != null && userAgent.toLowerCase().contains(botIdentifier);
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
            if (!userAgent.isBot()) { // Проверяем, не является ли это ботом
                uniqueUsers.add(logEntry.getPath()); // Добавляем URL реального пользователя
            }
        } else if (logEntry.getResponseCode() == 404 || logEntry.getResponseCode() >= 400) {
            nonListPages.add(logEntry.getPath()); // Добавляем URL несуществующей страницы
            errorRequests++; // Увеличиваем количество ошибочных запросов
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