package model;

public class User {
    private long id;
    private Currency currency;

    public User(long id, Currency currency) {
        this.id = id;
        this.currency = currency;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
