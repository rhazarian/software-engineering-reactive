package dao;

import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.MongoDatabase;
import model.Currency;
import model.User;
import org.bson.Document;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.Map;

public class MongoUserDao implements UserDao {
    private static final Scheduler scheduler = Schedulers.io();

    private final MongoDatabase database;

    public MongoUserDao(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public Observable<Boolean> create(User user) {
        return get(user.getId()).singleOrDefault(null).flatMap(existingUser -> {
            if (existingUser != null) {
                return Observable.just(false);
            }
            return database.getCollection("users").insertOne(new Document(Map.of(
                    "id", user.getId(), "currency", user.getCurrency().toString()
            ))).asObservable().isEmpty().map(empty -> !empty);
        }).subscribeOn(scheduler);
    }

    @Override
    public Observable<User> get(long id) {
        return database.getCollection("users").find(Filters.eq("id", id)).toObservable().map(
                document -> new User(id, Currency.valueOf(document.getString("currency")))
        ).subscribeOn(scheduler);
    }
}
