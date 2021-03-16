import com.google.gson.JsonParser;
import com.mongodb.rx.client.MongoClients;
import com.typesafe.config.ConfigFactory;
import config.CurrencyConverter;
import config.DatabaseConfig;
import config.ServerConfig;
import currency.CurrencyConverterImpl;
import dao.MongoItemDao;
import dao.MongoUserDao;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.server.HttpServer;
import model.Currency;
import model.Item;
import model.User;
import rx.Observable;

import java.nio.file.Paths;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        final var config = ConfigFactory.parseFile(Paths.get("src/main/resources/application.conf").toFile());
        final var databaseConfig = new DatabaseConfig(config.getConfig("database"));
        final var serverConfig = new ServerConfig(config.getConfig("server"));
        final var converterConfig = new CurrencyConverter(config.getConfig("currency-converter"));
        final var database = MongoClients.create(databaseConfig.getUri()).getDatabase(databaseConfig.getName());
        final var userDao = new MongoUserDao(database);
        final var itemDao = new MongoItemDao(database);
        final var converter = new CurrencyConverterImpl(HttpClient.newClient(converterConfig.getHostname(), converterConfig.getPort()));
        HttpServer.newServer(serverConfig.getPort()).start((request, response) -> {
            try {
                Observable<String> result = switch (request.getDecodedPath().substring(1)) {
                    case "user": {
                        final var method = request.getHttpMethod();
                        if (method == HttpMethod.GET) {
                            final var id = Long.parseLong(request.getQueryParameters().get("id").get(0));
                            yield Observable.just(userDao.get(id).toString());
                        } else if (method == HttpMethod.POST) {
                            yield request.getContent().flatMap(bb -> {
                                final var parameters = JsonParser.parseString(new String(bb.array())).getAsJsonObject();
                                return userDao.create(new User(
                                        parameters.get("id").getAsInt(),
                                        Currency.valueOf(parameters.get("currency").getAsString())
                                ));
                            }).map(Objects::toString);
                        }
                    }
                    case "item": {
                        final var method = request.getHttpMethod();
                        if (method == HttpMethod.GET) {
                            final var userId = Long.parseLong(request.getQueryParameters().get("userId").get(0));
                            final var id = Long.parseLong(request.getQueryParameters().get("id").get(0));
                            yield userDao.get(userId).flatMap(
                                    user -> itemDao.get(id).flatMap(
                                            item -> converter.convert(item.getPrice(), item.getPriceCurrency(), user.getCurrency()).map(
                                                    price -> new Item(item.getId(), item.getName(), price, user.getCurrency())
                                            )
                                    )
                            ).map(Objects::toString);
                        } else if (method == HttpMethod.POST) {
                            yield request.getContent().flatMap(bb -> {
                                final var parameters = JsonParser.parseString(new String(bb.array())).getAsJsonObject();
                                return itemDao.create(new Item(
                                        parameters.get("id").getAsInt(),
                                        parameters.get("name").getAsString(),
                                        parameters.get("price").getAsDouble(),
                                        Currency.valueOf(parameters.get("currency").getAsString())
                                ));
                            }).map(Objects::toString);
                        }
                    }
                    default: {
                        yield Observable.just("wrong path");
                    }
                };
                response.writeString(result);
            } catch (final Exception ex) {
                response.writeString(Observable.just(ex.getMessage()));
            }
            return Observable.empty();
        }).awaitShutdown();
    }
}
