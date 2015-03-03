
package cn.ljj.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

import cn.ljj.server.log.Log;

public class SqliteDatabase extends AbstractDatabase {
    public static final String TAG = "SqliteDatabase";
    private Connection mConnection = null;
    private Statement mStatement = null;

    private boolean isDebugSQL(){
        return true;
    }
  
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
            // delete the last", "
            sql.delete(sql.length() - 2, sql.length());
        }
        sql.append(" from ").append(table);
        if (selection != null) {
            sql.append(" where ");
            sql.append(fillArgs(selection, selectionArgs));
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
            if( isDebugSQL()){
            Log.i(TAG, "query full sql=" + sql.toString());
            }
            rs = mStatement.executeQuery(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    @Override
    public ResultSet rawQuery(String sql, String[] selectionArgs) {
        ResultSet rs = null;
        try {
            String fullSql = fillArgs(sql, selectionArgs);
            if( isDebugSQL()){
                Log.i(TAG, "rawQuery full sql=" + fullSql);
            }
            rs = mStatement.executeQuery(fullSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    private String fillArgs(String selection, String[] selectionArgs) {
        StringBuilder builder = new StringBuilder();
        if (selectionArgs == null || selectionArgs.length == 0) {
            builder.append(selection);
        } else {
            boolean appendSpace = false;
            if (selection.charAt(selection.length() - 1) == '?') {
                selection += " ";
                appendSpace = true;
            }
            String[] sels = selection.split("\\?");
            if (sels == null || sels.length <= 1) {
                builder.append(selection);
            } else {
                int length = Math.min(sels.length - 1, selectionArgs.length);
                for (int i = 0; i < length; i++) {
                    builder.append(sels[i]).append(selectionArgs[i]);
                }
                if (!appendSpace) {
                    builder.append(sels[length]);
                }
            }
        }
        return builder.toString();
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
        // delete the last", "
        keyString.delete(keyString.length() - 2, keyString.length()); 
        keyString.append(") ");
        valueString.delete(valueString.length() - 2, valueString.length());
        valueString.append(");");
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(table);
        sql.append(keyString);
        sql.append("values");
        sql.append(valueString);
        try {
            if( isDebugSQL()){
                Log.i(TAG, "insert full sql=" + sql.toString());
            }
            ret = mStatement.executeUpdate(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        int ret = -1;
        StringBuilder sql = new StringBuilder("delete from ");
        sql.append(table);
        if (whereClause != null) {
            sql.append(" where ");
            sql.append(fillArgs(whereClause, whereArgs));
        }
        try {
            if( isDebugSQL()){
                Log.i(TAG, "delete full sql=" + sql.toString());
            }
            ret = mStatement.executeUpdate(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public int update(String table, Map<String, String> values, String whereClause,
            String[] whereArgs) {
        int ret = -1;
        StringBuilder sql = new StringBuilder("update ");
        sql.append(table);
        sql.append(" set ");
        Set<String> keySet = values.keySet();
        for(String key : keySet){
            sql.append(key);
            sql.append('=');
            sql.append(values.get(key));
            sql.append(", ");
        }
        // delete the last", "
        sql.delete(sql.length() - 2, sql.length());
        if (whereClause != null) {
            sql.append(" where ");
            sql.append(fillArgs(whereClause, whereArgs));
        }
        try {
            if( isDebugSQL()){
                Log.i(TAG, "update full sql=" + sql.toString());
            }
            ret = mStatement.executeUpdate(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public boolean executeSql(String sql, Object[] bindArgs) {
        boolean ret = false;
        try {
            String fullSql = fillArgs(sql, bindArgs);
            if( isDebugSQL()){
                Log.i(TAG, "rawQuery full sql=" + fullSql);
            }
            ret = mStatement.execute(fullSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
