package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Response implements Serializable {
    private final Status status;
    private final Object body;
}
