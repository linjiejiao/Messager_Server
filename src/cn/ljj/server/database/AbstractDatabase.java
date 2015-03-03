
package cn.ljj.server.database;

import java.sql.*;
import java.util.Map;

public abstract class AbstractDatabase {
    private boolean inTransaction = false;

    public void beginTransaction() {
        inTransaction = true;
    }

    public void endTransaction() {
        inTransaction = false;
    }

    public abstract void setTransactionSuccessful();

    public boolean inTransaction() {
        return inTransaction;
    }

    public abstract boolean open(String location);
    
    public abstract void close();

    public abstract ResultSet query(String table, String[] columns, String selection,
            String[] selectionArgs, String groupBy, String having, String orderBy);

    public abstract ResultSet rawQuery(String sql, String[] selectionArgs);

    public abstract long insert(String table, Map<String, String> values);

    public abstract int delete(String table, String whereClause, String[] whereArgs);

    public abstract int update(String table, Map<String, String> values, String whereClause,
            String[] whereArgs);

    public abstract boolean executeSql(String sql, Object[] bindArgs);

}
