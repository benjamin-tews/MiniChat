package de.uniks.pmws2021.chat.network.client;

import kong.unirest.Callback;
import kong.unirest.HttpResponse;
import kong.unirest.UnirestException;

@FunctionalInterface
public interface Response<T> extends Callback<T> {
    public void completed(HttpResponse<T> response);

    @Override
    public default void failed(UnirestException e) {
        e.printStackTrace();
    }

    @Override
    public default void cancelled() {
        System.err.println("This should not have happened");
    }
}
