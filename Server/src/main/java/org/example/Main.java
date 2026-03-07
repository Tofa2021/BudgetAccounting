package org.example;

import org.example.connection.ConnectionManager;
import org.example.util.Hibernate;

public class Main {
    public static void main(String[] args) {
        ConnectionManager connectionManager = new ConnectionManager();
        Controller controller = new Controller();
        Hibernate.getSessionFactory();

        new Thread(() -> connectionManager.start(controller)).start();
    }
}
