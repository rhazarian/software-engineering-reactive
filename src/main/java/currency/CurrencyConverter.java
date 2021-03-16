package currency;

import model.Currency;
import rx.Observable;

public interface CurrencyConverter {
    public Observable<Double> convert(double price, Currency from, Currency to);
}
