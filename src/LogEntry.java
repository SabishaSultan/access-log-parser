import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String paths;
    private final int responseCode;
    private final Long responseSize;
    private final String referer;
    private final UserAgent agent;


    public LogEntry(String line) {
        Pattern pattern = Pattern.compile(
                "^(\\S+) "                             // IP-address
                + "- - "                       // Fixed dashes
                + "\\[([\\w:/]+\\s[+\\-]\\d{4})\\] "  // Date and time - corrected
                + "\"([A-Z]+) ([^ ]*) HTTP/[^\"]*\" "  // Method and path
                + "(\\d{3}|-) "                  // Response code
                + "(\\d+|-) "                    // Size of data
                + "\"([^\"]*)\" "             // Referrer
                + "\"([^\"]*)\""              // User-Agent
        );
        String lines = Arrays.toString(line.split("\n"));
        Matcher matcher = pattern.matcher(lines.trim());
        if (!matcher.find()) {
            System.err.println("Error parsing log line: " + line);
            throw new IllegalArgumentException("Invalid log format for line: " + line);
        }
        this.ipAddr = matcher.group(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z").withLocale(Locale.ENGLISH);;
        this.time = LocalDateTime.parse(matcher.group(2), formatter);
        this.method = HttpMethod.valueOf(matcher.group(3));
        this.paths = matcher.group(4);
        String responseCodeStr = matcher.group(5);
        this.responseCode = responseCodeStr.equals("-") ? 0 : Integer.parseInt(responseCodeStr); // Устанавливаем 0 по умолчанию
        String responseSizeStr = matcher.group(6);
        this.responseSize = responseSizeStr.equals("-") ? 0 : Long.parseLong(responseSizeStr); // Устанавливаем 0 по умолчанию
        this.referer = matcher.group(7).equals("-") ? "no referrer" : matcher.group(7);;
        this.agent = new UserAgent(matcher.group(8).equals("-") ? "unknown agent" : matcher.group(8));
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
        return paths;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getDataSize() {
        return Math.toIntExact(responseSize);
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
