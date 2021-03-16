package config;

import com.typesafe.config.Config;

public class CurrencyConverter {
    private final String hostname;
    private final int port;

    public CurrencyConverter(final Config config) {
        this.hostname = config.getString("hostname");
        this.port = config.getInt("port");
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }
}
