package com.ll.simpleDb;

import lombok.Setter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleDb {
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private Connection connection;
    @Setter
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
    public LocalDateTime selectDatetime(String sql) {
        return _run(sql, LocalDateTime.class);
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
                return parseResultSet(rs, cls);
            }

            setParams(stmt, params);
            return cls.cast(stmt.executeUpdate());

        } catch (SQLException e) {
            throw new RuntimeException("SQL 실행 실패: " + e.getMessage());
        }
    }

    private <T> T parseResultSet(ResultSet rs, Class<T> cls) throws SQLException {
        if(cls == Boolean.class) {
            rs.next();
            return cls.cast((rs.getBoolean(1)));
        }
        else if(cls == String.class){
            rs.next();
            return cls.cast(rs.getString(1));
        }
        else if(cls == Long.class){
            rs.next();
            return cls.cast(rs.getLong(1));
        }
        else if(cls == LocalDateTime.class){
            rs.next();
            return cls.cast(rs.getTimestamp(1).toLocalDateTime());
        }
        else if(cls == Map.class) {
            rs.next();
            return cls.cast(rsRowToMap(rs));

        } else if(cls == List.class) {
            List<Map<String, Object>> rows = new ArrayList<>();

            while(rs.next()) {
                rows.add(rsRowToMap(rs));
            }

            return cls.cast(rows);
        }

        throw new RuntimeException();
    }

    private Map<String, Object> rsRowToMap(ResultSet rs) throws SQLException {

        Map<String, Object> row = new HashMap<>();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String cname = metaData.getColumnName(i);
            row.put(cname, rs.getObject(i));
        }

        return row;
    }

    private void setParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]); // '?' 위치에 값 설정
        }
    }

    public Map<String, Object> selectRow(String sql) {
        return _run(sql, Map.class);
    }

    public List<Map<String, Object>> selectRows(String sql) {
        return _run(sql, List.class);
    }
}