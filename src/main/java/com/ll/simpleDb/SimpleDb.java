package com.ll.simpleDb;

public class SimpleDb {
    private final String host;
    private final String username;
    private final String password;
    private final String dbName;

    // what is 'JDBC' ???

    public SimpleDb(String host, String username, String password, String dbName) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.dbName = dbName;
    }
    public void setDevMode(boolean b) {
    }
    public void run(String sql) {
        System.out.println(sql);
    }
}
