
package cn.ljj.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

public class SqliteDatabase extends AbstractDatabase {
    private Connection mConnection = null;
    private Statement mStatement = null;

    public SqliteDatabase() throws Exception {
        Class.forName("org.sqlite.JDBC");
    }

    @Override
    public void setTransactionSuccessful() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean open(String location) {
        try {
            mConnection = DriverManager.getConnection("jdbc:sqlite:" + location);
            mStatement = mConnection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        try {
            if (mStatement != null) {
                mStatement.close();
            }
            if (mConnection != null) {
                mConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet query(String table, String[] columns, String selection,
            String[] selectionArgs, String groupBy, String having, String orderBy) {
        StringBuilder sql = new StringBuilder("select ");
        if (columns == null || columns.length == 0) {
            sql.append("*");
        } else {
            for (String col : columns) {
                sql.append(col).append(", ");
            }
            sql.delete(sql.length() - 2, sql.length()); // delete the last ", "
        }
        sql.append(" from ").append(table);
        if (selection != null) {
            sql.append(" where ");
            if (selectionArgs == null || selectionArgs.length == 0) {
                sql.append(selection);
            } else {
                if (selection.charAt(selection.length() - 1) == '?') {
                    selection += " ";
                }
                String[] sels = selection.split("\\?");
                if (sels == null || sels.length <= 1) {
                    sql.append(selection);
                } else {
                    int length = sels.length - 1 < selectionArgs.length ? sels.length - 1
                            : selectionArgs.length;
                    for (int i = 0; i < length; i++) {
                        sql.append(sels[i]).append(selectionArgs[i]);
                    }
                    sql.append(sels[length]);
                }
            }
        }
        if (groupBy != null && !groupBy.isEmpty()) {
            sql.append(" group by ").append(groupBy);
        }
        if (having != null && !having.isEmpty()) {
            sql.append(" having ").append(having);
        }
        if (orderBy != null && !orderBy.isEmpty()) {
            sql.append(" order by ").append(orderBy);
        }
        sql.append(";");
        ResultSet rs = null;
        try {
            rs = mStatement.executeQuery(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    @Override
    public ResultSet rawQuery(String sql, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long insert(String table, Map<String, String> values) {
        long ret = -1;
        Set<String> keySet = values.keySet();
        if (keySet.size() == 0) {
            return ret;
        }
        StringBuilder keyString = new StringBuilder(" (");
        StringBuilder valueString = new StringBuilder(" (");
        for (String key : keySet) {
            String value = values.get(key);
            if (value == null) {
                continue;
            }
            keyString.append(key).append(", ");
            valueString.append(value).append(", ");
        }
        keyString.delete(keyString.length() - 2, keyString.length()); // delete
                                                                      // the
                                                                      // last
                                                                      // ", "
        keyString.append(") ");
        valueString.delete(keyString.length() - 2, keyString.length());
        valueString.append(");");
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(table);
        sql.append(keyString);
        sql.append("values");
        sql.append(valueString);
        try {
            ret = mStatement.executeUpdate(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(String table, Map<String, String> values, String whereClause,
            String[] whereArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int executeSql(String sql, Object[] bindArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
