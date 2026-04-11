package com.mindease.common.result;

public interface DataCallback<T> {
    void onSuccess(T data);

    void onError(String message);
}
