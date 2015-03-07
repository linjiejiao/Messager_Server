
package cn.ljj.server.database;

import java.util.HashMap;
import java.util.Map;

import cn.ljj.server.config.Config;
import cn.ljj.server.database.TableDefines.*;

public class DatabaseFactory {
    public static final int DATABASE_TYPE_SQLITE = 0;
    public static final int DATABASE_TYPE_BAIDU_MYSQL = 1;


    public static AbstractDatabase getDatabase() {
        try {
            switch (Config.DATABASE_TYPE) {
                case DATABASE_TYPE_SQLITE:
                    AbstractDatabase db = new SqliteDatabase();
                    db.open(Config.DATABASE_LOCATION);
                    return db;
                case DATABASE_TYPE_BAIDU_MYSQL:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void crateTables(AbstractDatabase db) {
        for(String sql : TableDefines.INITIAL_SQLS){
            db.executeSql(sql, null);
        }
    }
    
    public static void initDatabaseData(AbstractDatabase db){
        //admin
        Map <String, Object> values = new HashMap<String, Object>();
        values.put(AdminColunms.NAME, "admin");
        values.put(AdminColunms.PASSWORD, "admin");
        values.put(AdminColunms.IDENTITY, 88888);
        db.insert(AdminColunms.TABLE_NAME, values);
        //users
        values = new HashMap<String, Object>();
        values.put(UserColunms.IDENTITY, 123);
        values.put(UserColunms.NAME, "name_123");
        values.put(UserColunms.PASSWORD, "123");
        db.insert(UserColunms.TABLE_NAME, values);
        values = new HashMap<String, Object>();
        values.put(UserColunms.IDENTITY, 456);
        values.put(UserColunms.NAME, "name_456");
        values.put(UserColunms.PASSWORD, "456");
        db.insert(UserColunms.TABLE_NAME, values);
        values = new HashMap<String, Object>();
        values.put(UserColunms.IDENTITY, 789);
        values.put(UserColunms.NAME, "name_789");
        values.put(UserColunms.PASSWORD, "789");
        db.insert(UserColunms.TABLE_NAME, values);
        values = new HashMap<String, Object>();
        values.put(UserColunms.IDENTITY, 888);
        values.put(UserColunms.NAME, "abc");
        values.put(UserColunms.PASSWORD, "abc");
        db.insert(UserColunms.TABLE_NAME, values);
        //no message
    }
}
