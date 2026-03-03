package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class User {
    private long id;
    private Set<Role> roles;
}
