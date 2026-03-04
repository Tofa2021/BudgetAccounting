package org.example;

public class Main {
    public static void main(String[] args) {
        ConnectionManager connectionManager = new ConnectionManager();
        Controller controller = new Controller();
        HibernateUtils.getSessionFactory();

        new Thread(() -> connectionManager.start(controller)).start();
    }
}
