package com.mindease.domain.model;

public class Suggestion {
    public final String id;
    public final String text;
    public final String type;

    public Suggestion(String id, String text, String type) {
        this.id = id;
        this.text = text;
        this.type = type;
    }
}
