import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;


public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> listPages; // Хранит список всех существующих страниц сайта
    private HashSet<String> nonListPages; // Хранит список несуществующих страниц (404)
    private HashMap<String, Integer> osStatistics; // Хранит статистику операционных систем пользователей сайта
    private HashMap<String, Integer> browserStatistics; // Хранит статистику браузеров пользователей сайта

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX; // Инициализируем максимальным значением
        this.maxTime = LocalDateTime.MIN; // Инициализируем минимальным значением
        this.listPages = new HashSet<>(); // Инициализируем HashSet
        this.nonListPages = new HashSet<>(); // Инициализируем HashSet для несуществующих страниц
        this.osStatistics = new HashMap<>(); // Инициализируем HashMap
        this.browserStatistics = new HashMap<>(); // Инициализируем HashMap для браузеров
    }

    public void addEntry(LogEntry logEntry) {
        totalTraffic += logEntry.getDataSize();

        if (logEntry.getDateTime().isBefore(minTime)) {
            minTime = logEntry.getDateTime();
        }

        if (logEntry.getDateTime().isAfter(maxTime)) {
            maxTime = logEntry.getDateTime();
        }
        // Проверяем код ответа и добавляем страницу
        if (logEntry.getResponseCode() == 200) {
            listPages.add(logEntry.getIpAddr());
        }else if (logEntry.getResponseCode() == 404) {
            nonListPages.add(logEntry.getIpAddr()); // Добавляем URL несуществующей страницы
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
    public HashMap<String, Double> getOSDistribution() {
        HashMap<String, Double> osDistribution = new HashMap<>();
        int totalOSCount = osStatistics.values().stream().mapToInt(Integer::intValue).sum(); // Суммируем все значения

        for (String os : osStatistics.keySet()) {
            double proportion = (double) osStatistics.get(os) / totalOSCount; // Рассчитываем долю
            osDistribution.put(os, proportion);
        }

        return osDistribution;
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