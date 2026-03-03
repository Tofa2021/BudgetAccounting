package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Operation implements Serializable {
    private int amount;
    private String type;
}
