package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Status implements Serializable {
    OK(200);

    private final int status;
}
