import java.time.LocalDateTime;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX; // Инициализируем максимальным значением
        this.maxTime = LocalDateTime.MIN; // Инициализируем минимальным значением
    }

    public void addEntry(LogEntry logEntry) {
        totalTraffic += logEntry.getDataSize();

        if (logEntry.getDateTime().isBefore(minTime)) {
            minTime = logEntry.getDateTime();
        }

        if (logEntry.getDateTime().isAfter(maxTime)) {
            maxTime = logEntry.getDateTime();
        }
    }
    public double getTrafficRate() {
        long hoursDifference = java.time.Duration.between(minTime, maxTime).toHours();

        if (hoursDifference == 0) { // Если разница в часах равна нулю, чтобы избежать деления на ноль
            return totalTraffic; // Возвращаем общий трафик, если нет разницы во времени
        }

        return (double) totalTraffic / hoursDifference; // Возвращаем средний трафик
    }
}