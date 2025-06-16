import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent agent;

    public LogEntry(String logLine) {
        String[] parts = logLine.split(" "); // Разделяем строку на части

        if (parts.length < 11) {
            throw new IllegalArgumentException("Недостаточно данных в строке лога: " + logLine);
        }

        this.ipAddr = parts[0];
        this.time = LocalDateTime.parse(parts[3].substring(1) + " " + parts[4].substring(0, parts[4].length() - 1),
                DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm:ss"));
        this.method = HttpMethod.valueOf(parts[5]); // Убираем начальный символ
        this.path = parts[6];
        this.responseCode = Integer.parseInt(parts[8]);
        this.responseSize = Integer.parseInt(parts[9]);
        this.referer = parts.length > 10 ? parts[10] : ""; // Если referer отсутствует
        this.agent = new UserAgent(parts.length > 11 ? parts[11] : ""); // Проверим длину массива
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getDateTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getDataSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return agent;
    }

    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD
    }
}
