package com.ll.simpleDb;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sql {

    private StringBuilder sqlBuilder;

    public Sql() {
        this.sqlBuilder = new StringBuilder();
    }

    public Sql append(String sqlLine) {
        sqlBuilder.append(sqlLine);
        return this;
    }

    public Sql append(String sqlLine, Object... args) {
        sqlBuilder.append(sqlLine);
        return this;
    }

    public long insert() {
        return 1;
    }
}
