package currency;

import com.google.gson.JsonParser;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import model.Currency;
import io.netty.buffer.ByteBuf;
import rx.Observable;

public class CurrencyConverterImpl implements CurrencyConverter {
    private final HttpClient<ByteBuf, ByteBuf> client;

    public CurrencyConverterImpl(HttpClient<ByteBuf, ByteBuf> client) {
        this.client = client;
    }

    @Override
    public Observable<Double> convert(double price, Currency from, Currency to) {
        return this.client.createGet(String.format("latest?base=%s&symbols=%s", from, to))
                .writeStringContent(Observable.just(""))
                .flatMap(HttpClientResponse::getContent)
                .map(bb -> {
                    final var object = JsonParser.parseString(new String(bb.array())).getAsJsonObject();
                    return price * object.get("rates").getAsJsonObject().get(to.toString()).getAsDouble();
                });
    }
}
