package config;

import com.typesafe.config.Config;

public class DatabaseConfig {
    private final String uri;
    private final String name;

    public DatabaseConfig(final Config config) {
        this.uri = config.getString("uri");
        this.name = config.getString("name");
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }
}
