public class UserAgent {
    private final String operatingSystem;
    private final String browser;
    private final boolean isBot; // Новое поле для определения, является ли это ботом


    public UserAgent(String userAgentString) {
        this.operatingSystem = extractOperatingSystem(userAgentString);
        this.browser = extractBrowser(userAgentString);
        this.isBot = checkIfBot(userAgentString); // Определяем, является ли это ботом

    }

    private String extractOperatingSystem(String userAgentString) {
        if (userAgentString.contains("Windows")) {
            return "Windows";
        } else if (userAgentString.contains("Macintosh") || (userAgentString.contains("Intel Mac OS X")) ||
        (userAgentString.contains("iPad"))){
            return "Mac_OS";
        } else if (userAgentString.contains("Linux")) {
            return "Linux";
        }
        return "Unknown";
    }

    private String extractBrowser(String userAgentString) {
        if (userAgentString.contains("Chrome")) {
            return "Chrome";
        } else if (userAgentString.contains("Firefox")) {
            return "Firefox";
        } else if (userAgentString.contains("Edge")) {
            return "Edge";
        } else if (userAgentString.contains("Opera")) {
            return "Opera";
        }
        return "Other";
    }
    private boolean checkIfBot(String userAgentString) {
        return userAgentString.toLowerCase().contains("bot"); // Проверяем наличие слова "bot"
    }
    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getBrowser() {
        return browser;
    }
    public boolean isBot() {
        return isBot; // Метод для получения информации о том, является ли это ботом
    }
}
