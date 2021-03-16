package dao;

import model.Item;
import rx.Observable;

public interface ItemDao {
    Observable<Boolean> create(Item item);
    Observable<Item> get(long id);
}
