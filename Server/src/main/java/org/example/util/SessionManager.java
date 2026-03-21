package org.example.util;

import org.hibernate.Session;

public interface SessionManager {
    Session openSession();
}
