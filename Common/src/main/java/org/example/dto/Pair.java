package org.example.dto;

import java.io.Serializable;

public record Pair<T, V>(T first, V second) implements Serializable {
}
