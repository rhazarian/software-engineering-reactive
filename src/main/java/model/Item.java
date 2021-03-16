package model;

public class Item {
    private long id;
    private String name;
    private double price;
    private Currency currency;

    public Item(long id, String name, double price, Currency priceCurrency) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.currency = priceCurrency;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price, Currency priceCurrency) {
        this.price = price;
        this.currency = priceCurrency;
    }

    public Currency getPriceCurrency() {
        return currency;
    }
}
