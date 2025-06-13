import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;


public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> listPages; // Хранит список всех существующих страниц сайта
    private HashMap<String, Integer> osStatistics; // Хранит статистику операционных систем пользователей сайта

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX; // Инициализируем максимальным значением
        this.maxTime = LocalDateTime.MIN; // Инициализируем минимальным значением
        this.listPages = new HashSet<>(); // Инициализируем HashSet
        this.osStatistics = new HashMap<>(); // Инициализируем HashMap
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
        }

        // Обрабатываем операционную систему
        String os = logEntry.getUserAgent().getOperatingSystem();
        osStatistics.put(os, osStatistics.getOrDefault(os, 0) + 1);
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

    public HashSet<String> getListPagesPages() {
        return listPages; // Возвращаем набор существующих страниц
    }
}