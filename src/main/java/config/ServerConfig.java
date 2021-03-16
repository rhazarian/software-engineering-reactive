package config;

import com.typesafe.config.Config;

public class ServerConfig {
    private final int port;

    public ServerConfig(final Config config) {
        this.port = config.getInt("port");
    }

    public int getPort() {
        return port;
    }
}
