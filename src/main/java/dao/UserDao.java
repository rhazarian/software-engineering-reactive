package dao;

import model.User;

import rx.Observable;

public interface UserDao {
    Observable<Boolean> create(User user);
    Observable<User> get(long id);
}
