
package cn.ljj.server.database;

import cn.ljj.server.config.Config;

public class DatabaseFactory {
    public static final int DATABASE_TYPE_SQLITE = 0;
    public static final int DATABASE_TYPE_BAIDU_MYSQL = 1;

    public static final String SQL_CRAETE_ADMIN_TABLE = "create table if not exists admin ("
            + Admin.IDENTITY +" integer primary key, "
            + Admin.NAME + " varchar, "
            + Admin.PASSWORD + " varchar, "
            + Admin.AUTHORITY + " varchar);";

    public static final String SQL_CRAETE_USER_TABLE = "create table if not exists user ("
            + User.IDENTITY +" integer primary key, "
            + User.NAME + " varchar, "
            + User.PASSWORD + " varchar);";

    public static final String SQL_CRAETE_MESSAGE_TABLE = "create table if not exists message ("
            + User.IDENTITY +" integer primary key, "
            + User.NAME + " varchar, "
            + User.PASSWORD + " varchar);";
    
    public static AbstractDatabase getDatabase() {
        try {
            switch (Config.DATABASE_TYPE) {
                case DATABASE_TYPE_SQLITE:
                    return new SqliteDatabase();
                case DATABASE_TYPE_BAIDU_MYSQL:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void crateTables(AbstractDatabase db) {
//        db.executeSql(sql, null);
    }
    
    public class Admin{
        public static final String IDENTITY = "identity";
        public static final String NAME = "name";
        public static final String PASSWORD = "password";
        public static final String AUTHORITY = "authority";
    }
    
    public class User{
        public static final String IDENTITY = "identity";
        public static final String NAME = "name";
        public static final String PASSWORD = "password";
    }
    
    public class Message{
        public static final String IDENTITY = "identity";
        public static final String DESTINATION = "destination";
        public static final String SOURCE = "source";
    }
}
