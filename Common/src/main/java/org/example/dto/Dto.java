package org.example.dto;

import java.io.Serializable;

public class Dto implements Serializable {
    private String text;

    public Dto(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
