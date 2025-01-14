package com.ll.simpleDb;

import java.sql.*;

public class SimpleDb {
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private Connection connection;
    private boolean devMode = false;

    // 생성자: 데이터베이스 연결 정보 초기화
    public SimpleDb(String host, String user, String password, String dbName) {
        this.dbUrl = "jdbc:mysql://" + host + ":3306/" + dbName; // JDBC URL
        this.dbUser = user;                                    // 사용자 이름
        this.dbPassword = password;                            // 비밀번호

        // 연결 초기화
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            if (devMode) {
                System.out.println("데이터베이스에 성공적으로 연결되었습니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 연결 실패: " + e.getMessage());
        }
    }

    public String selectString(String sql) {
        return _run(sql, String.class);
    }

    public Long selectLong(String sql) {
        return _run(sql, Long.class);
    }

    public boolean selectBoolean(String sql) {
        System.out.println("sql : " + sql);
        return _run(sql, Boolean.class);
    }

    public void run(String sql, Object... params) {
        _run(sql, Integer.class , params);
    }

    public Sql genSql() {
        return new Sql(this);
    }

    private <T> T _run(String sql, Class<T> cls, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            if(sql.startsWith("SELECT")) {
                ResultSet rs = stmt.executeQuery(); // 실제 반영된 로우 수. insert, update, delete
                rs.next();
                if(cls == Boolean.class) return cls.cast((rs.getBoolean(1)));
                else if(cls == String.class) return cls.cast(rs.getString(1));
                else if(cls == Long.class) return cls.cast(rs.getLong(1));
            }

            setParams(stmt, params);

            return cls.cast(stmt.executeUpdate());

        } catch (SQLException e) {
            throw new RuntimeException("SQL 실행 실패: " + e.getMessage());
        }
    }

    private void setParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]); // '?' 위치에 값 설정
        }
    }

}