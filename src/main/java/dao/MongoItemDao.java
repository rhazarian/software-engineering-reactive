package dao;

import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.MongoDatabase;
import model.Currency;
import model.Item;
import org.bson.Document;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.Map;

public class MongoItemDao implements ItemDao {
    private static final Scheduler scheduler = Schedulers.io();

    private final MongoDatabase database;

    public MongoItemDao(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public Observable<Boolean> create(Item item) {
        return get(item.getId()).singleOrDefault(null).flatMap(existingItem -> {
            if (existingItem != null) {
                return Observable.just(false);
            }
            return database.getCollection("items").insertOne(new Document(Map.of(
                    "id", item.getId(), "name", item.getName(), "price", item.getPrice(), "priceCurrency", item.getPriceCurrency().toString()
            ))).asObservable().isEmpty().map(empty -> !empty);
        }).subscribeOn(scheduler);
    }

    @Override
    public Observable<Item> get(long id) {
        return database.getCollection("items").find(Filters.eq("id", id)).toObservable().map(
                document -> new Item(id, document.getString("name"), document.getDouble("price"), Currency.valueOf(document.getString("priceCurrency")))
        ).subscribeOn(scheduler);
    }
}
