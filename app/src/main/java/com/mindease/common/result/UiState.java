package com.mindease.common.result;

public abstract class UiState<T> {
    private UiState() {}

    public static final class Loading<T> extends UiState<T> {
    }

    public static final class Success<T> extends UiState<T> {
        public final T data;

        public Success(T data) {
            this.data = data;
        }
    }

    public static final class Error<T> extends UiState<T> {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
