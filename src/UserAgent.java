public class UserAgent {
    private final String operatingSystem;
    private final String browser;

    public UserAgent(String userAgentString) {
        this.operatingSystem = extractOperatingSystem(userAgentString);
        this.browser = extractBrowser(userAgentString);
    }

    private String extractOperatingSystem(String userAgentString) {
        if (userAgentString.contains("Windows")) {
            return "Windows";
        } else if (userAgentString.contains("macOS")) {
            return "macOS";
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

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getBrowser() {
        return browser;
    }
}
