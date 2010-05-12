package no.statnett.larm.core.async;

public interface AsyncCallback<T> {

    void onSuccess(T result);

    void onFailure(Throwable e);

}
